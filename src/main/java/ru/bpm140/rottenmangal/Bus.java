package ru.bpm140.rottenmangal;

import ru.bpm140.rottenmangal.interfaces.Device;

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

    public <T extends Device> T findByType(Class<T> type) {
        for (Device d : devices.values()) {
            if (type.isInstance(d)) {
                return type.cast(d);
            }
        }
        return null;
    }

    public int send(Packet p) {
        Device d = devices.get(p.destinationAddress);
        if (d == null) {
            throw new RuntimeException("No device: " + p.destinationAddress);
        }
        return d.handle(p);
    }
}
