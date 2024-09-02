package com.liquidskr.liQuiDWorld.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TradeSession {
    private final Player requester;
    private final Player receiver;
    private final Inventory tradeInventory;
    private boolean requesterConfirmed;
    private boolean receiverConfirmed;
    private final boolean isRequester;

    public TradeSession(Player requester, Player receiver, Inventory tradeInventory, boolean isRequester) {
        this.requester = requester;
        this.receiver = receiver;
        this.tradeInventory = tradeInventory;
        this.isRequester = isRequester;
        this.requesterConfirmed = false;
        this.receiverConfirmed = false;
    }

    public Player getRequester() {
        return requester;
    }

    public Player getReceiver() {
        return receiver;
    }

    public Inventory getTradeInventory() {
        return tradeInventory;
    }

    public boolean isRequester() {
        return isRequester;
    }

    public boolean isRequesterConfirmed() {
        return requesterConfirmed;
    }

    public boolean isReceiverConfirmed() {
        return receiverConfirmed;
    }

    public void confirmRequester() {
        requesterConfirmed = true;
        updateFinalizeTradeButton();
    }

    public void confirmReceiver() {
        receiverConfirmed = true;
        updateFinalizeTradeButton();
    }

    public void finalizeTrade() {
        for (int i = 0; i < 27; i++) {
            if (i % 9 < 3) { // 거래 요청자 영역
                ItemStack item = tradeInventory.getItem(i);
                if (item != null) {
                    receiver.getInventory().addItem(item);
                    tradeInventory.setItem(i, null);
                }
            } else if (i % 9 > 5) { // 거래 수신자 영역
                ItemStack item = tradeInventory.getItem(i);
                if (item != null) {
                    requester.getInventory().addItem(item);
                    tradeInventory.setItem(i, null);
                }
            }
        }
    }

    public void cancelTrade() {
        for (int i = 0; i < 27; i++) {
            if (i % 9 < 3) { // 거래 요청자 영역
                ItemStack item = tradeInventory.getItem(i);
                if (item != null) {
                    requester.getInventory().addItem(item);
                    tradeInventory.setItem(i, null);
                }
            } else if (i % 9 > 5) { // 거래 수신자 영역
                ItemStack item = tradeInventory.getItem(i);
                if (item != null) {
                    receiver.getInventory().addItem(item);
                    tradeInventory.setItem(i, null);
                }
            }
        }
    }

    private void updateFinalizeTradeButton() {
        if (requesterConfirmed && receiverConfirmed) {
            // Change green stained glass pane to lime stained glass pane
            ItemStack finalizeTrade = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            ItemMeta finalizeTradeMeta = finalizeTrade.getItemMeta();
            finalizeTradeMeta.setDisplayName("§a교환하기");
            finalizeTrade.setItemMeta(finalizeTradeMeta);
            for (int i = 3; i <= 5; i++) {
                tradeInventory.setItem(i, finalizeTrade);
                tradeInventory.setItem(i + 9, finalizeTrade);
                tradeInventory.setItem(i + 18, finalizeTrade);
            }
        }
    }
}
