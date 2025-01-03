package com.liquidskr.liQuiDWorld.standard;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.UUID;

public class PlayerData implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID playerUUID;

    private String nickname;
    private int gold;
    private int dailyGifts;
    private transient Inventory storage;
    private ItemStack[] serializedStorage; // 보관함 데이터를 직렬화된 형태로 저장

    public PlayerData(String nickname) {
        this.playerUUID = UUID.randomUUID();
        this.nickname = nickname;
        this.gold = 10000;
        this.dailyGifts = 3;
        this.storage = Bukkit.createInventory(null, 45, "보관함");
        this.serializedStorage = new ItemStack[45]; // 기본적으로 빈 배열로 초기화
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
            if (serializedStorage != null) {
                storage.setContents(serializedStorage); // 저장된 데이터로 보관함 복원
            }
        }
        return storage;
    }

    public void setStorage(Inventory storage) {
        this.storage = storage;
        this.serializedStorage = storage.getContents(); // 보관함의 내용을 직렬화 가능한 형태로 저장
    }
}
