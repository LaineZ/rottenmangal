package ru.bpm140;

import java.io.IOException;
import java.util.Arrays;

public class CPU {
    private static final int RAM_BASE = 0x00000000;
    private static final int RAM_SIZE = 1*1024*1024;
    private static final int MMIO_BASE = 0x10000000;

    // CPU STATE
    public final int[] x = new int[32]; // registers
    public int pc = 0;
    public boolean running = true;
    int mepc = 0;
    int mcause = 0;
    int mtvec = 0x00000000;

    boolean mie = true;   // global interrupt enable
    boolean inTrap = false;
    int pendingInterrupts = 0;
    int cycle = 0;
    private byte[] ram = new byte[RAM_SIZE];

    private static final int BUS_CTRL = MMIO_BASE;
    private static final int BUS_ADDRESS_SELECT = MMIO_BASE + 0x4;
    private static final int BUS_DATA = MMIO_BASE + 0x8;
    private Packet packet = null;
    public Bus bus = new Bus();

    public int load8(int addr) {
        // RAM
        if (addr >= RAM_BASE && addr < RAM_BASE + RAM_SIZE) {
            return ram[addr - RAM_BASE] & 0xFF;
        }

        dumpRegisters();
        throw new RuntimeException(
                String.format("LOAD fault addr=0x%08X pc=0x%08X", addr, pc)
        );
    }

    public int load16(int addr) {
        return load8(addr)
                | (load8(addr + 1) << 8);
    }

    public int load32(int addr) {
        return load8(addr)
                | (load8(addr + 1) << 8)
                | (load8(addr + 2) << 16)
                | (load8(addr + 3) << 24);
    }

    public void store8(int addr, int value) {
        // BUS CONTROL
        if (addr == BUS_CTRL) {
            // 0x0 - RESET
            if (value == 0) {
                packet = null;
                return;
            }

            if (value == 0x1 || value == 0x2) {
                packet = new Packet();
                packet.read = value == 0x1; // 0x1 - means read, 0x2 - write
                return;
            }

            // Send packet when CTL register == 3
            if (value == 0x3 && packet != null) {
                bus.send(packet);
                store8(BUS_CTRL, 0x0);
                return;
            }
        }

        if (addr == BUS_ADDRESS_SELECT) {
            if (packet != null) {
                packet.destinationAddress = value;
                return;
            } else {
                dumpRegisters();
                throw new RuntimeException("BUS CONTROL is not initialized yet");
            }
        }

        // RAM
        if (addr >= RAM_BASE && addr < RAM_BASE + RAM_SIZE) {
            ram[addr - RAM_BASE] = (byte) value;
            return;
        }

        dumpRegisters();
        throw new RuntimeException(
                String.format("STORE fault addr=0x%08X pc=0x%08X", addr, pc)
        );
    }

    public void store16(int addr, int value) {
        store8(addr, value);
        store8(addr + 1, value >> 8);
    }

    public void store32(int addr, int value) {
        if (addr == BUS_DATA) {
            if (packet != null && !packet.read) {
                packet.value = value;
            } else if (packet == null) {
                dumpRegisters();
                throw new RuntimeException("BUS CONTROL not initialized for write to BUS_DATA");
            } else {
                dumpRegisters();
                throw new RuntimeException("Cannot write to BUS_DATA when read operation is set");
            }
            return;
        }

        // Other
        store8(addr, value);
        store8(addr + 1, value >> 8);
        store8(addr + 2, value >> 16);
        store8(addr + 3, value >> 24);
    }

    public void loadBinary(byte[] program, int address) {
        System.arraycopy(program, 0, ram, address, program.length);
        x[2] = RAM_BASE + RAM_SIZE - 0x1000;
        pc = address;
    }

    private int readHalf(byte[] data, int off) {
        return (data[off] & 0xFF) | ((data[off+1] & 0xFF) << 8);
    }
    private int readWord(byte[] data, int off) {
        return (data[off] & 0xFF) | ((data[off+1] & 0xFF) << 8) |
                ((data[off+2] & 0xFF) << 16) | ((data[off+3] & 0xFF) << 24);
    }

