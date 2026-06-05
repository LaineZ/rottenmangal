package ru.bpm140.rottenmangal;
import java.util.ArrayList;
import java.util.List;

public class CPU {
    // Memory map
    private static final int MMIO_BASE = 0x10000000;
    private static final int BUS_CTRL = MMIO_BASE;
    private static final int BUS_ADDRESS_SELECT = MMIO_BASE + 0x4;
    private static final int BUS_DATA = MMIO_BASE + 0x8;
    private static final int TIMER_IRQ = 7;

    // CPU State
    int[] registers = new int[32];
    int pc = 0; // program counter
    int oldPc = 0; // old pc
    private Packet packet = null;
    public List<MemoryRegion> memory = new ArrayList<>();
    public InterruptController interruptController = new InterruptController();
    public Bus bus = new Bus();
    private CPUStatus.CPUState state = CPUStatus.CPUState.HALTED;
    private CPUStatus.ExceptionCause exceptionCause = CPUStatus.ExceptionCause.NONE;
    private int cycle = 0;

    private MemoryRegion findRegion(int addr) {
        for (MemoryRegion r : memory) {
            if (r.contains(addr)) return r;
        }
        return null;
    }

    public void restoreFromSnapshot(CPUSnapshot snapshot) {
        this.pc = snapshot.pc;
        this.registers = snapshot.registers.clone();
        this.interruptController.mip = snapshot.mip;
        this.interruptController.mie = snapshot.mie;
        this.interruptController.mtvec = snapshot.mtvec;
        this.interruptController.mepc = snapshot.mepc;
        this.interruptController.mcause = snapshot.mcause;
        this.interruptController.mtval = snapshot.mtval;
        this.interruptController.mstatus = snapshot.mstatus;
    }

    public CPUSnapshot takeSnapshot() {
        return new CPUSnapshot(this);
    }

    public CPUStatus.CPUState getState() {
        return state;
    }


    public CPUStatus.ExceptionCause getExceptionCause() {
        return exceptionCause;
    }

    public void setRunning() {
        if (state != CPUStatus.CPUState.RUNNING) {
            this.reset(false);
        }
    }

    public void reset(boolean off) {
        this.registers = new int[32];
        this.pc = 0;
        this.oldPc = 0;
        this.packet = null;
        this.interruptController = new InterruptController();
        if (!off) {
            this.state = CPUStatus.CPUState.RUNNING;
        } else {
            this.state = CPUStatus.CPUState.HALTED;
        }
        this.exceptionCause = CPUStatus.ExceptionCause.NONE;
        this.cycle = 0;
    }

    private void flatMemWrite(int addr, byte value) {
        MemoryRegion r = findRegion(addr);
        if (r == null) {
            return;
        }
        var offset = r.offset(addr);
        r.data[offset] = value;
    }

    public void loadELF(byte[] elf) throws IllegalArgumentException {
        if (elf[0] != 0x7F || elf[1] != 'E' || elf[2] != 'L' || elf[3] != 'F')
            throw new IllegalArgumentException("Not an ELF file");

        int e_type = readHalf(elf, 0x10);
        int e_machine = readHalf(elf, 0x12);
        int e_entry = readWord(elf, 0x18);
        int e_phoff = readWord(elf, 0x1C);
        int e_phnum = readHalf(elf, 0x2C);
        int e_phentsize = readHalf(elf, 0x2A);

        if (e_type != 2) { // 2 is binary
            throw new IllegalArgumentException("ELF file is not executable");
        }

        if (e_machine != 243) { // RISC-V have a 243 machine type
            throw new IllegalArgumentException("ELF file is not intended for RISC-V architecture");
        }

        for (int i = 0; i < e_phnum; i++) {
            int offset = e_phoff + i * e_phentsize;
            int p_type = readWord(elf, offset);
            if (p_type != 1) continue;

            int p_vaddr = readWord(elf, offset + 0x08);
            int p_filesz = readWord(elf, offset + 0x10);
            int p_memsz = readWord(elf, offset + 0x14);
            int p_offset = readWord(elf, offset + 0x04);

            for (int j = 0; j < p_filesz; j++) {
                flatMemWrite(p_vaddr + j, elf[p_offset + j]);
            }

            for (int j = p_filesz; j < p_memsz; j++) {
                flatMemWrite(p_vaddr + j, (byte) 0);
            }
        }

        pc = e_entry;
    }

