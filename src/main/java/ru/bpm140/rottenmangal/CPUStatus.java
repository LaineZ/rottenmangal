package ru.bpm140.rottenmangal;

public class CPUStatus {
    public enum CPUState {
        RUNNING,
        HALTED,
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
                case UNKNOWN_OPCODE, BAD_SHIFT_OP_IMM, UNSUPPORTED_OP_IMM, UNSUPPORTED_OP -> { return 2; }
                default -> { return 19; } // Hardware error
            }
        }
    }

    CPUState status;
    ExceptionCause cause;

    public CPUStatus() {
        this.status = CPUState.HALTED;
        this.cause = ExceptionCause.NONE;
    }

    public void setException(ExceptionCause cause) {
        this.status = CPUState.EXCEPTION;
        this.cause = cause;
    }

    public void setRunning() {
        this.status = CPUState.RUNNING;
    }

    public boolean isRunning() {
        return this.status == CPUState.RUNNING;
    }

    public void setHalted() {
        this.status = CPUState.HALTED;
    }

    public CPUState getRunningState() {
        return status;
    }

    public CPUState getStatus() {
        return this.status;
    }

    public ExceptionCause getCause() {
        return cause;
    }
}
