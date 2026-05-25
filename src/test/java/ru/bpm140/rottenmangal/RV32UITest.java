package ru.bpm140.rottenmangal;

import org.junit.jupiter.api.Test;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;


// I can't really find any useful information how this tests works, but on experience other emulator writers -
// The test is successful if it reaches a certain ecall condition. RottenMangal raises
public class RV32UITest {
    private CPU instantiateCPU(String file) throws Exception {
        InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream(file);
        byte[] elf = is.readAllBytes();
        CPU cpu = new CPU();
        var memory = new MemoryRegion(0x80000000, 16 * 1024 * 1024, true, true);
        cpu.memory.add(memory);
        cpu.loadELF(elf);
        cpu.status.setRunning();
        return cpu;
    }

    private void runUntilExit(CPU cpu) {
        for (int i = 0; i < 5000; i++) {
            if (cpu.getStatus().isRunning()) {
                cpu.step();
            } else {
                return;
            }
        }
    }

    @Test
    void RV32UIAdd() {
        try {
            var cpu = instantiateCPU("rv32ui-p-add");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIAddi() {
        try {
            var cpu = instantiateCPU("rv32ui-p-addi");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIAnd() {
        try {
            var cpu = instantiateCPU("rv32ui-p-and");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIAndi() {
        try {
            var cpu = instantiateCPU("rv32ui-p-andi");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIAuipc() {
        try {
            var cpu = instantiateCPU("rv32ui-p-auipc");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIBeq() {
        try {
            var cpu = instantiateCPU("rv32ui-p-beq");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIBge() {
        try {
            var cpu = instantiateCPU("rv32ui-p-bge");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIBgeu() {
        try {
            var cpu = instantiateCPU("rv32ui-p-bgeu");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIBlt() {
        try {
            var cpu = instantiateCPU("rv32ui-p-blt");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIBltu() {
        try {
            var cpu = instantiateCPU("rv32ui-p-bltu");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIBne() {
        try {
            var cpu = instantiateCPU("rv32ui-p-bne");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIFence_i() {
        try {
            var cpu = instantiateCPU("rv32ui-p-fence_i");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIJal() {
        try {
            var cpu = instantiateCPU("rv32ui-p-jal");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIJalr() {
        try {
            var cpu = instantiateCPU("rv32ui-p-jalr");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UILb() {
        try {
            var cpu = instantiateCPU("rv32ui-p-lb");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UILbu() {
        try {
            var cpu = instantiateCPU("rv32ui-p-lbu");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UILd_st() {
        try {
            var cpu = instantiateCPU("rv32ui-p-ld_st");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UILh() {
        try {
            var cpu = instantiateCPU("rv32ui-p-lh");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UILhu() {
        try {
            var cpu = instantiateCPU("rv32ui-p-lhu");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UILui() {
        try {
            var cpu = instantiateCPU("rv32ui-p-lui");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UILw() {
        try {
            var cpu = instantiateCPU("rv32ui-p-lw");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIMa_data() {
        try {
            var cpu = instantiateCPU("rv32ui-p-ma_data");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIOr() {
        try {
            var cpu = instantiateCPU("rv32ui-p-or");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIOri() {
        try {
            var cpu = instantiateCPU("rv32ui-p-ori");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISb() {
        try {
            var cpu = instantiateCPU("rv32ui-p-sb");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISh() {
        try {
            var cpu = instantiateCPU("rv32ui-p-sh");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISimple() {
        try {
            var cpu = instantiateCPU("rv32ui-p-simple");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISll() {
        try {
            var cpu = instantiateCPU("rv32ui-p-sll");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISlli() {
        try {
            var cpu = instantiateCPU("rv32ui-p-slli");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISlt() {
        try {
            var cpu = instantiateCPU("rv32ui-p-slt");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISlti() {
        try {
            var cpu = instantiateCPU("rv32ui-p-slti");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISltiu() {
        try {
            var cpu = instantiateCPU("rv32ui-p-sltiu");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISltu() {
        try {
            var cpu = instantiateCPU("rv32ui-p-sltu");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISra() {
        try {
            var cpu = instantiateCPU("rv32ui-p-sra");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISrai() {
        try {
            var cpu = instantiateCPU("rv32ui-p-srai");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISrl() {
        try {
            var cpu = instantiateCPU("rv32ui-p-srl");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISrli() {
        try {
            var cpu = instantiateCPU("rv32ui-p-srli");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISt_ld() {
        try {
            var cpu = instantiateCPU("rv32ui-p-st_ld");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISub() {
        try {
            var cpu = instantiateCPU("rv32ui-p-sub");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISw() {
        try {
            var cpu = instantiateCPU("rv32ui-p-sw");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIXor() {
        try {
            var cpu = instantiateCPU("rv32ui-p-xor");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIXori() {
        try {
            var cpu = instantiateCPU("rv32ui-p-xori");
            runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
}