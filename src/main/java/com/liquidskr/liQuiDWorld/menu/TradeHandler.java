package com.liquidskr.liQuiDWorld.menu;

import com.liquidskr.liQuiDWorld.standard.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TradeHandler implements Listener {
    private final JavaPlugin plugin;
    private final Map<UUID, PlayerData> playerDataMap;
    private final Map<UUID, TradeSession> tradeSessions = new HashMap<>();
    private final Map<UUID, UUID> pendingTrades = new HashMap<>();

    public TradeHandler(JavaPlugin plugin, Map<UUID, PlayerData> playerDataMap) {
        this.plugin = plugin;
        this.playerDataMap = playerDataMap;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void openTradeRequestMenu(Player player) {
        Inventory tradeMenu = Bukkit.createInventory(null, 36, "거래 요청자 선택");

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!target.equals(player)) {
                ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
                SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
                meta.setOwningPlayer(target);
                meta.setDisplayName("§r" + target.getDisplayName());
                playerHead.setItemMeta(meta);

                tradeMenu.addItem(playerHead);
            }
        }

        player.openInventory(tradeMenu);
    }

    public void startTrade(Player requester, Player receiver) {
        Inventory tradeInventory = Bukkit.createInventory(null, 36, "거래");

        // 주황색 영역: 거래 요청자의 템 확정 버튼
        ItemStack confirmRequester = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
        ItemMeta confirmRequesterMeta = confirmRequester.getItemMeta();
        confirmRequesterMeta.setDisplayName("§6거래 확정");
        confirmRequester.setItemMeta(confirmRequesterMeta);
        tradeInventory.setItem(27, confirmRequester);
        tradeInventory.setItem(28, confirmRequester);
        tradeInventory.setItem(29, confirmRequester);

        // 하늘색 영역: 거래 수신자의 템 확정 버튼
        ItemStack confirmReceiver = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        ItemMeta confirmReceiverMeta = confirmReceiver.getItemMeta();
        confirmReceiverMeta.setDisplayName("§6거래 확정");
        confirmReceiver.setItemMeta(confirmReceiverMeta);
        tradeInventory.setItem(33, confirmReceiver);
        tradeInventory.setItem(34, confirmReceiver);
        tradeInventory.setItem(35, confirmReceiver);

        // 초록색 영역: 양측의 확정 후 거래 완료 버튼
        ItemStack finalizeTrade = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta finalizeTradeMeta = finalizeTrade.getItemMeta();
        finalizeTradeMeta.setDisplayName("§6교환 아이템을 먼저 확정하세요");
        finalizeTrade.setItemMeta(finalizeTradeMeta);
        for (int i = 3; i <= 5; i++) {
            tradeInventory.setItem(i, finalizeTrade);
            tradeInventory.setItem(i + 9, finalizeTrade);
            tradeInventory.setItem(i + 18, finalizeTrade);
        }

        requester.openInventory(tradeInventory);
        receiver.openInventory(tradeInventory);

        tradeSessions.put(requester.getUniqueId(), new TradeSession(requester, receiver, tradeInventory, true));
        tradeSessions.put(receiver.getUniqueId(), new TradeSession(requester, receiver, tradeInventory, false));
    }

    public void confirmTrade(Player player) {
        UUID requesterUUID = pendingTrades.get(player.getUniqueId());
        if (requesterUUID != null) {
            Player requester = Bukkit.getPlayer(requesterUUID);
            if (requester != null && requester.isOnline()) {
                startTrade(requester, player);
                player.sendMessage("§6거래를 수락했습니다.");
                requester.sendMessage("§6" + player.getDisplayName() + "님이 거래를 수락했습니다.");
                pendingTrades.remove(player.getUniqueId());
            }
        }
    }

    public void denyTrade(Player player) {
        UUID requesterUUID = pendingTrades.get(player.getUniqueId());
        if (requesterUUID != null) {
            Player requester = Bukkit.getPlayer(requesterUUID);
            if (requester != null && requester.isOnline()) {
                player.sendMessage("§c거래를 거절했습니다.");
                requester.sendMessage("§c" + player.getDisplayName() + "님이 거래를 거절했습니다.");
                pendingTrades.remove(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        String title = event.getView().getTitle();

        if (title.equals("거래 요청자 선택")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() == Material.PLAYER_HEAD) {
                SkullMeta skullMeta = (SkullMeta) clickedItem.getItemMeta();
                Player target = Bukkit.getPlayer(skullMeta.getOwningPlayer().getUniqueId());

                if (target != null) {
                    pendingTrades.put(target.getUniqueId(), player.getUniqueId());
                    player.closeInventory();
                    player.sendMessage("§6" + target.getDisplayName() + "에게 거래 요청을 보냈습니다.");
                    target.sendMessage("§6" + player.getDisplayName() + "님이 거래 요청을 보냈습니다. " +
                            "§a[수락]§r §c[거절]");
                    // 명령어를 실행하도록 텍스트 설정
                    target.spigot().sendMessage(createClickableMessage("/lqd trade confirm", "/lqd trade deny"));
                }
            }
        } else if (title.equals("거래")) {
            TradeSession session = tradeSessions.get(player.getUniqueId());
            if (session != null && clickedInventory.equals(session.getTradeInventory())) {
                event.setCancelled(true); // 모든 클릭 기본 취소
                int slot = event.getSlot();

                if (session.isRequester()) {
                    if (slot >= 0 && slot < 27 && slot % 9 < 3) { // 거래 요청자 영역
                        if (!session.isRequesterConfirmed()) {
                            event.setCancelled(false);
                        }
                    } else if (slot >= 27 && slot <= 29) { // 거래 요청자의 템 확정 버튼
                        session.confirmRequester();
                        player.sendMessage("§6거래 요청자가 아이템을 확정했습니다.");
                    }
                } else {
                    if (slot >= 0 && slot < 27 && slot % 9 > 5) { // 거래 수신자 영역
                        if (!session.isReceiverConfirmed()) {
                            event.setCancelled(false);
                        }
                    } else if (slot >= 33 && slot <= 35) { // 거래 수신자의 템 확정 버튼
                        session.confirmReceiver();
                        player.sendMessage("§6거래 수신자가 아이템을 확정했습니다.");
                    }
                }

                if (slot >= 3 && slot <= 5 && session.isRequesterConfirmed() && session.isReceiverConfirmed()) { // 거래 완료 버튼
                    session.finalizeTrade();
                    player.sendMessage("§6거래가 완료되었습니다.");
                    tradeSessions.remove(session.getRequester().getUniqueId());
                    tradeSessions.remove(session.getReceiver().getUniqueId());
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        TradeSession session = tradeSessions.get(player.getUniqueId());

        if (session != null) {
            session.cancelTrade();
            tradeSessions.remove(session.getRequester().getUniqueId());
            tradeSessions.remove(session.getReceiver().getUniqueId());
            session.getRequester().closeInventory();
            session.getReceiver().closeInventory();
            player.sendMessage("§c거래가 취소되었습니다.");
        }
    }

    private net.md_5.bungee.api.chat.TextComponent createClickableMessage(String confirmCommand, String denyCommand) {
        net.md_5.bungee.api.chat.TextComponent message = new net.md_5.bungee.api.chat.TextComponent();
        net.md_5.bungee.api.chat.TextComponent confirm = new net.md_5.bungee.api.chat.TextComponent("§a[수락]");
        confirm.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, confirmCommand));
        net.md_5.bungee.api.chat.TextComponent deny = new net.md_5.bungee.api.chat.TextComponent("§c[거절]");
        deny.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, denyCommand));
        message.addExtra(confirm);
        message.addExtra(" ");
        message.addExtra(deny);
        return message;
    }
}
