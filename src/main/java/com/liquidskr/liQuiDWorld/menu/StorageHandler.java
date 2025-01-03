package com.liquidskr.liQuiDWorld.menu;

import com.liquidskr.liQuiDWorld.standard.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;

public class StorageHandler implements Listener {
    private final JavaPlugin plugin;
    private final Map<UUID, PlayerData> playerDataMap;

    public StorageHandler(JavaPlugin plugin, Map<UUID, PlayerData> playerDataMap) {
        this.plugin = plugin;
        this.playerDataMap = playerDataMap;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void openStorage(Player player) {
        UUID playerUUID = player.getUniqueId();
        PlayerData playerData = playerDataMap.get(playerUUID);
        if (playerData != null) {
            Inventory storage = playerData.getStorage();
            player.openInventory(storage);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();

        if (event.getView().getTitle().equals("보관함")) {
            if (clickedInventory != null && clickedInventory.equals(player.getInventory())) {
                // 플레이어 인벤토리 클릭 허용
            } else {
                // 보관함 인벤토리 클릭 허용
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        Inventory inventory = event.getInventory();

        if (event.getView().getTitle().equals("보관함")) {
            PlayerData playerData = playerDataMap.get(playerUUID);
            if (playerData != null) {
                playerData.getStorage().setContents(inventory.getContents());

            }
        }
    }
    private void savePlayerData(PlayerData playerData) {
        // playerData를 저장하는 로직 구현
        // 예: 파일에 직렬화하여 저장
    }
}
