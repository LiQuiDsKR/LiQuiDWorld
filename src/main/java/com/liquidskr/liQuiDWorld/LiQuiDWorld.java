package com.liquidskr.liQuiDWorld;

import com.liquidskr.liQuiDWorld.menu.MainMenuHandler;
import com.liquidskr.liQuiDWorld.menu.GiftHandler;
import com.liquidskr.liQuiDWorld.menu.StorageHandler;
import com.liquidskr.liQuiDWorld.menu.TradeHandler;
import com.liquidskr.liQuiDWorld.standard.PlayerData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventHandler;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public final class LiQuiDWorld extends JavaPlugin implements Listener {

    private Map<UUID, PlayerData> playerDataMap = new HashMap<>();
    private TradeHandler tradeHandler;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        // 플러그인 데이터 폴더가 존재하지 않으면 생성
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // 메뉴 핸들러 초기화
        GiftHandler giftHandler = new GiftHandler(this, playerDataMap);
        new MainMenuHandler(this, playerDataMap);
        new StorageHandler(this, playerDataMap);
        this.tradeHandler = new TradeHandler(this, playerDataMap);

        // 플러그인 활성화 시 플레이어 데이터 로드
        loadAllPlayerData();

        // 서버가 시작되거나 리로드될 때 현재 접속 중인 모든 플레이어의 데이터 로드 및 디스플레이 이름 설정
        for (Player player : getServer().getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            PlayerData playerData = loadPlayerData(uuid);
            if (playerData == null) {
                playerData = new PlayerData(player.getName());
                player.sendMessage("환영합니다! 새로운 플레이어 데이터가 생성되었습니다.");
            } else {
                player.sendMessage("다시 오신 것을 환영합니다!");
            }
            playerDataMap.put(uuid, playerData);

            // 플레이어의 디스플레이 이름 설정
            player.setDisplayName(playerData.getNickname());
        }

        // 15분마다 모든 플레이어 데이터를 저장하는 스케줄러 설정
        new BukkitRunnable() {
            @Override
            public void run() {
                saveAllPlayerData();
            }
        }.runTaskTimer(this, 18000L, 18000L); // 18000 틱 = 15분
    }

    @Override
    public void onDisable() {
        // 플러그인 비활성화 시 모든 플레이어 데이터 저장
        saveAllPlayerData();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // 플레이어 데이터 로드 또는 새로 생성
        PlayerData playerData = loadPlayerData(uuid);
        if (playerData == null) {
            playerData = new PlayerData(player.getName());
            player.sendMessage("환영합니다! 새로운 플레이어 데이터가 생성되었습니다.");
        } else {
            player.sendMessage("다시 오신 것을 환영합니다!");
        }

        playerDataMap.put(uuid, playerData);

        // 플레이어의 디스플레이 이름 설정
        player.setDisplayName(playerData.getNickname());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // 플레이어 퇴장 시 데이터 저장
        savePlayerData(uuid);
        playerDataMap.remove(uuid);
        player.sendMessage("플레이어 데이터가 저장되었습니다.");
    }

    private void saveAllPlayerData() {
        for (UUID uuid : playerDataMap.keySet()) {
            savePlayerData(uuid);
        }
        getLogger().info("모든 플레이어 데이터가 저장되었습니다.");
    }

    private void loadAllPlayerData() {
        // 모든 플레이어 데이터 파일 로드 로직 구현
        getLogger().info("모든 플레이어 데이터가 로드되었습니다.");
    }

    private PlayerData loadPlayerData(UUID uuid) {
        File playerDataFile = new File(getDataFolder(), uuid.toString() + ".dat");
        if (!playerDataFile.exists()) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(playerDataFile))) {
            return (PlayerData) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void savePlayerData(UUID uuid) {
        PlayerData playerData = playerDataMap.get(uuid);
        if (playerData == null) {
            return;
        }

        File playerDataFile = new File(getDataFolder(), uuid.toString() + ".dat");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(playerDataFile))) {
            oos.writeObject(playerData);
            getLogger().info("플레이어 데이터가 저장되었습니다: " + playerData.getNickname());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("lqd")) {
                if (args.length > 0) {
                    if (args[0].equalsIgnoreCase("trade")) {
                        if (args.length > 1) {
                            if (args[1].equalsIgnoreCase("confirm")) {
                                tradeHandler.confirmTrade(player);
                                return true;
                            } else if (args[1].equalsIgnoreCase("deny")) {
                                tradeHandler.denyTrade(player);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
