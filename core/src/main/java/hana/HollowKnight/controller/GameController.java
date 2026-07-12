package hana.HollowKnight.controller;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import hana.HollowKnight.model.GameModel;
import hana.HollowKnight.model.data.GameData;
import hana.HollowKnight.model.entities.*;
import hana.HollowKnight.model.items.CharmType;
import hana.HollowKnight.model.map.BossArena;
import hana.HollowKnight.model.map.PortalModel;
import hana.HollowKnight.model.map.RoomModel;
import hana.HollowKnight.view.GameView;
import hana.HollowKnight.view.audio.AudioManager;
import hana.HollowKnight.view.renderers.MapRenderer;
import hana.HollowKnight.view.screens.*;

import java.util.ArrayList;

public class GameController {

    private static final int HAZARD_DAMAGE = 1;

    public static float brightness = 1.0f;

    private final Game game;
    private final SpriteBatch batch;
    private GameModel model;
    private GameView activeGameView;

    private final AIController aiController = new AIController(this);
    private final BossAIController bossAIController = new BossAIController();

    private RoomModel currentRoom;
    private CollisionController collision;
    private ArrayList<CrawlerModel> mosscreeps = new ArrayList<>();
    private ArrayList<CrawlerModel> tiktiks = new ArrayList<>();
    private ArrayList<FlyModel> flies = new ArrayList<>();
    private ArrayList<BossModel> bosses = new ArrayList<>();
    private ZoteModel zote;
    private String zoteMapPath;

    private String currentMapPath;
    private String pendingMapPath;
    private float pendingSpawnX, pendingSpawnY;
    private boolean pendingUseRoomSpawn = true;
    private boolean pendingSpawnAtBossArena = false;

    public GameController(Game game, SpriteBatch batch) {
        this.game = game;
        this.batch = batch;
        this.model = new GameModel();
    }

    public void initRoom(String mapPath, TiledMap map, MapRenderer mapRenderer) {
        PlayerModel player = model.getPlayer();

        currentRoom = RoomLoader.load(map, mapPath);

        // Only make a fresh Zote when we're actually entering a different room than the one
        // he's currently in — otherwise every death-respawn or portal-back-into-the-same-room
        // wiped his talkingId/rulesId/hasEverTalked progress back to zero.
        if (zote == null || !mapPath.equals(zoteMapPath)) {
            zote = new ZoteModel(currentRoom.getZoteSpawn().x, currentRoom.getZoteSpawn().y);
            zoteMapPath = mapPath;
        }

        collision = new CollisionController(player, currentRoom.getHazards(),
            currentRoom.getBreakableWall(), currentRoom.getPortal(), mapRenderer, zote);

        mosscreeps = RoomLoader.spawnCrawlers(currentRoom, "mosscreep");
        tiktiks = RoomLoader.spawnCrawlers(currentRoom, "tiktik");
        currentRoom.setCrawlers(mosscreeps);
        flies = RoomLoader.spawnFlies(currentRoom, player);
        bosses = RoomLoader.spawnBoss(currentRoom);

        currentMapPath = mapPath;

        if (pendingSpawnAtBossArena) {
            BossArena arena = currentRoom.getBossArena();
            if (arena != null && arena.getBounds() != null) {
                player.setPosition(arena.getBounds().x, arena.getBounds().y);
            } else {
                Vector2 spawn = currentRoom.getKnightSpawn();
                player.setPosition(spawn.x, spawn.y);
            }
        } else if (pendingUseRoomSpawn) {
            Vector2 spawn = currentRoom.getKnightSpawn();
            player.setPosition(spawn.x, spawn.y);
        } else {
            player.setPosition(pendingSpawnX, pendingSpawnY);
        }
        player.savePrevPosition();

        pendingMapPath = null;
        pendingUseRoomSpawn = true;
        pendingSpawnAtBossArena = false;
    }

    public void teleportToBossArena(String fallbackMapPath) {
        BossArena arena = (currentRoom != null) ? currentRoom.getBossArena() : null;
        if (arena != null && arena.getBounds() != null) {
            model.getPlayer().setPosition(arena.getBounds().x, arena.getBounds().y);
            model.getPlayer().savePrevPosition();
            return;
        }
        pendingSpawnAtBossArena = true;
        pendingMapPath = fallbackMapPath;
    }

    public boolean hasPendingRoomChange() {
        return pendingMapPath != null;
    }

    public String consumePendingMapPath() {
        return pendingMapPath;
    }

    private void requestRoomChange(String mapPath, float x, float y) {
        pendingMapPath = mapPath;
        pendingSpawnX = x;
        pendingSpawnY = y;
        pendingUseRoomSpawn = false;
    }

    private void requestRoomChange(String mapPath) {
        pendingMapPath = mapPath;
        pendingUseRoomSpawn = true;
    }