    public int csrRead(int addr) {
        // TODO: Make some registry for CSR's?
        var value = interruptController.readCSR(addr);

        if (value.isPresent()) {
            return value.getAsInt();
        } else {
            return 0; // FIXME
        }
    }

    public void csrWrite(int addr, int value) {
        // TODO: Make some registry for CSR's?
        // TODO: Check for write
        interruptController.writeCSR(addr, value);
    }

    public Integer load8(int addr) {
        MemoryRegion r = findRegion(addr);
        if (r != null && r.read) {
            return r.data[r.offset(addr)] & 0xFF;
        }

        enterException(CPUStatus.ExceptionCause.LOAD_FAULT, addr);
        return null;
    }

    public Integer load16(int addr) {
        Integer b0 = load8(addr);
        if (b0 == null) return null;

        Integer b1 = load8(addr + 1);
        if (b1 == null) return null;

        return b0 | (b1 << 8);
    }

    public Integer load32(int addr) {
        Integer b0 = load8(addr);
        if (b0 == null) return null;

        Integer b1 = load8(addr + 1);
        if (b1 == null) return null;

        Integer b2 = load8(addr + 2);
        if (b2 == null) return null;

        Integer b3 = load8(addr + 3);
        if (b3 == null) return null;

        return b0 | (b1 << 8) | (b2 << 16) | (b3 << 24);
    }

    public boolean store8(int addr, int value) {
        // BUS CONTROL
        if (addr == BUS_CTRL) {
            // 0x0 - RESET
            if (value == 0) {
                packet = null;
            }

            if (value == 0x1 || value == 0x2) {
                packet = new Packet();
                packet.read = value == 0x1; // 0x1 - means read, 0x2 - writeCSR
            }

            // Send packet when CTL register == 3
            if (value == 0x3 && packet != null) {
                bus.send(packet);
                store8(BUS_CTRL, 0x0);
            }

            return true;
        }

        if (addr == BUS_ADDRESS_SELECT) {
            if (packet != null) {
                packet.destinationAddress = value;
            } else {
                enterException(CPUStatus.ExceptionCause.UNINITIALIZED_BUS_CONTROL, addr);
                return false;
            }
            return true;
        }

        MemoryRegion r = findRegion(addr);
        if (r != null && r.write) {
            r.data[r.offset(addr)] = (byte) value;
            return true;
        }

        enterException(CPUStatus.ExceptionCause.STORE_FAULT, addr);
        return false;
    }

    public void store16(int addr, int value) {
        if (!store8(addr, value)) {
            return;
        }

        store8(addr + 1, value >> 8);
    }

    public void store32(int addr, int value) {
        if (addr == BUS_DATA) {
            if (packet == null) {
                enterException(CPUStatus.ExceptionCause.UNINITIALIZED_BUS_CONTROL, addr);
                return;
            }

            if (packet.read) {
                enterException(CPUStatus.ExceptionCause.BUS_CONTROL_WRITE_ON_READ, addr);
                return;
            }

            packet.value = value;
            bus.send(packet);
            store8(BUS_CTRL, 0x0);
            return;
        }

        // Other
        if (!store8(addr, value)) {
            return;
        }
        if (!store8(addr + 1, value >> 8)) {
            return;
        }
        if (!store8(addr + 2, value >> 16)) {
            return;
        }
        if (!store8(addr + 3, value >> 24)) {
            return;
        }
    }

    void enterException(CPUStatus.ExceptionCause cause, int trapValue) {
        pc = interruptController.trap(oldPc, cause.getRiscVExceptionCode(), trapValue);
        exceptionCause = cause;
        System.out.printf("EXCEPTION=%08X value=%08X ExceptionPC=%08X HandlerPC=%08X CAUSE=%s\n",
                interruptController.mcause, trapValue, oldPc, pc, cause);
    }

