package com.liquidskr.liQuiDWorld.menu;

import com.liquidskr.liQuiDWorld.standard.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class MainMenuHandler implements Listener {
    private final JavaPlugin plugin;
    private final Map<UUID, PlayerData> playerDataMap;
    private final NicknameChangeHandler nicknameChangeHandler;
    private final GoldCheckHandler goldCheckHandler;
    private final GoldSendHandler goldSendHandler;
    private final GiftHandler giftHandler;
    private final StorageHandler storageHandler;

    public MainMenuHandler(JavaPlugin plugin, Map<UUID, PlayerData> playerDataMap) {
        this.plugin = plugin;
        this.playerDataMap = playerDataMap;
        this.nicknameChangeHandler = new NicknameChangeHandler(plugin, playerDataMap);
        this.goldCheckHandler = new GoldCheckHandler(plugin, playerDataMap);
        this.goldSendHandler = new GoldSendHandler(plugin, playerDataMap);
        this.giftHandler = new GiftHandler(plugin, playerDataMap);
        this.storageHandler = new StorageHandler(plugin, playerDataMap);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public Inventory createMainMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 36, "메인 메뉴");

        // 닉네임 변경 아이템 생성
        ItemStack nameChangeItem = new ItemStack(Material.NAME_TAG);
        ItemMeta nameChangeMeta = nameChangeItem.getItemMeta();
        nameChangeMeta.setDisplayName("닉네임 변경");

        List<String> nameChangeLore = new ArrayList<>();
        nameChangeLore.add("§7클릭하여 닉네임을 변경합니다.");
        nameChangeMeta.setLore(nameChangeLore);
        nameChangeItem.setItemMeta(nameChangeMeta);

        menu.setItem(0, nameChangeItem);

        // 골드 조회 버튼 생성 (금, 보유 골드, 금색)
        ItemStack goldCheckItem = new ItemStack(Material.GOLD_INGOT);
        ItemMeta goldCheckMeta = goldCheckItem.getItemMeta();
        goldCheckMeta.setDisplayName("§6보유 골드");

        List<String> goldCheckLore = new ArrayList<>();
        goldCheckLore.add("§7클릭하여 보유 골드를 확인합니다.");
        goldCheckMeta.setLore(goldCheckLore);
        goldCheckItem.setItemMeta(goldCheckMeta);

        menu.setItem(1, goldCheckItem);

        // 골드 보내기 버튼 생성 (금 말 갑옷, 골드 보내기, 금색)
        ItemStack goldSendItem = new ItemStack(Material.GOLDEN_HORSE_ARMOR);
        ItemMeta goldSendMeta = goldSendItem.getItemMeta();
        goldSendMeta.setDisplayName("§6골드 보내기");

        List<String> goldSendLore = new ArrayList<>();
        goldSendLore.add("§7클릭하여 골드를 보냅니다.");
        goldSendMeta.setLore(goldSendLore);
        goldSendItem.setItemMeta(goldSendMeta);

        menu.setItem(2, goldSendItem);

        // 선물하기 버튼 생성 (상자가 실린 벚나무 보트, 선물하기, 금색)
        ItemStack giftItem = new ItemStack(Material.CHERRY_CHEST_BOAT);
        ItemMeta giftMeta = giftItem.getItemMeta();
        giftMeta.setDisplayName("§6선물하기");

        List<String> giftLore = new ArrayList<>();
        giftLore.add("§7클릭하여 선물을 보냅니다.");
        giftMeta.setLore(giftLore);
        giftItem.setItemMeta(giftMeta);

        menu.setItem(3, giftItem);

        // 보관함 열기 버튼 생성 (조각된 책장, 보관함 열기, 금색)
        ItemStack storageItem = new ItemStack(Material.CHISELED_BOOKSHELF);
        ItemMeta storageMeta = storageItem.getItemMeta();
        storageMeta.setDisplayName("§6보관함 열기");

        List<String> storageLore = new ArrayList<>();
        storageLore.add("§7클릭하여 보관함을 엽니다.");
        storageMeta.setLore(storageLore);
        storageItem.setItemMeta(storageMeta);

        menu.setItem(4, storageItem);

        return menu;
    }

    // 플레이어가 웅크린 상태에서 양손 바꾸기 키를 눌렀을 때 메뉴 열기
    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (player.isSneaking()) {
            event.setCancelled(true); // 양손 바꾸기 기능을 비활성화합니다.
            Inventory menu = createMainMenu(player);
            player.openInventory(menu);
        }
    }

    // 메뉴 아이템 클릭 이벤트 처리
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().equals("메인 메뉴")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || clickedItem.getType() == Material.AIR) {
                return;
            }

            if (clickedItem.getType() == Material.NAME_TAG && clickedItem.getItemMeta().getDisplayName().equals("닉네임 변경")) {
                nicknameChangeHandler.startNicknameChange(player);
            }

            if (clickedItem.getType() == Material.GOLD_INGOT && clickedItem.getItemMeta().getDisplayName().equals("§6보유 골드")) {
                goldCheckHandler.checkGold(player);
            }

            if (clickedItem.getType() == Material.GOLDEN_HORSE_ARMOR && clickedItem.getItemMeta().getDisplayName().equals("§6골드 보내기")) {
                goldSendHandler.openMenu(player);
            }

            if (clickedItem.getType() == Material.CHERRY_CHEST_BOAT && clickedItem.getItemMeta().getDisplayName().equals("§6선물하기")) {
                giftHandler.startGiftProcess(player);
            }

            if (clickedItem.getType() == Material.CHISELED_BOOKSHELF && clickedItem.getItemMeta().getDisplayName().equals("§6보관함 열기")) {
                storageHandler.openStorage(player);
            }
        }
    }
}
