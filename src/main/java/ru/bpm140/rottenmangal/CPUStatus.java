package ru.bpm140.rottenmangal;

public class CPUStatus {
    public enum CPUState {
        RUNNING,
        HALTED,
        HALT_TEST_PASS, // For tests
        EXCEPTION,
        FAULT
    }

    public enum ExceptionCause {
        NONE,
        BREAKPOINT,
        LOAD_FAULT,
        STORE_FAULT,
        UNINITIALIZED_BUS_CONTROL,
        BUS_CONTROL_WRITE_ON_READ,
        UNKNOWN_OPCODE,
        UNKNOWN_SYSCALL,
        UNKNOWN_CSR_MODE,
        BAD_SHIFT_OP_IMM,
        UNSUPPORTED_OP_IMM,
        UNSUPPORTED_OP,
        UNSUPPORTED_LOAD,
        UNSUPPORTED_STORE;

        // Gets a RISC-V specified exception code, correspond to the RISC-V documentation
        public int getRiscVExceptionCode() {
            switch (this) {
                case NONE -> { return 0; } // SCARY!!!
                case BREAKPOINT -> { return 3; }
                case LOAD_FAULT, UNSUPPORTED_LOAD -> { return 5; }
                case STORE_FAULT, UNSUPPORTED_STORE -> { return 7; }
                case UNINITIALIZED_BUS_CONTROL -> { return 24; }
                case BUS_CONTROL_WRITE_ON_READ -> { return 25; }
                case UNKNOWN_OPCODE, BAD_SHIFT_OP_IMM, UNSUPPORTED_OP_IMM, UNSUPPORTED_OP, UNKNOWN_SYSCALL, UNKNOWN_CSR_MODE -> { return 2; }
                default -> { return 19; } // Hardware error
            }
        }
    }

    private CPUState state;
    ExceptionCause cause;

    public CPUStatus() {
        this.state = CPUState.HALTED;
        this.cause = ExceptionCause.NONE;
    }

    public void setException(ExceptionCause cause) {
        this.state = CPUState.EXCEPTION;
        this.cause = cause;
    }

    void setState(CPUState state) {
        if (state == CPUState.EXCEPTION) {
            throw new IllegalArgumentException("CPU state exception can only set with `setExceptionArgument`");
        }

        this.state = state;
    }

    public void setRunning() {
        this.state = CPUState.RUNNING;
    }

    public boolean isRunning() {
        return this.state == CPUState.RUNNING;
    }

    public void setHalted() {
        this.state = CPUState.HALTED;
    }

    public CPUState getRunningState() {
        return state;
    }

    public CPUState getState() {
        return this.state;
    }

    public ExceptionCause getCause() {
        return cause;
    }
}