    public void loadELF(byte[] elf) throws IOException {
        if (elf[0] != 0x7F || elf[1] != 'E' || elf[2] != 'L' || elf[3] != 'F')
            throw new IllegalArgumentException("Not an ELF file");

        int e_type = readHalf(elf, 0x10);
        int e_machine = readHalf(elf, 0x12);
        int e_entry = readWord(elf, 0x18);
        int e_phoff = readWord(elf, 0x1C);
        int e_phnum = readHalf(elf, 0x2C);
        int e_phentsize = readHalf(elf, 0x2A);

        for (int i = 0; i < e_phnum; i++) {
            int offset = e_phoff + i * e_phentsize;
            int p_type = readWord(elf, offset + 0x00);
            if (p_type != 1) continue;

            int p_vaddr  = readWord(elf, offset + 0x08);
            int p_filesz = readWord(elf, offset + 0x10);
            int p_memsz  = readWord(elf, offset + 0x14);
            int p_offset = readWord(elf, offset + 0x04);

            if (p_vaddr + p_memsz > ram.length) {
                throw new RuntimeException("Segment exceeds RAM");
            }
            System.arraycopy(elf, p_offset, ram, p_vaddr, p_filesz);

            if (p_memsz > p_filesz) {
                Arrays.fill(ram, p_vaddr + p_filesz, p_vaddr + p_memsz, (byte)0);
            }
        }

        pc = e_entry;
        x[2] = RAM_BASE + RAM_SIZE - 0x1000;
    }

    private void handleInterrupt() {
        System.out.println("TRAP");
        inTrap = true;

        // save state
        mepc = pc;

        // find first pending interrupt
        int irq = Integer.numberOfTrailingZeros(pendingInterrupts);
        pendingInterrupts &= ~(1 << irq);
        System.out.printf("mtvec=%08X irq=%d\n", mtvec, irq);
        mcause = 0x80000000 | irq; // high bit = interrupt

        // jump to handler
        pc = mtvec + irq * 4;
    }

    public void step() {
        if (cycle % 1000 == 0) {
            //pendingInterrupts |= (1 << 0);
            cycle = 0;
        }

        if (mie && pendingInterrupts != 0 && !inTrap) {
            handleInterrupt();
            return;
        }

        int instr = load32(pc);
        int oldPc = pc;

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
            case 0x63 -> execBranch(instr, oldPc);
            // JAL
            case 0x6F -> execJal(instr, oldPc);
            // JALR
            case 0x67 -> execJalr(instr, oldPc);
            // LUI
            case 0x37 -> execLui(instr);
            // AUIPC
            case 0x17 -> execAuipc(instr, oldPc);
            // SYSTEM
            case 0x73 -> {
                int funct12 = (instr >> 20) & 0xFFF;

                if (funct12 == 0x302) {
                    pc = mepc;
                    inTrap = false;
                } else if (funct12 == 0x000) {
                    System.out.println("Ecal stub: " + funct12);
                } else {
                    System.out.println("Halt!");
                    running = false;
                }
            }

            default -> {
                System.err.printf(
                        "Unknown opcode 0x%02X at PC=0x%08X\n",
                        opcode,
                        oldPc
                );
                running = false;
            }
        }

