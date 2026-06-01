package ru.bpm140.rottenmangal;

import java.io.*;

public class CPUSnapshot {
    // CPU
    public final int[] registers;
    public final int pc;
    // Interrupts
    public final int mip;
    public final int mie;
    public final int mtvec;
    public final int mepc;
    public final int mcause;
    public final int mtval;
    public final int mstatus;

    CPUSnapshot(CPU cpu) {
        pc = cpu.pc;
        registers = cpu.registers.clone();
        mip = cpu.interruptController.mip;
        mie = cpu.interruptController.mie;
        mtvec = cpu.interruptController.mtvec;
        mepc = cpu.interruptController.mepc;
        mcause = cpu.interruptController.mcause;
        mtval = cpu.interruptController.mtval;
        mstatus = cpu.interruptController.mstatus;
    }

    public CPUSnapshot(byte[] state) throws IOException {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(state));

        this.pc = in.readInt();

        this.registers = new int[in.readInt()];
        for (int i = 0; i < this.registers.length; i++) {
            this.registers[i] = in.readInt();
        }

        this.mip = in.readInt();
        this.mie = in.readInt();
        this.mtvec = in.readInt();
        this.mepc = in.readInt();
        this.mcause = in.readInt();
        this.mtval = in.readInt();
        this.mstatus = in.readInt();
    }

    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bout);

        out.writeInt(pc);

        out.writeInt(registers.length);
        for (int r : registers) {
            out.writeInt(r);
        }

        out.writeInt(mip);
        out.writeInt(mie);
        out.writeInt(mtvec);
        out.writeInt(mepc);
        out.writeInt(mcause);
        out.writeInt(mtval);
        out.writeInt(mstatus);

        out.flush();
        return bout.toByteArray();
    }
}
