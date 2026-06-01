package ru.bpm140.rottenmangal;

public class CPUStatus {
    public enum CPUState {
        RUNNING,
        HALTED,
        HALT_TEST_PASS, // For tests
        FAULT // Critical emulator fault
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
}