        cycle += 1;
        // x0 is always zero
        x[0] = 0;
        if (opcode == 0x6F || opcode == 0x67) {
            //System.out.printf("JUMP pc=%08X -> %08X\n", oldPc, pc);
        }
//       System.out.printf("PC=%08X instr=%08X pending=%d inTrap=%b\n", pc, load32(pc), pendingInterrupts, inTrap);
    }

    private void execOpImm(int instr) {

        int rd = (instr >> 7) & 0x1F;
        int funct3 = (instr >> 12) & 0x7;
        int rs1 = (instr >> 15) & 0x1F;
        int imm = signExtend(instr >> 20, 12);
        int shamt = imm & 0x1F;
        int funct7 = imm >>> 5;

        switch (funct3) {

            case 0x0 -> x[rd] = x[rs1] + signExtend(imm, 12); // ADDI
            case 0x2 -> x[rd] = (x[rs1] < imm) ? 1 : 0; // SLTI
            case 0x3 -> x[rd] = (Integer.compareUnsigned(x[rs1], imm) < 0) ? 1 : 0; // SLTIU
            case 0x4 -> x[rd] = x[rs1] ^ imm; // XORI
            case 0x6 -> x[rd] = x[rs1] | imm; // ORI
            case 0x7 -> x[rd] = x[rs1] & imm; // ANDI
            case 0x1 -> x[rd] = x[rs1] << shamt; // SLLI
            case 0x5 -> {
                if (funct7 == 0x00) {
                    x[rd] = x[rs1] >>> shamt; // SRLI
                } else if (funct7 == 0x20) {
                    x[rd] = x[rs1] >> shamt; // SRAI
                } else {
                    System.err.println("Bad SHIFT OP-IMM");
                    running = false;
                }
            }

            default -> {
                System.err.println("Unsupported OP-IMM funct3=" + funct3);
                running = false;
            }
        }

//        System.out.printf(
//                "OP-IMM: rd=%d rs1=%d funct3=%X imm=%X instr=%08X\n",
//                rd, rs1, funct3, imm, instr
//        );
    }

    private void execOp(int instr) {
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
            case 0x0 -> x[rd] = x[rs1] * x[rs2]; // MUL
            case 0x1 -> { // MULH
                long a = x[rs1];
                long b = x[rs2];
                x[rd] = (int)((a * b) >> 32);
            }
            case 0x2 -> { // MULHSU
                long a = x[rs1];
                long b = Integer.toUnsignedLong(x[rs2]);
                x[rd] = (int)((a * b) >> 32);
            }

            case 0x3 -> { // MULHU
                long a = Integer.toUnsignedLong(x[rs1]);
                long b = Integer.toUnsignedLong(x[rs2]);
                x[rd] = (int)((a * b) >> 32);
            }

            case 0x4 -> { // DIV
                if (x[rs2] == 0) {
                    x[rd] = -1;
                } else if (x[rs1] == Integer.MIN_VALUE && x[rs2] == -1) {
                    x[rd] = Integer.MIN_VALUE;
                } else {
                    x[rd] = x[rs1] / x[rs2];
                }
            }

            case 0x5 -> { // DIVU
                if (x[rs2] == 0) {
                    x[rd] = -1;
                } else {
                    x[rd] = Integer.divideUnsigned(x[rs1], x[rs2]);
                }
            }

            case 0x6 -> { // REM
                if (x[rs2] == 0) {
                    x[rd] = x[rs1];
                } else if (x[rs1] == Integer.MIN_VALUE && x[rs2] == -1) {
                    x[rd] = 0;
                } else {
                    x[rd] = x[rs1] % x[rs2];
                }
            }

            case 0x7 -> { // REMU
                if (x[rs2] == 0) {
                    x[rd] = x[rs1];
                } else {
                    x[rd] = Integer.remainderUnsigned(x[rs1], x[rs2]);
                }
            }

            default -> {
                System.err.println("Unsupported M extension OP");
                running = false;
            }
        }
    }

    private void execOpInteger(int rd, int funct3, int rs1, int rs2, int funct7) {
        switch (funct3) {
            case 0x0 -> {
                if (funct7 == 0x00) {
                    x[rd] = x[rs1] + x[rs2]; // ADD
                } else if (funct7 == 0x20) {
                    x[rd] = x[rs1] - x[rs2]; // SUB
                } else {
                    System.err.println("Unsupported OP");
                    running = false;
                }
            }
            case 0x1 -> x[rd] = x[rs1] << (x[rs2] & 31); // SLL
            case 0x2 -> x[rd] = (x[rs1] < x[rs2]) ? 1 : 0; // SLT
            case 0x3 -> x[rd] = Integer.compareUnsigned(x[rs1], x[rs2]) < 0 ? 1 : 0; // SLTU
            case 0x4 -> x[rd] = x[rs1] ^ x[rs2]; // XOR
            case 0x5 -> {
                if (funct7 == 0x00) {
                    x[rd] = x[rs1] >>> (x[rs2] & 31); // SRL
                } else if (funct7 == 0x20) {
                    x[rd] = x[rs1] >> (x[rs2] & 31); // SRA
                } else {
                    System.err.println("Unsupported OP");
                    running = false;
                }
            }
            case 0x6 -> x[rd] = x[rs1] | x[rs2]; // OR
            case 0x7 -> x[rd] = x[rs1] & x[rs2]; // AND
            default -> {
                System.err.println("Unsupported OP");
                running = false;
            }
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

        int addr = x[rs1] + imm;

        switch (funct3) {
            case 0x0 -> x[rd] = signExtend(load8(addr), 8);   // LB
            case 0x1 -> x[rd] = signExtend(load16(addr), 16);  // LH
            case 0x2 -> x[rd] = load32(addr); // LW
            case 0x4 -> x[rd] = load8(addr) & 0xFF; // LBU
            case 0x5 -> x[rd] = load16(addr) & 0xFFFF; // LHU

            default -> {
                System.err.printf("Unsupported LOAD: %08X", funct3);
                running = false;
            }
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

        int addr = x[rs1] + imm;

        switch (funct3) {

            case 0x0 -> store8(addr, x[rs2]); // SB

            case 0x1 -> store16(addr, x[rs2]); // SH

            case 0x2 -> store32(addr, x[rs2]); // SW

            default -> {
                System.err.println("Unsupported STORE");
                running = false;
            }
        }

//        System.out.printf(
//                "STORE: rs1=%d val=0x%08X imm=0x%08X addr=0x%08X\n",
//                rs1, x[rs1], imm, addr
//        );
    }

    private void execBranch(int instr, int oldPc) {

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
            case 0x0 -> x[rs1] == x[rs2]; // BEQ
            case 0x1 -> x[rs1] != x[rs2]; // BNE
            case 0x4 -> x[rs1] < x[rs2]; // BLT
            case 0x5 -> x[rs1] >= x[rs2]; // BGE
            case 0x6 -> Integer.compareUnsigned(x[rs1], x[rs2]) < 0; // BLTU
            case 0x7 -> Integer.compareUnsigned(x[rs1], x[rs2]) >= 0; // BGEU
            default -> false;
        };



        if (take) {
            pc = oldPc + imm;
        }
        //System.out.printf("BRANCH pc=%08X take=%b\n", oldPc, take);
    }

    private void execJal(int instr, int oldPc) {

        int rd = (instr >> 7) & 0x1F;

        int imm =
                (((instr >> 31) & 1) << 20)
                        | (((instr >> 12) & 0xFF) << 12)
                        | (((instr >> 20) & 1) << 11)
                        | (((instr >> 21) & 0x3FF) << 1);

        imm = signExtend(imm, 21);

        x[rd] = oldPc + 4;
        pc = oldPc + imm;
    }

    private void execJalr(int instr, int oldPc) {

        int rd = (instr >> 7) & 0x1F;
        int rs1 = (instr >> 15) & 0x1F;
        int imm = signExtend(instr >> 20, 12);
        int target = (x[rs1] + imm) & ~1;

        x[rd] = oldPc + 4;
        pc = target;
    }

    private void execLui(int instr) {

        int rd = (instr >> 7) & 0x1F;

        x[rd] = instr & 0xFFFFF000;
    }

    private void execAuipc(int instr, int oldPc) {

        int rd = (instr >> 7) & 0x1F;

        x[rd] = oldPc + (instr & 0xFFFFF000);
    }

    // utils
    private int signExtend(int value, int bits) {

        int shift = 32 - bits;

        return (value << shift) >> shift;
    }

    public void dumpRegisters() {

        for (int i = 0; i < 32; i++) {
            System.out.printf(
                    "x%-2d = 0x%08X (%d)\n",
                    i,
                    x[i],
                    x[i]
            );
        }

        System.out.printf("PC=%08X pending=%d\n", pc, pendingInterrupts);
    }
}