package ru.bpm140.rottenmangal.utils;

import ru.bpm140.rottenmangal.CPU;
import ru.bpm140.rottenmangal.CPUStatus;
import ru.bpm140.rottenmangal.MemoryRegion;

import java.io.InputStream;

public class CPUUtils {
    public static CPU instantiateCPU(String file) throws Exception {
        InputStream is = CPUUtils.class
                .getClassLoader()
                .getResourceAsStream(file);
        byte[] elf = is.readAllBytes();
        CPU cpu = new CPU();
        var memory = new MemoryRegion(0x80000000, 16 * 1024 * 1024, true, true);
        cpu.memory.add(memory);
        cpu.loadELF(elf);
        cpu.setRunning();
        return cpu;
    }

    public static void runUntilExit(CPU cpu) {
        for (int i = 0; i < 5000; i++) {
            if (cpu.getState() == CPUStatus.CPUState.RUNNING) {
                cpu.step();
            } else {
                System.out.println(cpu.getState());
                return;
            }
        }

        System.out.println("Exiting from loop");
    }
}
