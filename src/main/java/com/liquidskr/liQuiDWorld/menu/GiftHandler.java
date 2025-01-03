package com.liquidskr.liQuiDWorld.menu;

import com.liquidskr.liQuiDWorld.standard.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GiftHandler implements Listener {
    private final JavaPlugin plugin;
    private final Map<UUID, PlayerData> playerDataMap;
    private final Map<UUID, Boolean> awaitingGift = new HashMap<>(); // 선물하기 상태를 저장
    private final Map<UUID, ItemStack> selectedGiftItem = new HashMap<>(); // 플레이어가 선택한 선물 아이템을 저장

    public GiftHandler(JavaPlugin plugin, Map<UUID, PlayerData> playerDataMap) {
        this.plugin = plugin;
        this.playerDataMap = playerDataMap;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void startGiftProcess(Player player) {
        awaitingGift.put(player.getUniqueId(), true);
        player.sendMessage("인벤토리를 열고, 선물로 보낼 아이템을 클릭하세요:");
    }

    public void openRecipientSelection(Player player, ItemStack giftItem) {
        Inventory recipientMenu = Bukkit.createInventory(null, 36, "선물받을 플레이어 선택");
        for (Player target : Bukkit.getOnlinePlayers()) {
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
            skullMeta.setOwningPlayer(target);
            skullMeta.setDisplayName("§r" + target.getDisplayName()); // 기울임을 없애기 위해 §r 사용
            playerHead.setItemMeta(skullMeta);

            recipientMenu.addItem(playerHead);
        }
        player.openInventory(recipientMenu);
        selectedGiftItem.put(player.getUniqueId(), giftItem);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        String title = event.getView().getTitle();

        if (title.equals("선물받을 플레이어 선택")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.getType() == Material.PLAYER_HEAD) {
                SkullMeta skullMeta = (SkullMeta) clickedItem.getItemMeta();
                Player target = Bukkit.getPlayer(skullMeta.getOwningPlayer().getUniqueId());

                if (target != null) {
                    UUID playerUUID = player.getUniqueId();
                    UUID targetUUID = target.getUniqueId();
                    PlayerData playerData = playerDataMap.get(playerUUID);
                    PlayerData targetData = playerDataMap.get(targetUUID);
                    ItemStack giftItem = selectedGiftItem.remove(playerUUID);

                    if (playerData != null && targetData != null && giftItem != null && playerData.getDailyGifts() > 0) {
                        Inventory targetStorage = targetData.getStorage();
                        if (targetStorage.firstEmpty() != -1) { // 보관함에 빈 슬롯이 있는지 확인
                            targetStorage.addItem(giftItem);
                            player.sendMessage("§6" + targetData.getNickname() + "에게 선물을 보냈습니다.");
                            Player targetPlayer = Bukkit.getPlayer(targetUUID);
                            if (targetPlayer != null) {
                                targetPlayer.sendMessage("§6" + playerData.getNickname() + "로부터 선물이 도착했습니다.");
                            }
                            playerData.setDailyGifts(playerData.getDailyGifts() - 1);

                            // 선물 보내기 성공 시 보내는 사람의 인벤토리에서 아이템 제거
                            player.getInventory().removeItem(giftItem);

                            player.closeInventory();
                        } else {
                            player.sendMessage("§c상대방의 보관함이 가득 차 있어 선물을 보낼 수 없습니다.");
                        }
                    } else if (playerData != null && giftItem != null) {
                        player.sendMessage("§c오늘 더 이상 선물을 보낼 수 없습니다.");
                    }

                    // 마우스 커서에 아이템이 남지 않도록 설정
                    player.setItemOnCursor(new ItemStack(Material.AIR));
                }
            }
        } else if (awaitingGift.getOrDefault(player.getUniqueId(), false) && clickedInventory != null && clickedInventory.equals(player.getInventory())) {
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                player.setItemOnCursor(new ItemStack(Material.AIR));
                event.setCancelled(true);
                awaitingGift.remove(player.getUniqueId());
                openRecipientSelection(player, clickedItem);
            }
        }
    }
    private void savePlayerData(PlayerData playerData) {
        // playerData를 저장하는 로직 구현
        // 예: 파일에 직렬화하여 저장
    }
}
