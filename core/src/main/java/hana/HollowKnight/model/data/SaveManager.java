package hana.HollowKnight.model.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * مدیpsریت ذخیره/بارگذاری بازی با پایگاه‌داده‌ی SQLite (به‌جای فایل JSON).
 * دیتابیس یک فایل تکی (saves/hollowknight.db) کنار بازی ذخیره می‌شود.
 *
 * جدول‌ها:
 *  - saves            : یک ردیف به‌ازای هر اسلات (وضعیت کلی بازیکن)
 *  - save_charms      : چارم‌های باز/سوارشده‌ی هر اسلات (رابطه‌ی یک‌به‌چند)
 *  - save_achievements: دستاوردهای بازشده‌ی هر اسلات (رابطه‌ی یک‌به‌چند)
 */
public class SaveManager {

    public static final int SLOT_COUNT = 4;

    private final String dbUrl;

    public SaveManager() {
        // مطمئن می‌شویم پوشه‌ی saves وجود دارد، بعد آدرس فایل دیتابیس را می‌سازیم
        Gdx.files.local("saves/").file().mkdirs();
        this.dbUrl = "jdbc:sqlite:" + Gdx.files.local("saves/hollowknight.db").file().getAbsolutePath();
        initDatabase();
    }

    private Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(dbUrl);
        // برای فعال بودن ON DELETE CASCADE باید این pragma روی هر کانکشن ست بشه
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }

    private void initDatabase() {
        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS saves (" +
                "slot INTEGER PRIMARY KEY," +
                "save_date TEXT," +
                "room_id TEXT," +
                "player_x REAL," +
                "player_y REAL," +
                "health INTEGER," +
                "max_health INTEGER," +
                "soul INTEGER," +
                "playtime_seconds REAL," +
                "enemies_killed INTEGER)");

            st.execute("CREATE TABLE IF NOT EXISTS save_charms (" +
                "slot INTEGER NOT NULL," +
                "charm_name TEXT NOT NULL," +
                "equipped INTEGER NOT NULL," +
                "PRIMARY KEY (slot, charm_name)," +
                "FOREIGN KEY (slot) REFERENCES saves(slot) ON DELETE CASCADE)");

            st.execute("CREATE TABLE IF NOT EXISTS save_achievements (" +
                "slot INTEGER NOT NULL," +
                "achievement_name TEXT NOT NULL," +
                "PRIMARY KEY (slot, achievement_name)," +
                "FOREIGN KEY (slot) REFERENCES saves(slot) ON DELETE CASCADE)");

            st.execute("CREATE TABLE IF NOT EXISTS save_defeated_bosses (" +
                "slot INTEGER NOT NULL," +
                "boss_name TEXT NOT NULL," +
                "PRIMARY KEY (slot, boss_name)," +
                "FOREIGN KEY (slot) REFERENCES saves(slot) ON DELETE CASCADE)");
        } catch (SQLException e) {
            throw new GdxRuntimeException("Failed to initialize save database", e);
        }
    }

    public void save(int slot, GameData data) {
        data.saveDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());

        String upsertSql = "INSERT INTO saves " +
            "(slot, save_date, room_id, player_x, player_y, health, max_health, soul, playtime_seconds, enemies_killed) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?) " +
            "ON CONFLICT(slot) DO UPDATE SET " +
            "save_date=excluded.save_date, room_id=excluded.room_id, player_x=excluded.player_x, " +
            "player_y=excluded.player_y, health=excluded.health, max_health=excluded.max_health, " +
            "soul=excluded.soul, playtime_seconds=excluded.playtime_seconds, enemies_killed=excluded.enemies_killed";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(upsertSql)) {
                    ps.setInt(1, slot);
                    ps.setString(2, data.saveDate);
                    ps.setString(3, data.roomId);
                    ps.setFloat(4, data.playerX);
                    ps.setFloat(5, data.playerY);
                    ps.setInt(6, data.playerHealth);
                    ps.setInt(7, data.playerMaxHealth);
                    ps.setInt(8, data.playerSoul);
                    ps.setFloat(9, data.playtimeSeconds);
                    ps.setInt(10, data.enemiesKilled);
                    ps.executeUpdate();
                }

                replaceChildRows(conn, "DELETE FROM save_charms WHERE slot = ?",
                    "INSERT INTO save_charms (slot, charm_name, equipped) VALUES (?,?,?)",
                    slot, data.unlockedCharms, data.equippedCharms);

                replaceAchievementRows(conn, slot, data.unlockedAchievements);

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new GdxRuntimeException("Failed to save slot " + slot, e);
            }
        } catch (SQLException e) {
            throw new GdxRuntimeException("Failed to save slot " + slot, e);
        }
    }

    private void replaceChildRows(Connection conn, String deleteSql, String insertSql,
                                  int slot, List<String> unlocked, List<String> equipped) throws SQLException {
        try (PreparedStatement del = conn.prepareStatement(deleteSql)) {
            del.setInt(1, slot);
            del.executeUpdate();
        }
        if (unlocked == null || unlocked.isEmpty()) return;
        try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
            for (String charmName : unlocked) {
                ins.setInt(1, slot);
                ins.setString(2, charmName);
                ins.setInt(3, equipped != null && equipped.contains(charmName) ? 1 : 0);
                ins.addBatch();
            }
            ins.executeBatch();
        }
    }

    private void replaceAchievementRows(Connection conn, int slot, List<String> achievements) throws SQLException {
        try (PreparedStatement del = conn.prepareStatement("DELETE FROM save_achievements WHERE slot = ?")) {
            del.setInt(1, slot);
            del.executeUpdate();
        }
        if (achievements == null || achievements.isEmpty()) return;
        try (PreparedStatement ins = conn.prepareStatement(
            "INSERT INTO save_achievements (slot, achievement_name) VALUES (?,?)")) {
            for (String id : achievements) {
                ins.setInt(1, slot);
                ins.setString(2, id);
                ins.addBatch();
            }
            ins.executeBatch();
        }
    }

    private void replaceDefeatedBossRows(Connection conn, int slot, List<String> bosses) throws SQLException {
        try (PreparedStatement del = conn.prepareStatement("DELETE FROM save_defeated_bosses WHERE slot = ?")) {
            del.setInt(1, slot);
            del.executeUpdate();
        }
        if (bosses == null || bosses.isEmpty()) return;
        try (PreparedStatement ins = conn.prepareStatement(
            "INSERT INTO save_defeated_bosses (slot, boss_name) VALUES (?,?)")) {
            for (String bossName : bosses) {
                ins.setInt(1, slot);
                ins.setString(2, bossName);
                ins.addBatch();
            }
            ins.executeBatch();
        }
    }

    public GameData load(int slot) {
        String selectSql = "SELECT * FROM saves WHERE slot = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setInt(1, slot);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                GameData data = new GameData();
                data.saveDate = rs.getString("save_date");
                data.roomId = rs.getString("room_id");
                data.playerX = rs.getFloat("player_x");
                data.playerY = rs.getFloat("player_y");
                data.playerHealth = rs.getInt("health");
                data.playerMaxHealth = rs.getInt("max_health");
                data.playerSoul = rs.getInt("soul");
                data.playtimeSeconds = rs.getFloat("playtime_seconds");
                data.enemiesKilled = rs.getInt("enemies_killed");

                loadCharms(conn, slot, data);
                loadAchievements(conn, slot, data);
                return data;
            }
        } catch (SQLException e) {
            throw new GdxRuntimeException("Failed to load slot " + slot, e);
        }
    }

    private void loadCharms(Connection conn, int slot, GameData data) throws SQLException {
        data.unlockedCharms = new ArrayList<>();
        data.equippedCharms = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
            "SELECT charm_name, equipped FROM save_charms WHERE slot = ?")) {
            ps.setInt(1, slot);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("charm_name");
                    data.unlockedCharms.add(name);
                    if (rs.getInt("equipped") == 1) {
                        data.equippedCharms.add(name);
                    }
                }
            }
        }
    }

    private void loadAchievements(Connection conn, int slot, GameData data) throws SQLException {
        data.unlockedAchievements = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
            "SELECT achievement_name FROM save_achievements WHERE slot = ?")) {
            ps.setInt(1, slot);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data.unlockedAchievements.add(rs.getString("achievement_name"));
                }
            }
        }
    }


    public boolean hasSave(int slot) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM saves WHERE slot = ?")) {
            ps.setInt(1, slot);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new GdxRuntimeException("Failed to check slot " + slot, e);
        }
    }

}
