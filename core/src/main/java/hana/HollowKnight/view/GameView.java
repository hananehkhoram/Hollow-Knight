package hana.HollowKnight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import hana.HollowKnight.controller.GameController;
import hana.HollowKnight.controller.InputHandler;
import hana.HollowKnight.model.entities.BossModel;
import hana.HollowKnight.model.entities.CrawlerModel;
import hana.HollowKnight.model.entities.FlyModel;
import hana.HollowKnight.model.entities.PlayerModel;
import hana.HollowKnight.model.map.BossArena;
import hana.HollowKnight.model.map.RoomModel;
import hana.HollowKnight.view.audio.AudioManager;
import hana.HollowKnight.view.hud.GameHUD;
import hana.HollowKnight.view.overlays.InventoryMenu;
import hana.HollowKnight.view.overlays.PauseMenu;
import hana.HollowKnight.view.overlays.AchievementPopup;
import hana.HollowKnight.view.overlays.ZoteDialog;
import hana.HollowKnight.view.renderers.*;
import hana.HollowKnight.view.screens.BaseScreen;

public class GameView extends BaseScreen {

    private final MapRenderer mapRenderer = new MapRenderer();
    private GameHUD hud;
    private final PlayerModel player = controller.getModel().getPlayer();
    private PlayerRenderer playerRenderer;
    private CollisionDebugRenderer debugRenderer;
    private PauseMenu pauseOverlay;
    private ZoteDialog zoteOverlay;
    private InventoryMenu inventoryOverlay;
    private AchievementPopup achievementPopup;
    private boolean isPaused = false;

    private final CrawlerRenderer mosscreepRenderer = new CrawlerRenderer("mosscreep");
    private final CrawlerRenderer tiktikRenderer = new CrawlerRenderer("tiktik");
    private final ZoteRenderer zoteRenderer = new  ZoteRenderer();
    private final FlyRenderer flyRenderer = new FlyRenderer();
    private final BossRenderer bossRenderer = new BossRenderer();

    private enum OverlayType { NONE, PAUSE, INVENTORY }
    private OverlayType currentOverlay = OverlayType.NONE;

    public GameView(GameController controller) {
        super(controller);
        playerRenderer = new PlayerRenderer();
    }

    @Override
    public void show() {
        debugRenderer = new CollisionDebugRenderer();
        hud = new GameHUD();
        playerRenderer = new PlayerRenderer();
        pauseOverlay = new PauseMenu(controller, this::resumeFromPause);
        inventoryOverlay = new InventoryMenu(controller, this::resumeFromPause);
        achievementPopup = new AchievementPopup(batch);
        controller.getModel().getStats().addListener(achievementPopup);
        isPaused = false;

        String targetMap = (controller.getModel().getRoomPath() != null)
            ? controller.getModel().getRoomPath()
            : "maps/City of Tears-20260707T215923Z-3-001/cityOfTears1.tmx";

        loadNewRoom(targetMap);
        zoteOverlay = new ZoteDialog(controller, controller.getZote());
        camera.position.set(player.getX(), player.getY(), 0);
        camera.zoom = 1.6f;
        camera.update();
    }

    private void loadNewRoom(String mapPath) {
        mapRenderer.load(mapPath);
        controller.initRoom(mapPath, mapRenderer.getMap(), mapRenderer);
        if (mapPath.contains("Greenpath")) {
            AudioManager.getInstance().playGreenpathSound();
        } else {
            AudioManager.getInstance().playCityOfTearsSound();
        }
    }

    private void togglePause(boolean pauseMenu) {
        isPaused = !isPaused;
        audioManager.clickMenuSound();

        if (isPaused) {
            if (pauseMenu) {
                currentOverlay = OverlayType.PAUSE;
                pauseOverlay.showMainPanel();
                Gdx.input.setInputProcessor(pauseOverlay.getStage());
            } else {
                currentOverlay = OverlayType.INVENTORY;
                inventoryOverlay.show();
                Gdx.input.setInputProcessor(inventoryOverlay.getStage());
            }
        } else {
            currentOverlay = OverlayType.NONE;
            Gdx.input.setInputProcessor(null);
        }
    }

