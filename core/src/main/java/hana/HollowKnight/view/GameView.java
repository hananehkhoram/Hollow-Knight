package hana.HollowKnight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import hana.HollowKnight.controller.CollisionController;
import hana.HollowKnight.controller.GameController;
import hana.HollowKnight.controller.RoomLoader;
import hana.HollowKnight.model.entities.PlayerModel;
import hana.HollowKnight.model.map.PortalModel;
import hana.HollowKnight.model.map.RoomModel;
import hana.HollowKnight.view.hud.GameHUD;
import hana.HollowKnight.view.renderers.MapRenderer;
import hana.HollowKnight.view.screens.BaseScreen;

public class GameView extends BaseScreen {

    private static final int HAZARD_DAMAGE = 1;

    private GameHUD hud;
    private PlayerModel player = controller.getModel().getPlayer();
    private CollisionController collision;
    private RoomModel currentRoom;
    private final MapRenderer mapRenderer = new MapRenderer();

    public GameView(GameController controller) {
        super(controller);
    }

    @Override
    public void show() {
        hud = new GameHUD();
        loadRoom("maps/City of Tears-20260707T215923Z-3-001/cityOfTears1.tmx");

        camera.position.set(0, 0, 0);
        camera.zoom = 2f;
        camera.update();
    }

    private void loadRoom(String mapPath) {
        mapRenderer.load(mapPath);
        currentRoom = RoomLoader.load(mapRenderer.getMap(), mapPath);
        collision = new CollisionController(player, currentRoom.getHazards(),
            currentRoom.getBreakableWall(), currentRoom.getPortal());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.4f, 0.6f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        controller.updateGameplay(delta);

        collision.checkHazardCollisions(HAZARD_DAMAGE);
        collision.checkAttackOnBreakable();

        PortalModel triggeredPortal = collision.checkPortalCollision();
        if (triggeredPortal != null) {
            loadRoom(triggeredPortal.getTargetMapPath());
            player.setPosition(triggeredPortal.getX(), triggeredPortal.getY());
        }

        mapRenderer.clampCamera(camera, currentRoom.getMinX(), currentRoom.getMinY(),
            currentRoom.getMaxX(), currentRoom.getMaxY(), player.getX(), player.getY());
        camera.update();

        mapRenderer.render(camera);

        batch.setProjectionMatrix(camera.combined);
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
    }
}
