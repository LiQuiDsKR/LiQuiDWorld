package com.liquidskr.liQuiDWorld.menu;

import com.liquidskr.liQuiDWorld.standard.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GoldSendHandler implements Listener {
    private final JavaPlugin plugin;
    private final Map<UUID, PlayerData> playerDataMap;
    private final Map<UUID, UUID> awaitingGoldTransfer = new HashMap<>();

    public GoldSendHandler(JavaPlugin plugin, Map<UUID, PlayerData> playerDataMap) {
        this.plugin = plugin;
        this.playerDataMap = playerDataMap;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void openMenu(Player player) {
        Inventory sendGoldMenu = Bukkit.createInventory(null, 36, "골드 보내기");
        for (Player target : Bukkit.getOnlinePlayers()) {
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
            skullMeta.setOwningPlayer(target);
            skullMeta.setDisplayName("§r" + target.getDisplayName()); // 기울임을 없애기 위해 §r 사용
            playerHead.setItemMeta(skullMeta);

            sendGoldMenu.addItem(playerHead);
        }
        player.openInventory(sendGoldMenu);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().equals("골드 보내기")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.getType() == Material.PLAYER_HEAD) {
                SkullMeta skullMeta = (SkullMeta) clickedItem.getItemMeta();
                Player target = Bukkit.getPlayer(skullMeta.getOwningPlayer().getUniqueId());

                if (target != null) {
                    player.sendMessage(target.getDisplayName() + "에게 몇 골드를 보내시겠습니까? (단위: G)");
                    awaitingGoldTransfer.put(player.getUniqueId(), target.getUniqueId());
                    player.closeInventory();
                }
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (awaitingGoldTransfer.containsKey(playerUUID)) {
            event.setCancelled(true); // 채팅 이벤트 취소

            try {
                int amount = Integer.parseInt(event.getMessage());
                UUID targetUUID = awaitingGoldTransfer.get(playerUUID);
                PlayerData playerData = playerDataMap.get(playerUUID);
                PlayerData targetData = playerDataMap.get(targetUUID);

                if (playerData != null && targetData != null && amount > 0 && playerData.getGold() >= amount) {
                    playerData.setGold(playerData.getGold() - amount);
                    targetData.setGold(targetData.getGold() + amount);
                    player.sendMessage("§6" + amount + " G를 " + targetData.getNickname() + "에게 보냈습니다.");
                    Player targetPlayer = Bukkit.getPlayer(targetUUID);
                    if (targetPlayer != null) {
                        targetPlayer.sendMessage("§6" + playerData.getNickname() + "로부터 " + amount + " G를 받았습니다.");
                    }
                    awaitingGoldTransfer.remove(playerUUID);
                } else {
                    player.sendMessage("§c유효하지 않은 금액입니다.");
                }
            } catch (NumberFormatException e) {
                player.sendMessage("§c유효하지 않은 숫자입니다.");
            }
        }
    }
}
