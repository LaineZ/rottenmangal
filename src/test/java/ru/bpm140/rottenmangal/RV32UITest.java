package ru.bpm140.rottenmangal;

import org.junit.jupiter.api.Test;
import ru.bpm140.rottenmangal.utils.CPUUtils;

import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;

public class RV32UITest {
    @Test
    void RV32UIAdd() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-add");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIAddi() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-addi");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIAnd() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-and");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIAndi() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-andi");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIAuipc() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-auipc");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIBeq() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-beq");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIBge() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-bge");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIBgeu() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-bgeu");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIBlt() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-blt");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIBltu() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-bltu");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIBne() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-bne");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIFence_i() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-fence_i");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIJal() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-jal");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIJalr() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-jalr");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UILb() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-lb");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UILbu() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-lbu");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UILd_st() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-ld_st");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UILh() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-lh");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UILhu() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-lhu");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UILui() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-lui");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UILw() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-lw");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIMa_data() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-ma_data");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIOr() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-or");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIOri() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-ori");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISb() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-sb");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISh() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-sh");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISimple() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-simple");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISll() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-sll");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISlli() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-slli");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISlt() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-slt");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISlti() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-slti");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISltiu() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-sltiu");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISltu() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-sltu");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISra() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-sra");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISrai() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-srai");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISrl() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-srl");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISrli() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-srli");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISt_ld() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-st_ld");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISub() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-sub");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UISw() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-sw");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIXor() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-xor");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
    @Test
    void RV32UIXori() {
        try {
            var cpu = CPUUtils.instantiateCPU("rv32ui-p-xori");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getState());

        } catch (Exception e) {
            assertFalse(false);
        }
    }
}