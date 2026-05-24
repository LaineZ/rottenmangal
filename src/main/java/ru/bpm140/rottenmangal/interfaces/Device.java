package ru.bpm140.rottenmangal.interfaces;

import ru.bpm140.rottenmangal.Packet;

public interface Device {
    int handle(Packet p);
}