    private void handleInterrupt() {
        int irq = interruptController.takeInterrupt();
        if (irq < 0) return;
        pc = interruptController.trap(pc, 0x80000000 | irq, 0);
    }

    /**
     * Steps CPU to one instruction cycle
     */
    public void step() {
        cycle++;

        if (cycle >= 10) {
            cycle = 0;
            interruptController.raise(TIMER_IRQ);
        }

        if (state != CPUStatus.CPUState.RUNNING) {
            return;
        }

        if (interruptController.hasInterrupt()) {
            handleInterrupt();
            return;
        }

        oldPc = pc;
        var instr = load32(pc);

        if (instr == null) {
            enterException(CPUStatus.ExceptionCause.INSTRUCTION_ACCESS_FAULT, pc);
            return;
        }

        pc += 4;
        int opcode = instr & 0x7F;

        switch (opcode) {
            // OP-IMM
            case 0x13 -> execOpImm(instr);
            // OP
            case 0x33 -> execOp(instr);
            // LOAD
            case 0x03 -> execLoad(instr);
            // STORE
            case 0x23 -> execStore(instr);
            // BRANCH
            case 0x63 -> execBranch(instr);
            // JAL
            case 0x6F -> execJal(instr);
            // JALR
            case 0x67 -> execJalr(instr);
            // LUI
            case 0x37 -> execLui(instr);
            // AUIPC
            case 0x17 -> execAuipc(instr);
            // SYSTEM
            case 0x73 -> {
                int funct3 = (instr >>> 12) & 0x7;
                int funct12 = (instr >>> 20) & 0xFFF;
                int rd = (instr >>> 7) & 0x1F;
                int rs1 = (instr >>> 15) & 0x1F;
                int csrAddr = (instr >>> 20) & 0xFFF;

                if (funct3 == 0) {
                    switch (funct12) {
                        case 0x302 -> {
                            pc = interruptController.mretPC();
                            return;
                        }

                        case 0x0 -> {
                            handleSyscall();
                            return;
                        }

                        case 0x1 -> {
                            enterException(CPUStatus.ExceptionCause.BREAKPOINT, 0);
                            return;
                        }

                        default -> {
                            enterException(CPUStatus.ExceptionCause.UNKNOWN_SYSCALL, instr);
                            return;
                        }
                    }
                }

                int imm = rs1;
                switch (funct3) {
                    case 0x1 -> {
                        int old = csrRead(csrAddr);
                        if (rd != 0) registers[rd] = old;
                        csrWrite(csrAddr, registers[rs1]);
                    }
                    case 0x2 -> {
                        int old = csrRead(csrAddr);
                        if (rd != 0) registers[rd] = old;
                        csrWrite(csrAddr, old | registers[rs1]);
                    }
                    case 0x3 -> {
                        int old = csrRead(csrAddr);
                        if (rd != 0) registers[rd] = old;
                        csrWrite(csrAddr, old & ~registers[rs1]);
                    }
                    case 0x5 -> {
                        int old = csrRead(csrAddr);
                        if (rd != 0) registers[rd] = old;
                        csrWrite(csrAddr, imm);
                    }
                    case 0x6 -> {
                        int old = csrRead(csrAddr);
                        if (rd != 0) registers[rd] = old;
                        csrWrite(csrAddr, old | imm);
                    }
                    case 0x7 -> {
                        int old = csrRead(csrAddr);
                        if (rd != 0) registers[rd] = old;
                        csrWrite(csrAddr, old & ~imm);
                    }

                    default -> enterException(CPUStatus.ExceptionCause.UNKNOWN_CSR_MODE, funct3);
                }
            }
            case 0xF -> {
                // FENCE, do nothing, at least for now...
            }

            case 0x0 -> {
                // NOP
            }

            default -> enterException(CPUStatus.ExceptionCause.UNKNOWN_OPCODE, opcode);
        }
        // x0 is always zero
        registers[0] = 0;
//
//        int v = load32(0x80001000);
//
//        if (v != 0) {
//            if (v == 1) {
//                System.out.println("PASS");
//            } else {
//                int testnum = v >> 1;
//                System.out.println("FAIL testnum=" + testnum + " raw=" + v);
//            }
//        }
    }

