package ru.bpm140.rottenmangal;

import org.junit.jupiter.api.Test;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;

public class RV32UITest {
    private CPU instantiateCPU(String file) throws Exception {
        InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream(file);
        byte[] elf = is.readAllBytes();
        CPU cpu = new CPU();
        var memory = new MemoryRegion(0x80000000, 16*1024*1024, true, true);
        cpu.memory.add(memory);
        cpu.loadELF(elf);
        cpu.status.setRunning();
        return cpu;
    }

    @Test
    void RV32UIAdd() {
        try {
            var cpu = instantiateCPU("rv32ui-p-add");

            while (true) {
                if (cpu.getStatus().isRunning()) {
                    cpu.step();
                } else {
                    break;
                }
            }

            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }

    @Test
    void RV32UIAddi() {
        try {
            var cpu = instantiateCPU("rv32ui-p-addi");

            while (true) {
                if (cpu.getStatus().isRunning()) {
                    cpu.step();
                } else {
                    break;
                }
            }

            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }

    @Test
    void RV32UIAnd() {
        try {
            var cpu = instantiateCPU("rv32ui-p-and");

            while (true) {
                if (cpu.getStatus().isRunning()) {
                    cpu.step();
                } else {
                    break;
                }
            }

            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }

    @Test
    void RV32UIAndi() {
        try {
            var cpu = instantiateCPU("rv32ui-p-andi");

            while (true) {
                if (cpu.getStatus().isRunning()) {
                    cpu.step();
                } else {
                    break;
                }
            }

            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }

    @Test
    void RV32UIAuipc() {
        try {
            var cpu = instantiateCPU("rv32ui-p-auipc");

            while (true) {
                if (cpu.getStatus().isRunning()) {
                    cpu.step();
                } else {
                    break;
                }
            }

            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
}
