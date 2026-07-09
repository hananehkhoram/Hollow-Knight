package hana.HollowKnight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import hana.HollowKnight.controller.CollisionController;
import hana.HollowKnight.controller.GameController;
import hana.HollowKnight.model.entities.PlayerModel;
import hana.HollowKnight.view.hud.GameHUD;
import hana.HollowKnight.view.screens.BaseScreen;
import hana.HollowKnight.controller.RoomLoader;
import hana.HollowKnight.model.room.RoomModel;

public class GameView extends BaseScreen {

    private GameHUD hud;
    private PlayerModel player = controller.getModel().getPlayer();
    private CollisionController collision;
    private RoomModel currentRoom;

    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

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
        if (map != null) map.dispose();
        if (mapRenderer != null) mapRenderer.dispose();

        map = new TmxMapLoader().load(mapPath);
        currentRoom = RoomLoader.load(map, mapPath);
        collision = new CollisionController(player, currentRoom.getHazards());
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.4f, 0.6f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        controller.updateGameplay(delta);

        clampCamera(currentRoom.getMinX(), currentRoom.getMinY(),
            currentRoom.getMaxX(), currentRoom.getMaxY());
        camera.update();

        AnimatedTiledMapTile.updateAnimationBaseTime();
        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.setProjectionMatrix(camera.combined);
        hud.render(batch, player.getHealth(), player.getMaxHealth(), player.getSoul(), player.getMaxSoul());
        drawBrightnessOverlay();

        if (collision.checkHazardCollisions()) {
            // اینجا فقط باید یه متد رو مدل صدا بزنی، نه گرافیک مستقیم
            // مثلاً: player.takeDamage(hazardDamage);
        }
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
        if (map != null) map.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
    }

    private void clampCamera(float roomMinX, float roomMinY, float roomMaxX, float roomMaxY) {
        float halfViewportWidth = camera.viewportWidth * camera.zoom / 2f;
        float halfViewportHeight = camera.viewportHeight * camera.zoom / 2f;

        float camX = MathUtils.clamp(2592, roomMinX + halfViewportWidth, roomMaxX - halfViewportWidth);
        float camY = MathUtils.clamp(1144, roomMinY + halfViewportHeight, roomMaxY - halfViewportHeight);

        camera.position.set(camX, camY, 0);
    }
}