    private void debugPortWrite(int ptr, int len) {
        for (int i = 0; i < len; i++) {
            int ch = load8(ptr + i);
            System.out.print((char) ch); // TODO: Switch to something else
        }
    }

    private void handleSyscall() {
        // syscall arguments
        int a7 = registers[17];
        int a0 = registers[10];
        int a1 = registers[11];

        System.out.println("ECALL: " + a7 + " A0" + a0);

        switch (a7) {
            case 1 -> debugPortWrite(a0, a1);
            case 2 -> state = CPUStatus.CPUState.HALTED;
            // https://github.com/PhilippRados/ruscv/blob/master/src/cpu.rs#L162-L170
            case 93 -> {
                if (a0 == 0) {
                    state = CPUStatus.CPUState.HALT_TEST_PASS;
                } else {
                    state = CPUStatus.CPUState.HALTED;
                }
            }
            default -> System.out.printf("unknown ECALL called at %08X A7: %d\n", pc, a7);
        }
    }

    private void execOpImm(int instr) {
        int rd = (instr >> 7) & 0x1F;
        int funct3 = (instr >> 12) & 0x7;
        int rs1 = (instr >> 15) & 0x1F;
        int imm = signExtend(instr >> 20, 12);
        int shamt = imm & 0x1F;
        int funct7 = imm >>> 5;

        switch (funct3) {
            case 0x0 -> registers[rd] = registers[rs1] + signExtend(imm, 12); // ADDI
            case 0x2 -> registers[rd] = (registers[rs1] < imm) ? 1 : 0; // SLTI
            case 0x3 -> registers[rd] = (Integer.compareUnsigned(registers[rs1], imm) < 0) ? 1 : 0; // SLTIU
            case 0x4 -> registers[rd] = registers[rs1] ^ imm; // XORI
            case 0x6 -> registers[rd] = registers[rs1] | imm; // ORI
            case 0x7 -> registers[rd] = registers[rs1] & imm; // ANDI
            case 0x1 -> registers[rd] = registers[rs1] << shamt; // SLLI
            case 0x5 -> {
                if (funct7 == 0x00) {
                    registers[rd] = registers[rs1] >>> shamt; // SRLI
                } else if (funct7 == 0x20) {
                    registers[rd] = registers[rs1] >> shamt; // SRAI
                } else {
                    enterException(CPUStatus.ExceptionCause.BAD_SHIFT_OP_IMM, funct7);
                }
            }
            default -> enterException(CPUStatus.ExceptionCause.UNSUPPORTED_OP_IMM, funct3);
        }
    }

    void execOp(int instr) {
        int rd = (instr >> 7) & 0x1F;
        int funct3 = (instr >> 12) & 0x7;
        int rs1 = (instr >> 15) & 0x1F;
        int rs2 = (instr >> 20) & 0x1F;
        int funct7 = (instr >> 25) & 0x7F;

        if (funct7 == 0x01) {
            execOpMul(rd, funct3, rs1, rs2);
        } else {
            execOpInteger(rd, funct3, rs1, rs2, funct7);
        }
    }