    private void resumeFromPause() {
        isPaused = false;
        currentOverlay = OverlayType.NONE;
        audioManager.clickMenuSound();
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {
        if (InputHandler.getInstance().isJustPressed(InputHandler.PlayerAction.PAUSE)) {
            togglePause(true);
        } else if  (InputHandler.getInstance().isJustPressed(InputHandler.PlayerAction.OPEN_INVENTORY)) {
            togglePause(false);
        }
        AudioManager.getInstance().update(delta);

        boolean talkingToZote = false;
        if (!isPaused) {
            zoteOverlay.update(controller.isInZoteArea());
            talkingToZote = zoteOverlay.isTalking();
            if (talkingToZote) {
                controller.getZote().update(delta);
            }
        }

        if (!isPaused && !talkingToZote) {
            if (controller.hasPendingRoomChange()) {
                String nextMap = controller.consumePendingMapPath();
                loadNewRoom(nextMap);
                camera.position.set(player.getX(), player.getY(), 0);
            } else {
                controller.updateGameplay(delta);
            }
        }

        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        RoomModel currentRoom = controller.getCurrentRoom();

        if (!isPaused && !talkingToZote) {
            updateCamera(delta, currentRoom);
        }

        mapRenderer.renderAllExcept(camera, "for");
        mapRenderer.renderAllExcept(camera, "secret room");

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (CrawlerModel crawler : controller.getMosscreeps()) {
            mosscreepRenderer.render(batch, crawler);
        }
        for (CrawlerModel crawler : controller.getTiktiks()) {
            tiktikRenderer.render(batch, crawler);
        }
        for (FlyModel fly : controller.getFlies()) {
            flyRenderer.render(batch, fly);
        }
        for (BossModel boss : controller.getBosses()) {
            bossRenderer.render(batch, boss);
        }
        zoteRenderer.render(batch, controller.getZote());
        renderPlayer();

        batch.end();

        mapRenderer.renderLayer(camera, "for");
        mapRenderer.renderLayer(camera, "secret room");
//        debugRenderer.render(camera, player, currentRoom.getSolidTiles(), currentRoom.getHazards(),
//            controller.getMosscreeps(), controller.getFlies(), controller.getTiktiks(), controller.getBosses());
        hud.render(batch, player.getHealth(), player.getMaxHealth(), player.getSoul(), player.getMaxSoul());
        drawBrightnessOverlay();
        achievementPopup.render(delta);

        if (controller.isInZoteArea()){
            zoteOverlay.render(delta);
        }

        if (isPaused) {
            if (currentOverlay == OverlayType.PAUSE) {
                pauseOverlay.render(delta);
            } else if (currentOverlay == OverlayType.INVENTORY) {
                inventoryOverlay.render(delta);
            }
        }
    }

    private void renderPlayer() {
        if (player.isInvincible()) {
            if (MathUtils.sin(player.getInvulnerabilityTimer() * 25f) > 0) {
                playerRenderer.render(batch, player);
            }
        } else {
            playerRenderer.render(batch, player);
        }
    }

    private void updateCamera(float delta, RoomModel currentRoom) {
        float targetX = player.getX();
        float targetY = player.getY() + 200;
        float lerp = Math.min(1f, 6f * delta);

        camera.position.x += (targetX - camera.position.x) * lerp;
        camera.position.y += (targetY - camera.position.y) * lerp;

        BossArena arena = currentRoom.getBossArena();
        float minX, minY, maxX, maxY;
        if (arena != null && arena.isLocked() && arena.getBounds() != null) {
            minX = arena.getBounds().x;
            minY = arena.getBounds().y;
            maxX = arena.getBounds().x + arena.getBounds().width;
            maxY = arena.getBounds().y + arena.getBounds().height;
        } else {
            minX = currentRoom.getMinX();
            minY = currentRoom.getMinY();
            maxX = currentRoom.getMaxX();
            maxY = currentRoom.getMaxY();
        }

        float halfWidth = camera.viewportWidth * camera.zoom / 2f;
        float halfHeight = camera.viewportHeight * camera.zoom / 2f;

        if (maxX - minX > halfWidth * 2f) {
            camera.position.x = Math.max(minX + halfWidth, Math.min(camera.position.x, maxX - halfWidth));
        } else {
            camera.position.x = (minX + maxX) / 2f;
        }

        if (maxY - minY > halfHeight * 2f) {
            camera.position.y = Math.max(minY + halfHeight, Math.min(camera.position.y, maxY - halfHeight));
        } else {
            camera.position.y = (minY + maxY) / 2f;
        }

        camera.update();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height, false);
        if (hud != null) hud.resize(width, height);
        if (pauseOverlay != null) pauseOverlay.resize(width, height);
        if (achievementPopup != null) achievementPopup.resize(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (hud != null) hud.dispose();
        if (pauseOverlay != null) pauseOverlay.dispose();
        if (achievementPopup != null) achievementPopup.dispose();
        if (zoteOverlay != null) zoteOverlay.dispose();
        zoteRenderer.dispose();
        mapRenderer.dispose();
        playerRenderer.dispose();
        mosscreepRenderer.dispose();
        flyRenderer.dispose();
        bossRenderer.dispose();
    }
}
