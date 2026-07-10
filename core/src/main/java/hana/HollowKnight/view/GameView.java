package hana.HollowKnight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import hana.HollowKnight.controller.GameController;
import hana.HollowKnight.model.entities.BossModel;
import hana.HollowKnight.model.entities.CrawlerModel;
import hana.HollowKnight.model.entities.FlyModel;
import hana.HollowKnight.model.entities.PlayerModel;
import hana.HollowKnight.model.map.BossArena;
import hana.HollowKnight.model.map.RoomModel;
import hana.HollowKnight.view.hud.GameHUD;
import hana.HollowKnight.view.renderers.BossRenderer;
import hana.HollowKnight.view.renderers.CollisionDebugRenderer;
import hana.HollowKnight.view.renderers.CrawlerRenderer;
import hana.HollowKnight.view.renderers.FlyRenderer;
import hana.HollowKnight.view.renderers.MapRenderer;
import hana.HollowKnight.view.renderers.PlayerRenderer;
import hana.HollowKnight.view.screens.BaseScreen;

public class GameView extends BaseScreen {

    private final MapRenderer mapRenderer = new MapRenderer();
    private GameHUD hud;
    private final PlayerModel player = controller.getModel().getPlayer();
    private PlayerRenderer playerRenderer;
    private CollisionDebugRenderer debugRenderer;

    private final CrawlerRenderer mosscreepRenderer = new CrawlerRenderer("mosscreep");
    private final CrawlerRenderer tiktikRenderer = new CrawlerRenderer("tiktik");
    private final FlyRenderer flyRenderer = new FlyRenderer();
    private final BossRenderer bossRenderer = new BossRenderer();

    public GameView(GameController controller) {
        super(controller);
        playerRenderer = new PlayerRenderer();
    }

    @Override
    public void show() {
        debugRenderer = new CollisionDebugRenderer();
        hud = new GameHUD();
        playerRenderer = new PlayerRenderer();

        String targetMap = (controller.getModel().getRoomPath() != null)
            ? controller.getModel().getRoomPath()
            : "maps/City of Tears-20260707T215923Z-3-001/cityOfTears1.tmx";

        loadNewRoom(targetMap);

        camera.position.set(player.getX(), player.getY(), 0);
        camera.zoom = 1.6f;
        camera.update();
    }

    private void loadNewRoom(String mapPath) {
        mapRenderer.load(mapPath);
        controller.initRoom(mapPath, mapRenderer.getMap(), mapRenderer);
    }

    @Override
    public void render(float delta) {
        if (controller.hasPendingRoomChange()) {
            String nextMap = controller.consumePendingMapPath();
            loadNewRoom(nextMap);
            camera.position.set(player.getX(), player.getY(), 0);
        } else {
            controller.updateGameplay(delta);
        }

        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        RoomModel currentRoom = controller.getCurrentRoom();
        updateCamera(delta, currentRoom);

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
        renderPlayer();

        batch.end();

        mapRenderer.renderLayer(camera, "for");
        mapRenderer.renderLayer(camera, "secret room");

//        debugRenderer.render(camera, player, currentRoom.getSolidTiles(), currentRoom.getHazards(),
//            controller.getMosscreeps(), controller.getFlies(), controller.getTiktiks());
        hud.render(batch, player.getHealth(), player.getMaxHealth(), player.getSoul(), player.getMaxSoul());
        drawBrightnessOverlay();
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
    }

    @Override
    public void dispose() {
        super.dispose();
        if (hud != null) hud.dispose();
        mapRenderer.dispose();
        playerRenderer.dispose();
        mosscreepRenderer.dispose();
        flyRenderer.dispose();
        bossRenderer.dispose();
    }
}
