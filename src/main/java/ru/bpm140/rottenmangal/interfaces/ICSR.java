package ru.bpm140.rottenmangal.interfaces;
import java.util.OptionalInt;

public interface ICSR {
    public OptionalInt readCSR(int addr);
    public boolean writeCSR(int addr, int val);
}
