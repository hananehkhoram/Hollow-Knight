package hana.HollowKnight.view.renderers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;

public class MapRenderer {

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    public void load(String mapPath) {
        dispose();
        map = new TmxMapLoader().load(mapPath);
        renderer = new OrthogonalTiledMapRenderer(map, 1);
    }

    public void render(OrthographicCamera camera) {
        AnimatedTiledMapTile.updateAnimationBaseTime();
        renderer.setView(camera);
        renderer.render();
    }

    public void renderLayer(OrthographicCamera camera, String layerName) {
        AnimatedTiledMapTile.updateAnimationBaseTime();
        renderer.setView(camera);
        MapLayer layer = map.getLayers().get(layerName);
        if (layer != null && layer.isVisible() && layer instanceof com.badlogic.gdx.maps.tiled.TiledMapTileLayer) {
            renderer.getBatch().setProjectionMatrix(camera.combined);
            renderer.getBatch().begin();
            renderer.renderTileLayer((com.badlogic.gdx.maps.tiled.TiledMapTileLayer) layer);
            renderer.getBatch().end();
        }
    }

    public void renderAllExcept(OrthographicCamera camera, String excludedLayerName) {
        AnimatedTiledMapTile.updateAnimationBaseTime();
        renderer.setView(camera);
        renderer.getBatch().setProjectionMatrix(camera.combined);
        renderer.getBatch().begin();
        for (MapLayer layer : map.getLayers()) {
            if (!layer.getName().equals(excludedLayerName) && layer.isVisible() && layer instanceof com.badlogic.gdx.maps.tiled.TiledMapTileLayer) {
                renderer.renderTileLayer((com.badlogic.gdx.maps.tiled.TiledMapTileLayer) layer);
            }
        }
        renderer.getBatch().end();
    }

    public TiledMap getMap() {
        return map;
    }

    public void dispose() {
        if (renderer != null) renderer.dispose();
        if (map != null) map.dispose();
        renderer = null;
        map = null;
    }

    public void setLayerVisibility(String layerName, boolean visible) {
        MapLayer layer = map.getLayers().get(layerName);
        layer.setVisible(visible);
        }

   }
