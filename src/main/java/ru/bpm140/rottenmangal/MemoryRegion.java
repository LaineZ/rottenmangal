package ru.bpm140.rottenmangal;

public class MemoryRegion {
    final int base;
    final int size;
    byte[] data;
    boolean read;
    boolean write;

    public MemoryRegion(int base, int size, boolean r, boolean w) {
        this.base = base;
        this.size = size;
        this.data = new byte[size];
        this.read = r;
        this.write = w;
    }

    /**
     * Loads a data from memory. Primarily for deserialization purposes
     */
    public void loadData(byte[] data) throws IllegalStateException {
        if (this.data.length < data.length) {
            throw new IllegalArgumentException("Loaded array block is larger than this shit can handle...");
        }

        System.arraycopy(data, 0, data, 0, data.length);
    }

    /**
     * Gets a data from memory. For serialization purposes only
     * @return an array copy of the memory data
     */
    public byte[] getData() {
        return this.data.clone();
    }

    boolean contains(int addr) {
        return addr >= base && addr < base + size;
    }
    int offset(int addr) {
        return addr - base;
    }
}
