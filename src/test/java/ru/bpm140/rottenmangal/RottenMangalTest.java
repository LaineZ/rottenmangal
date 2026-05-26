package ru.bpm140.rottenmangal;

import org.junit.jupiter.api.Test;
import ru.bpm140.rottenmangal.utils.CPUUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class RottenMangalTest {
    @Test
    void RottenMangalEbreakTrap() {
        try {
            var cpu = CPUUtils.instantiateCPU("rottenmangal-ebreak-trap");
            CPUUtils.runUntilExit(cpu);
            assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

        } catch (Exception e) {
            assertFalse(false);
        }
    }
}
