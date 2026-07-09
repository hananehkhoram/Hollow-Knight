package hana.HollowKnight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import hana.HollowKnight.controller.AIController;
import hana.HollowKnight.controller.CollisionController;
import hana.HollowKnight.controller.GameController;
import hana.HollowKnight.controller.RoomLoader;
import hana.HollowKnight.model.entities.MosscreepModel;
import hana.HollowKnight.model.entities.PlayerModel;
import hana.HollowKnight.model.map.PortalModel;
import hana.HollowKnight.model.map.RoomModel;
import hana.HollowKnight.view.hud.GameHUD;
import hana.HollowKnight.view.renderers.CollisionDebugRenderer;
import hana.HollowKnight.view.renderers.MosscreepRenderer;
import hana.HollowKnight.view.renderers.MapRenderer;
import hana.HollowKnight.view.renderers.PlayerRenderer;
import hana.HollowKnight.view.screens.BaseScreen;

import java.util.ArrayList;

public class GameView extends BaseScreen {

    private static final int HAZARD_DAMAGE = 1;
    private final MapRenderer mapRenderer = new MapRenderer();
    private GameHUD hud;
    private PlayerModel player = controller.getModel().getPlayer();
    private CollisionController collision;
    private RoomModel currentRoom;
    private PlayerRenderer playerRenderer;
    private CollisionDebugRenderer debugRenderer;

    private final AIController aiController = new AIController();
    private final MosscreepRenderer crawlerRenderer = new MosscreepRenderer();
    private ArrayList<MosscreepModel> crawlers;

    public GameView(GameController controller) {
        super(controller);
    }

    @Override
    public void show() {
        debugRenderer = new CollisionDebugRenderer();
        hud = new GameHUD();

        String targetMap = (controller.getModel().getRoomPath() != null) ?
            controller.getModel().getRoomPath() : "maps/City of Tears-20260707T215923Z-3-001/cityOfTears1.tmx";
        loadRoom(targetMap);

        playerRenderer = new PlayerRenderer();

        Vector2 spawn = currentRoom.getKnightSpawn();
        player.setPosition(spawn.x, spawn.y);

        player.savePrevPosition();
        camera.position.set(player.getX(), player.getY() , 0);
        camera.zoom = 1.8f;
        camera.update();
    }

    private void loadRoom(String mapPath) {
        mapRenderer.load(mapPath);
        currentRoom = RoomLoader.load(mapRenderer.getMap(), mapPath);
        collision = new CollisionController(player, currentRoom.getHazards(),
            currentRoom.getBreakableWall(), currentRoom.getPortal(), mapRenderer);
        crawlers = RoomLoader.spawnCrawlers(currentRoom);
        currentRoom.setCrawlers(crawlers);
    }

    @Override
    public void render(float delta) {
        PortalModel triggeredPortal = collision.checkPortalCollision();
        if (triggeredPortal != null) {
            loadRoom(triggeredPortal.getTargetMapPath());
            player.setPosition(triggeredPortal.getX(), triggeredPortal.getY());
            camera.position.set(player.getX(), player.getY(), 0);
        }

        Gdx.gl.glClearColor(0.2f, 0.4f, 0.6f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        controller.updateGameplay(delta);

        player.savePrevPosition();

        collision.updateMovement(delta, currentRoom.getSolidTiles());

        player.update(delta);
        collision.checkHazardCollisions(HAZARD_DAMAGE);
        collision.checkAttackOnBreakable();

        for (MosscreepModel crawler : crawlers) {
            aiController.updateCrawler(crawler, delta, currentRoom.getSolidTiles(), player);
        }

        camera.position.set(player.getX(), player.getY() + 200, 0);
        camera.update();

        mapRenderer.renderAllExcept(camera, "for");
        mapRenderer.renderAllExcept(camera, "secret room");

        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        if (player.isInvincible()) {
            if (com.badlogic.gdx.math.MathUtils.sin(player.getInvulnerabilityTimer() * 25f) > 0) {
                playerRenderer.render(batch, player);
            }
        } else {
            playerRenderer.render(batch, player);
        }
        for (MosscreepModel crawler : crawlers) {
            crawlerRenderer.render(batch, crawler);
        }
        batch.end();

        mapRenderer.renderLayer(camera, "for");
        mapRenderer.renderLayer(camera, "secret room");

        debugRenderer.render(camera, player, currentRoom.getSolidTiles(), currentRoom.getHazards(), crawlers);
        hud.render(batch, player.getHealth(), player.getMaxHealth(), player.getSoul(), player.getMaxSoul());
        drawBrightnessOverlay();
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
        crawlerRenderer.dispose();
    }

}
