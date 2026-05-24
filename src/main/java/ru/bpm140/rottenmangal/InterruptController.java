package ru.bpm140.rottenmangal;

import ru.bpm140.rottenmangal.interfaces.ICSR;

import java.util.OptionalInt;

public class InterruptController implements ICSR {
    public int mip = 0;   // pending interrupts
    public int mie = 0;   // enabled interrupts

    public int mtvec = 0x0;

    public int mepc;
    public int mcause;
    public int mtval;
    public int mstatus;

    private static final int MIE  = 1 << 3;   // global interrupt enable
    private static final int MPIE = 1 << 7;   // previous interrupt enable
    static final int MPP  = 3 << 11;

    public void raise(int irq) {
        mip |= (1 << irq);
    }
    public void clear(int irq) {
        mip &= ~(1 << irq);
    }

    public boolean hasInterrupt(boolean inTrap) {
        return !inTrap && ((mip & mie) != 0);
    }

    public int takeInterrupt() {
        int pending = mip & mie;
        if (pending == 0) return -1;

        int irq = Integer.numberOfTrailingZeros(pending);
        mip &= ~(1 << irq);
        return irq;
    }

    public int trap(int pc, int cause, int tval) {
        // save context
        mepc = pc;
        mcause = cause;
        mtval = tval;

        // save/disable interrupts (RISC-V behavior)
        boolean mie = (mstatus & MIE) != 0;

        if (mie) {
            mstatus |= MPIE;
        }
        else {
            mstatus &= ~MPIE;
        }

        mstatus &= ~MIE;
        mstatus = (mstatus & ~MPP) | (3 << 11);

        // compute vector
        //System.out.printf("mstatus=%08X mtvec=%08X\n", mstatus, mtvec);
        return dispatchVector(cause);
    }

    private int dispatchVector(int cause) {
        int base = getVectorBase();

        boolean isInterrupt = (cause & 0x80000000) != 0;

        // vectored mode only applies to interrupts
        if (isInterrupt && (mtvec & 0x3) == 1) {
            int irq = cause & 0x7fffffff;
            return base + irq * 4;
        }

        return base;
    }

    private int getVectorBase() {
        return mtvec & ~0x3;
    }

    public int mretPC() {
        // restore MIE from MPIE
        if ((mstatus & MPIE) != 0) {
            mstatus |= MIE;
        } else {
            mstatus &= ~MIE;
        }

        mstatus |= MPIE;

        // clear MPP (optional but correct)
        mstatus &= ~(3 << 11);

        return mepc;
    }

    @Override
    public OptionalInt readCSR(int addr) {
        return switch (addr) {
            case 0x304 -> OptionalInt.of(mie);
            case 0x305 -> OptionalInt.of(mtvec);
            case 0x344 -> OptionalInt.of(mip);
            case 0x341 -> OptionalInt.of(mepc);
            case 0x342 -> OptionalInt.of(mcause);
            case 0x343 -> OptionalInt.of(mtval);
            case 0x300 -> OptionalInt.of(mstatus);
            default -> OptionalInt.empty();
        };
    }

    @Override
    public boolean writeCSR(int addr, int val) {
        //System.out.printf("write_csr=%08X\n", addr);
        switch (addr) {
            case 0x304 -> { mie = val; return true; }
            case 0x305 -> { mtvec = val & ~0x3; return true; }

            case 0x341 -> { mepc = val; return true; }
            case 0x342 -> { mcause = val; return true; }
            case 0x343 -> { mtval = val; return true; }

            case 0x300 -> {
                //System.out.printf("write_mstatus=%08X\n", mstatus);
                mstatus = val;
                return true;
            }

            case 0x344 -> {
                mip &= ~val; // write-1-to-clear
                return true;
            }

            default -> { return false; }
        }
    }
}