    private void execOpMul(int rd, int funct3, int rs1, int rs2) {
        // RV32M
        switch (funct3) {
            case 0x0 -> registers[rd] = registers[rs1] * registers[rs2]; // MUL
            case 0x1 -> { // MULH
                long a = registers[rs1];
                long b = registers[rs2];
                registers[rd] = (int) ((a * b) >> 32);
            }
            case 0x2 -> { // MULHSU
                long a = registers[rs1];
                long b = Integer.toUnsignedLong(registers[rs2]);
                registers[rd] = (int) ((a * b) >> 32);
            }

            case 0x3 -> { // MULHU
                long a = Integer.toUnsignedLong(registers[rs1]);
                long b = Integer.toUnsignedLong(registers[rs2]);
                registers[rd] = (int) ((a * b) >> 32);
            }

            case 0x4 -> { // DIV
                if (registers[rs2] == 0) {
                    registers[rd] = -1;
                } else if (registers[rs1] == Integer.MIN_VALUE && registers[rs2] == -1) {
                    registers[rd] = Integer.MIN_VALUE;
                } else {
                    registers[rd] = registers[rs1] / registers[rs2];
                }
            }

            case 0x5 -> { // DIVU
                if (registers[rs2] == 0) {
                    registers[rd] = -1;
                } else {
                    registers[rd] = Integer.divideUnsigned(registers[rs1], registers[rs2]);
                }
            }

            case 0x6 -> { // REM
                if (registers[rs2] == 0) {
                    registers[rd] = registers[rs1];
                } else if (registers[rs1] == Integer.MIN_VALUE && registers[rs2] == -1) {
                    registers[rd] = 0;
                } else {
                    registers[rd] = registers[rs1] % registers[rs2];
                }
            }

            case 0x7 -> { // REMU
                if (registers[rs2] == 0) {
                    registers[rd] = registers[rs1];
                } else {
                    registers[rd] = Integer.remainderUnsigned(registers[rs1], registers[rs2]);
                }
            }
            default -> enterException(CPUStatus.ExceptionCause.UNSUPPORTED_OP, funct3);
        }
    }

    private void execOpInteger(int rd, int funct3, int rs1, int rs2, int funct7) {
        switch (funct3) {
            case 0x0 -> {
                if (funct7 == 0x00) {
                    registers[rd] = registers[rs1] + registers[rs2]; // ADD
                } else if (funct7 == 0x20) {
                    registers[rd] = registers[rs1] - registers[rs2]; // SUB
                } else {
                    enterException(CPUStatus.ExceptionCause.UNSUPPORTED_OP, funct3);
                }
            }
            case 0x1 -> registers[rd] = registers[rs1] << (registers[rs2] & 31); // SLL
            case 0x2 -> registers[rd] = (registers[rs1] < registers[rs2]) ? 1 : 0; // SLT
            case 0x3 -> registers[rd] = Integer.compareUnsigned(registers[rs1], registers[rs2]) < 0 ? 1 : 0; // SLTU
            case 0x4 -> registers[rd] = registers[rs1] ^ registers[rs2]; // XOR
            case 0x5 -> {
                if (funct7 == 0x00) {
                    registers[rd] = registers[rs1] >>> (registers[rs2] & 31); // SRL
                } else if (funct7 == 0x20) {
                    registers[rd] = registers[rs1] >> (registers[rs2] & 31); // SRA
                } else {
                    enterException(CPUStatus.ExceptionCause.UNSUPPORTED_OP, funct7);
                }
            }
            case 0x6 -> registers[rd] = registers[rs1] | registers[rs2]; // OR
            case 0x7 -> registers[rd] = registers[rs1] & registers[rs2]; // AND
            default -> enterException(CPUStatus.ExceptionCause.UNSUPPORTED_OP, funct3);
        }
    }

    private void execLoad(int instr) {
        int rd = (instr >> 7) & 0x1F;
        int funct3 = (instr >> 12) & 0x7;
        int rs1 = (instr >> 15) & 0x1F;
        int imm = signExtend((instr >> 20) & 0xFFF, 12);

        if (rd == 0) {
            return;
        }

        int addr = registers[rs1] + imm;

        switch (funct3) {
            case 0x0 -> {
                var load = load8(addr);
                if (load != null) {
                    registers[rd] = signExtend(load, 8);
                }
            }   // LB
            case 0x1 -> {
                var load = load16(addr);
                if (load != null) {
                    registers[rd] = signExtend(load, 16);
                }
            }  // LH
            case 0x2 -> {
                var load = load32(addr);
                if (load != null) {
                    registers[rd] = load;
                }
            } // LW
            case 0x4 -> {
                var load = load8(addr);
                if (load != null) {
                    registers[rd] = load & 0xFF;
                }
            } // LBU
            case 0x5 -> {
                var load = load16(addr);
                if (load != null) {
                    registers[rd] = load & 0xFFFF;
                }
            } // LHU
            default -> enterException(CPUStatus.ExceptionCause.UNSUPPORTED_LOAD, funct3);
        }

//        System.out.printf(
//                "LOAD: rs1=%d val=0x%08X imm=0x%08X addr=0x%08X\n",
//                rs1, x[rs1], imm, addr
//        );
    }

