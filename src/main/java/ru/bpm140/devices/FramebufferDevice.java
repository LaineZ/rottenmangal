package ru.bpm140.devices;

import ru.bpm140.Device;
import ru.bpm140.Packet;

public class FramebufferDevice implements Device {
    public final byte[] fb;
    private final int width;
    private final int height;


    public FramebufferDevice(int w, int h) {
        width = w;
        height = h;
        fb = new byte[width * height];
    }

    public int getWidth() {
        return width;
    }

    public  int getHeight() {
        return height;
    }

    @Override
    public int handle(Packet p) {
        int data = p.value;

        int x = (data & 0xFF);
        int y = ((data >> 8) & 0xFF);
        byte color = (byte) ((data >> 16) & 0xFF);

        var offset = y * getWidth() + x;

        if (!p.read) {
            fb[offset] = color;
        }

        return fb[offset] & 0xFF;
    }
}
