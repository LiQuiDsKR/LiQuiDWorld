package com.liquidskr.liQuiDWorld.menu;

import com.liquidskr.liQuiDWorld.standard.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.HashMap;

public class NicknameChangeHandler implements Listener {
    private final JavaPlugin plugin;
    private final Map<UUID, PlayerData> playerDataMap;
    private final Map<UUID, Boolean> awaitingNicknameChange = new HashMap<>();

    public NicknameChangeHandler(JavaPlugin plugin, Map<UUID, PlayerData> playerDataMap) {
        this.plugin = plugin;
        this.playerDataMap = playerDataMap;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void openMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 36, "닉네임 변경");
        // 메뉴 설정
        player.openInventory(menu);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (awaitingNicknameChange.getOrDefault(playerUUID, false)) {
            event.setCancelled(true); // 채팅 이벤트 취소

            String newNickname = event.getMessage();
            PlayerData playerData = playerDataMap.get(playerUUID);
            if (playerData != null) {
                playerData.setNickname(newNickname);
                player.setDisplayName(newNickname); // 플레이어의 디스플레이 이름 업데이트
                player.sendMessage("닉네임이 '" + newNickname + "'(으)로 변경되었습니다.");
                awaitingNicknameChange.put(playerUUID, false);
            }
        }
    }

    public void startNicknameChange(Player player) {
        player.sendMessage("변경할 닉네임을 입력하세요:");
        awaitingNicknameChange.put(player.getUniqueId(), true);
        player.closeInventory();
    }
}
