package ru.bpm140.rottenmangal;

import java.io.*;

public class CPUSnapshot {
    // CPU
    public CPUStatus.CPUState state;
    public CPUStatus.ExceptionCause exceptionCause;
    public int[] registers;
    public int pc;
    // Interrupts
    public int mip;
    public int mie;
    public int mtvec;
    public int mepc;
    public int mcause;
    public int mtval;
    public int mstatus;

    public CPUSnapshot() {}

    CPUSnapshot(CPU cpu) {
        state = cpu.getState();
        exceptionCause = cpu.getExceptionCause();
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
}
