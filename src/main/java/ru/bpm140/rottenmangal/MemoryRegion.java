package ru.bpm140.rottenmangal;

public class MemoryRegion {
    int base;
    int size;
    public byte[] data;
    boolean read;
    boolean write;

    public MemoryRegion(int base, int size, boolean r, boolean w) {
        this.base = base;
        this.size = size;
        this.data = new byte[size];
        this.read = r;
        this.write = w;
    }

    boolean contains(int addr) {
        return addr >= base && addr < base + size;
    }

    int offset(int addr) {
        return addr - base;
    }
}