    public void updateGameplay(float delta) {
        PlayerModel player = model.getPlayer();

        if (player.isJustDead()) {
            player.setHealth(player.getMaxHealth());
            player.setSoul(0);
            player.setAlive(true);
            player.setJustDead(false);
            model.getStats().recordDeath();
            requestRoomChange(currentMapPath);
            return;
        }

        if (!player.isAlive()) {
            player.update(delta);
            return;
        }
        if (zote != null){
            zote.update(delta);
            collision.updateMovementZote(delta, currentRoom.getSolidTiles());
        }

        InputHandler.getInstance().update(player);
        InputHandler.getInstance().updateCheat(delta, this);
        if (collision == null || currentRoom == null) return;

        if (!bosses.isEmpty() && !bosses.getFirst().isAlive()) {
            model.getStats().defeatedBoss();
            model.getStats().onGameCompleted();
            endGame(player);
        }
        model.getStats().checkHunterAchievement(3);

        PortalModel triggeredPortal = collision.checkPortalCollision();
        if (triggeredPortal != null) {
            requestRoomChange(triggeredPortal.getTargetMapPath(),
                triggeredPortal.getTargetX(), triggeredPortal.getTargetY());
            return;
        }

        player.savePrevPosition();
        model.getStats().update(delta);

        collision.updateMovement(delta, currentRoom.getSolidTiles(), currentRoom.getWalls());
        player.update(delta);
        collision.checkHazardCollisions(HAZARD_DAMAGE);
        collision.checkAttackOnBreakable();


        for (CrawlerModel crawler : mosscreeps) {
            aiController.updateCrawler(crawler, delta, currentRoom.getSolidTiles(), player);
        }
        for (CrawlerModel crawler : tiktiks) {
            aiController.updateCrawler(crawler, delta, currentRoom.getSolidTiles(), player);
        }
        for (FlyModel fly : flies) {
            aiController.updateFly(fly, delta, currentRoom.getSolidTiles(), player);
        }
        for (BossModel boss : bosses) {
            bossAIController.updateBoss(boss, delta, player, currentRoom.getSolidTiles(), currentRoom.getBossArena());
            aiController.checkPlayerInteraction(boss, player, 1);
        }

        if (player.checkVoidHeart()){
            currentRoom.getVoidHeart().setVisible(false);
            model.getCharms().get(CharmType.VOID_HEART).setUnlocked(true);
        }

        updateBossArena();
    }

    private void updateBossArena() {
        BossArena arena = currentRoom.getBossArena();
        if (arena == null) return;

        if (!arena.isLocked() && arena.getTrigger() != null
            && arena.getTrigger().overlaps(model.getPlayer().getBounds()) && !bosses.isEmpty()) {
            arena.setLocked(true);
            arena.setGateVisibility(true);
            AudioManager.getInstance().playBossFightSound();
            currentRoom.getSolidTiles().addAll(arena.getGates());
        }

        if (arena.isLocked() && allBossesDead()) {
            arena.setLocked(false);
            arena.setGateVisibility(false);
            currentRoom.getSolidTiles().removeAll(arena.getGates(), true);
        }
    }

    private boolean allBossesDead() {
        for (BossModel boss : bosses) {
            if (!boss.isDead()) return false;
        }
        return true;
    }

    public boolean isInZoteArea(){
        PlayerModel player =  model.getPlayer();
        return player.getBounds().overlaps(zote.getBounds());
    }


    public RoomModel getCurrentRoom() {
        return currentRoom;
    }

    public ArrayList<CrawlerModel> getMosscreeps() {
        return mosscreeps;
    }

    public ArrayList<FlyModel> getFlies() {
        return flies;
    }

    public ArrayList<BossModel> getBosses() {
        return bosses;
    }

    public InputHandler getInputHandler() {
        return InputHandler.getInstance();
    }

    public void goToMainMenu() {
        game.setScreen(new MainMenuView(this));
    }

    public void openSettings() {
        game.setScreen(new SettingScreenView(this));
    }

    public void startGame() {
        game.setScreen(new StartGameScreen(this));
    }

    public void endGame(PlayerModel player) {
        AudioManager.getInstance().playGameOver();
        game.setScreen(new EndScreen(this, player));
    }

    public void openGuide() {
        game.setScreen(new GuideScreen(this));
    }

    public void openAchievements() {
        game.setScreen(new AchievementsScreen(this));
    }

    public void startNewGame(int slot) {
        model = new GameModel();
        model.setActiveSlot(slot);
        activeGameView = new GameView(this);
        game.setScreen(activeGameView);
        model.getStats().startFirstGame();
    }

    public void loadGame(int slot) {
        if (model.load(slot)) {
            activeGameView = new GameView(this);
            game.setScreen(activeGameView);
        }
    }

    public void saveGame(int slot) {
        model.save(slot);
    }

    public void saveCurrentAndExit() {
        if (model.getActiveSlot() != -1) {
            model.save(model.getActiveSlot());
        }
        AudioManager.getInstance().stopGreenpathSound();
        AudioManager.getInstance().stopCityofTears();
        AudioManager.getInstance().playMenuSound();
        goToMainMenu();
    }

    public boolean hasSave(int slot) {
        return model.hasSave(slot);
    }

    public GameData peekSave(int slot) {
        return model.peekSave(slot);
    }

    public void openKeyboardSetting() {
        game.setScreen(new KeyboardSetting(this));
    }

    public void refreshCurrentScreen(String path) {
        Screen current = game.getScreen();
        BaseScreen.changeBackground(path);
        game.setScreen(new SettingScreenView(this));
        if (current != null) {
            current.dispose();
        }
    }


    public GameModel getModel() {
        return model;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public Game getGame() {
        return game;
    }

    public ArrayList<CrawlerModel> getTiktiks() {
        return tiktiks;
    }

    public ZoteModel getZote() {
        return zote;
    }

}
