package ru.bpm140.rottenmangal;
import java.util.OptionalInt;

public interface ICSR {
    public OptionalInt readCSR(int addr);
    public boolean writeCSR(int addr, int val);
}
