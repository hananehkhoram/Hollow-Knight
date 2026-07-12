package hana.HollowKnight.model.data;

import java.util.ArrayList;
import java.util.List;

public class GameData {
    public String saveDate;
    public String roomId;

    public float playerX, playerY;
    public int playerHealth, playerMaxHealth, playerSoul;

    public List<String> unlockedCharms = new ArrayList<>();
    public List<String> equippedCharms = new ArrayList<>();
    public List<String> unlockedAchievements = new ArrayList<>();
    public float playtimeSeconds;
    public int enemiesKilled;

    public boolean bossDefeated;

    public GameData() {
    }
}
