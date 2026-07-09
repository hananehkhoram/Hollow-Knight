package hana.HollowKnight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import hana.HollowKnight.controller.CollisionController;
import hana.HollowKnight.controller.GameController;
import hana.HollowKnight.model.entities.PlayerModel;
import hana.HollowKnight.view.hud.GameHUD;
import hana.HollowKnight.view.screens.BaseScreen;

public class GameView extends BaseScreen {

    private GameHUD hud;
    private PlayerModel player = controller.getModel().getPlayer();
    private CollisionController collision;

    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    public GameView(GameController controller) {
        super(controller);
    }

    @Override
    public void show() {
        Skin hudSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        hud = new GameHUD();

        map = new TmxMapLoader().load("maps/City of Tears-20260707T215923Z-3-001/cityOfTears1.tmx");
        collision = new CollisionController(player, loadHazards(map));
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1);

        camera.position.set(0, 0, 0);
        camera.zoom = 2f;
        camera.update();


    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.4f, 0.6f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        controller.updateGameplay(delta);

        clampCamera(0,0,5744, 3200);
        camera.update();

        AnimatedTiledMapTile.updateAnimationBaseTime();
        mapRenderer.setView(camera);
        mapRenderer.render();

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
        if (map != null) map.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
    }

    private void clampCamera(float roomMinX, float roomMinY, float roomMaxX, float roomMaxY) {
        float halfViewportWidth = camera.viewportWidth * camera.zoom / 2f;
        float halfViewportHeight = camera.viewportHeight * camera.zoom / 2f;

        float camX = MathUtils.clamp(2592, roomMinX + halfViewportWidth, roomMaxX - halfViewportWidth);
        float camY = MathUtils.clamp(1144, roomMinY + halfViewportHeight, roomMaxY - halfViewportHeight);

        camera.position.set(camX, camY, 0);
        if (collision.checkHazardCollisions()){

        }
    }

    public Array<Rectangle> loadHazards(TiledMap map) {
        Array<Rectangle> hazardRects = new Array<>();

        MapLayer hazardLayer = map.getLayers().get("hazards");

        if (hazardLayer != null) {
            for (MapObject object : hazardLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    hazardRects.add(rect);
                }
            }
        }
        return hazardRects;
    }
}
