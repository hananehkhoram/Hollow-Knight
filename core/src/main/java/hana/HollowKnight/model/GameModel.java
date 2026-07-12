package hana.HollowKnight.model;

import hana.HollowKnight.model.data.GameData;
import hana.HollowKnight.model.data.SaveManager;
import hana.HollowKnight.model.entities.PlayerModel;
import hana.HollowKnight.model.items.Charm;
import hana.HollowKnight.model.items.CharmType;
import hana.HollowKnight.model.stats.GameStats;

import java.util.HashMap;

public class GameModel {
    private PlayerModel player;
    private GameStats gameStats;
    private String currentRoomId;
    private final SaveManager saveManager;
    private int activeSlot = -1;
    private boolean bossDefeated = false;
    private HashMap<CharmType, Charm> charms = new HashMap<>();

    public GameModel() {
        this.player = new PlayerModel();
        this.gameStats = new GameStats();
        this.saveManager = new SaveManager();
        for (CharmType charmType : CharmType.values()) {
            charms.put(charmType, Charm.charmFactory(charmType, player));
        }
    }

    public HashMap<CharmType, Charm> getCharms() {
        return charms;
    }

    public void save(int slot) {
        GameData data = new GameData();
        data.roomId = currentRoomId;
        data.playerX = player.getX();
        data.playerY = player.getY();
        data.playerHealth = player.getHealth();
        data.playerMaxHealth = player.getMaxHealth();
        data.playerSoul = player.getSoul();
        data.unlockedCharms = player.getUnlockedCharmNames();
        data.equippedCharms = player.getEquippedCharmNames();
        data.unlockedAchievements = gameStats.getUnlockedAchievementIds();
        data.playtimeSeconds = gameStats.getPlaytimeSeconds();
        data.enemiesKilled = gameStats.getEnemiesKilled();
        data.bossDefeated = bossDefeated;

        saveManager.save(slot, data);
        activeSlot = slot;
    }

    public boolean load(int slot) {
        GameData data = saveManager.load(slot);
        if (data == null) return false;

        currentRoomId = data.roomId;
        player.setPosition(data.playerX, data.playerY);
        player.setHealth(data.playerHealth);
        player.setMaxHealth(data.playerMaxHealth);
        player.setSoul(data.playerSoul);
        player.setUnlockedCharmsByName(data.unlockedCharms);
        player.setEquippedCharmsByName(data.equippedCharms);
        gameStats.setUnlockedAchievementIds(data.unlockedAchievements);
        gameStats.setPlaytimeSeconds(data.playtimeSeconds);
        gameStats.setEnemiesKilled(data.enemiesKilled);
        bossDefeated = data.bossDefeated;

        activeSlot = slot;
        return true;
    }

    public boolean hasSave(int slot) { return saveManager.hasSave(slot); }
    public GameData peekSave(int slot) { return saveManager.load(slot); }
    public int getActiveSlot() { return activeSlot; }
    public void setActiveSlot(int slot) { this.activeSlot = slot; }
    public PlayerModel getPlayer() { return player; }
    public GameStats getStats() { return gameStats; }

    public String getRoomPath() {
        return currentRoomId;
    }

    public void setRoomPath(String roomPath) {
        this.currentRoomId = roomPath;
    }

    public boolean isBossDefeated() {
        return bossDefeated;
    }

    public void setBossDefeated(boolean bossDefeated) {
        this.bossDefeated = bossDefeated;
    }
}
