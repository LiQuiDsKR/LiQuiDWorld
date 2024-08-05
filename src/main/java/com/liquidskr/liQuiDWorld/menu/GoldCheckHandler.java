package com.liquidskr.liQuiDWorld.menu;

import com.liquidskr.liQuiDWorld.standard.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;

public class GoldCheckHandler implements Listener {
    private final JavaPlugin plugin;
    private final Map<UUID, PlayerData> playerDataMap;

    public GoldCheckHandler(JavaPlugin plugin, Map<UUID, PlayerData> playerDataMap) {
        this.plugin = plugin;
        this.playerDataMap = playerDataMap;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void checkGold(Player player) {
        PlayerData playerData = playerDataMap.get(player.getUniqueId());
        if (playerData != null) {
            player.sendMessage("§6보유 골드: " + playerData.getGold() + " G");
        }
    }
}
