package com.liquidskr.liQuiDWorld.standard;

import java.io.Serializable;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class PlayerData implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID playerUUID;
    private String nickname;
    private int gold;
    private int dailyGifts;
    private transient Inventory storage;

    public PlayerData(String nickname) {
        this.playerUUID = UUID.randomUUID();
        this.nickname = nickname;
        this.gold = 10000;
        this.dailyGifts = 3;
        this.storage = null;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getDailyGifts() {
        return dailyGifts;
    }

    public void setDailyGifts(int dailyGifts) {
        this.dailyGifts = dailyGifts;
    }

    public Inventory getStorage() {
        if (storage == null) {
            storage = Bukkit.createInventory(null, 45, "보관함");
        }
        return storage;
    }
}