    private void execStore(int instr) {
        int funct3 = (instr >> 12) & 0x7;
        int rs1 = (instr >> 15) & 0x1F;
        int rs2 = (instr >> 20) & 0x1F;

        int imm =
                ((instr >> 7) & 0x1F)
                        | (((instr >> 25) & 0x7F) << 5);

        imm = signExtend(imm, 12);

        int addr = registers[rs1] + imm;

        switch (funct3) {
            case 0x0 -> store8(addr, registers[rs2]); // SB
            case 0x1 -> store16(addr, registers[rs2]); // SH
            case 0x2 -> store32(addr, registers[rs2]); // SW
            default -> enterException(CPUStatus.ExceptionCause.UNSUPPORTED_STORE, funct3);
        }

//        System.out.printf(
//                "STORE: rs1=%d val=0x%08X imm=0x%08X addr=0x%08X\n",
//                rs1, x[rs1], imm, addr
//        );
    }

    private void execBranch(int instr) {
        int funct3 = (instr >> 12) & 0x7;
        int rs1 = (instr >> 15) & 0x1F;
        int rs2 = (instr >> 20) & 0x1F;

        int imm =
                (((instr >> 31) & 1) << 12)
                        | (((instr >> 7) & 1) << 11)
                        | (((instr >> 25) & 0x3F) << 5)
                        | (((instr >> 8) & 0xF) << 1);

        imm = signExtend(imm, 13);

        boolean take = switch (funct3) {
            case 0x0 -> registers[rs1] == registers[rs2]; // BEQ
            case 0x1 -> registers[rs1] != registers[rs2]; // BNE
            case 0x4 -> registers[rs1] < registers[rs2]; // BLT
            case 0x5 -> registers[rs1] >= registers[rs2]; // BGE
            case 0x6 -> Integer.compareUnsigned(registers[rs1], registers[rs2]) < 0; // BLTU
            case 0x7 -> Integer.compareUnsigned(registers[rs1], registers[rs2]) >= 0; // BGEU
            default -> false;
        };


        if (take) {
            pc = oldPc + imm;
        }
        //System.out.printf("BRANCH pc=%08X take=%b\n", oldPc, take);
    }

    private void execJal(int instr) {
        int rd = (instr >> 7) & 0x1F;

        int imm =
                (((instr >> 31) & 1) << 20)
                        | (((instr >> 12) & 0xFF) << 12)
                        | (((instr >> 20) & 1) << 11)
                        | (((instr >> 21) & 0x3FF) << 1);

        imm = signExtend(imm, 21);

        registers[rd] = oldPc + 4;
        pc = oldPc + imm;
    }

    private void execJalr(int instr) {
        int rd = (instr >> 7) & 0x1F;
        int rs1 = (instr >> 15) & 0x1F;
        int imm = signExtend(instr >> 20, 12);
        int target = (registers[rs1] + imm) & ~1;

        registers[rd] = oldPc + 4;
        pc = target;
    }

    private void execLui(int instr) {
        int rd = (instr >> 7) & 0x1F;
        registers[rd] = instr & 0xFFFFF000;
    }

    private void execAuipc(int instr) {
        int rd = (instr >> 7) & 0x1F;
        registers[rd] = oldPc + (instr & 0xFFFFF000);
    }

    // utils
    private int signExtend(int value, int bits) {
        int shift = 32 - bits;
        return (value << shift) >> shift;
    }

    private int readHalf(byte[] data, int off) {
        return (data[off] & 0xFF) | ((data[off + 1] & 0xFF) << 8);
    }

    private int readWord(byte[] data, int off) {
        return (data[off] & 0xFF) | ((data[off + 1] & 0xFF) << 8) |
                ((data[off + 2] & 0xFF) << 16) | ((data[off + 3] & 0xFF) << 24);
    }
}