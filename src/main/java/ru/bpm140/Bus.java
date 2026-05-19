package ru.bpm140;

import java.util.HashMap;

public class Bus {
    HashMap<Integer, Device> devices = new HashMap<>();
    private int connectedDeviceIndex = 0;

    public int attach(Device dev) {
        connectedDeviceIndex += 1;
        devices.put(connectedDeviceIndex, dev);
        return connectedDeviceIndex;
    }

    public void detach(int id) {
        connectedDeviceIndex--;
        devices.remove(id);
    }

    public int send(Packet p) {
        Device d = devices.get(p.destinationAddress);
        if (d == null) {
            throw new RuntimeException("No device: " + p.destinationAddress);
        }
        return d.handle(p);
    }
}
