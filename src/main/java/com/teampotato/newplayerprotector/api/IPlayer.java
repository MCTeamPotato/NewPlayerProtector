package com.teampotato.newplayerprotector.api;

public interface IPlayer {
    boolean protect_shouldCountTick();
    void protect_setShouldCountTick(boolean shouldCountTick);

    int protect_getTickCount();
    int protect_getRespawnTickCount();
    void protect_setRespawnTickCount(int respawnTickCount);
}
