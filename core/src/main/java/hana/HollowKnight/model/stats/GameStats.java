package hana.HollowKnight.model.stats;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * آمار یک بازی (playtime، تعداد کشته‌ها) و مدیریت دستاوردها (Achievements).
 * برای پاپ‌آپ دستاورد از الگوی Observer استفاده شده: هر شنونده (مثلا HUD) با
 * addListener ثبت‌نام می‌کند و به‌محض قفل بازشدن یک دستاورد باخبر می‌شود.
 */
public class GameStats {

    public enum Achievement {
        COMPLETION("Completion", "Finish the game."),
        SPEEDRUN("Speedrun", "Finish the game within the target time."),
        TRUE_HUNTER("True Hunter", "Kill every enemy type in the game."),
        DEFEAT_FALSE_KNIGHT("Defeat False Knight", "Defeat the False Knight boss."),
        NOOB ("The Noob", "Start the first game.");

        private final String title;
        private final String description;

        Achievement(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
    }

    /** برای پاپ‌آپ آنی دستاورد؛ View (مثلا HUDRenderer) این را پیاده و ثبت می‌کند. */
    public interface AchievementListener {
        void onAchievementUnlocked(Achievement achievement);
    }

    private static final float SPEEDRUN_TARGET_SECONDS = 15 * 60f; // ۱۵ دقیقه؛ خودت می‌تونی عوض کنی

    private float playtimeSeconds;
    private int enemiesKilled;
    private int deaths;

    private final Set<String> killedEnemyTypes = new LinkedHashSet<>();

    private final Set<Achievement> unlockedAchievements = EnumSet.noneOf(Achievement.class);
    private final List<AchievementListener> listeners = new ArrayList<>();

    // ==================== ثبت‌نام برای پاپ‌آپ ====================

    public void addListener(AchievementListener listener) {
        listeners.add(listener);
    }

    public void removeListener(AchievementListener listener) {
        listeners.remove(listener);
    }

    private void unlock(Achievement achievement) {
        if (unlockedAchievements.add(achievement)) {
            for (AchievementListener l : listeners) {
                l.onAchievementUnlocked(achievement);
            }
        }
    }


    public void update(float delta) {
        playtimeSeconds += delta;
    }

    public void recordEnemyKilled(String enemyTypeName) {
        enemiesKilled++;
        killedEnemyTypes.add(enemyTypeName);
    }

    public void recordDeath() {
        deaths++;
    }

    public void checkHunterAchievement(int totalEnemyTypesInGame) {
        if (killedEnemyTypes.size() >= totalEnemyTypesInGame) {
            unlock(Achievement.TRUE_HUNTER);
        }
    }

    public void defeatedBoss (){
        unlock(Achievement.DEFEAT_FALSE_KNIGHT);
    }

    public void onGameCompleted() {
        unlock(Achievement.COMPLETION);
        if (playtimeSeconds <= SPEEDRUN_TARGET_SECONDS) {
            unlock(Achievement.SPEEDRUN);
        }
    }

    public boolean isUnlocked(Achievement achievement) {
        return unlockedAchievements.contains(achievement);
    }

    public Set<Achievement> getUnlockedAchievements() {
        return unlockedAchievements;
    }

    public float getPlaytimeSeconds() { return playtimeSeconds; }
    public void setPlaytimeSeconds(float playtimeSeconds) { this.playtimeSeconds = playtimeSeconds; }

    public int getEnemiesKilled() { return enemiesKilled; }
    public void setEnemiesKilled(int enemiesKilled) { this.enemiesKilled = enemiesKilled; }

    public int getDeaths() { return deaths; }
    public void setDeaths(int deaths) { this.deaths = deaths; }

    public Set<String> getKilledEnemyTypes() { return killedEnemyTypes; }
    public void setKilledEnemyTypes(Set<String> types) {
        killedEnemyTypes.clear();
        if (types != null) killedEnemyTypes.addAll(types);
    }


    /** برای GameData: تبدیل enum ها به رشته جهت سریالایز با Json. */
    public List<String> getUnlockedAchievementIds() {
        List<String> ids = new ArrayList<>();
        for (Achievement a : unlockedAchievements) ids.add(a.name());
        return ids;
    }

    public void setUnlockedAchievementIds(List<String> ids) {
        unlockedAchievements.clear();
        if (ids == null) return;
        for (String id : ids) unlockedAchievements.add(Achievement.valueOf(id));
    }
}
