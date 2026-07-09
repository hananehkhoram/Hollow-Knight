package hana.HollowKnight.controller;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import hana.HollowKnight.model.room.RoomModel;

public class RoomLoader {

    public static RoomModel load(TiledMap map, String mapPath) {
        RoomModel room = new RoomModel(mapPath);

        MapLayer hazardLayer = map.getLayers().get("hazards");
        if (hazardLayer != null) {
            for (MapObject object : hazardLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    room.getHazards().add(((RectangleMapObject) object).getRectangle());
                }
            }
        }

        MapLayer collisionLayer = map.getLayers().get("collisions");
        if (collisionLayer != null) {
            for (MapObject object : collisionLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    if ("breakable".equals(object.getName()) || object.getProperties().containsKey("breakable")) {
                        room.setBreakableWall(rect);
                    } else if ("portable".equals(object.getName()) || object.getProperties().containsKey("portable")) {
                        room.getPortablePlatforms().add(rect);
                    }
                }
            }
        }

        MapProperties props = map.getProperties();
        int width = props.get("width", Integer.class);
        int height = props.get("height", Integer.class);
        int tileW = props.get("tilewidth", Integer.class);
        int tileH = props.get("tileheight", Integer.class);
        room.setBounds(0, 0, width * tileW, height * tileH);

        return room;
    }
}
