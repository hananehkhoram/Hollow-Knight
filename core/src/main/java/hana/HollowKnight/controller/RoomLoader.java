package hana.HollowKnight.controller;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import hana.HollowKnight.model.entities.*;
import hana.HollowKnight.model.map.*;

import java.util.ArrayList;

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
                    MapProperties props = object.getProperties();
                    String name = object.getName();

                    if ("portal".equals(name)) {
                        String targetMap = props.get("targetPath", String.class);
                        float targetX = props.get("targetX", Float.class);
                        float targetY = props.get("targetY", Float.class);
                        room.setPortal(new PortalModel(rect, targetMap, targetX, targetY));
                        room.setTargetX(targetX);
                        room.setTargetY(targetY);
                    } else if ("breakable".equals(name) || props.containsKey("breakable")) {
                        room.setBreakableWall(new BreakableWallModel(rect));
                    } else if ("wall".equals(name)) {
                        room.getWalls().add(rect);
                        room.getSolidTiles().add(rect);
                    } else {
                        room.getSolidTiles().add(rect);
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


        MapLayer spawnLayer = map.getLayers().get("spawn_points");
        if (spawnLayer != null) {
            for (MapObject object : spawnLayer.getObjects()) {
                String name = object.getName();
                MapProperties prop = object.getProperties();
                float x = prop.get("x", Float.class);
                float y = prop.get("y", Float.class);

                if (name == null) continue;
                if ("knight".equals(name)) {
                    room.setKnightSpawn(x, y);
                }else if("zote".equals(name)) {
                    room.setZoteSpawn(x,y);
                } else {
                    room.getEnemySpawns().add(new SpawnPointModel(name, x, y));
                }
            }
        }

        MapLayer voidHeart = map.getLayers().get("voidheart");
        room.setVoidHeart(voidHeart);

        MapLayer gateLayer = map.getLayers().get("gate");

        MapLayer bossArenaLayer = map.getLayers().get("boss_arena");
        if (bossArenaLayer != null) {
            BossArena arena = new BossArena();
            arena.setGateLayer(gateLayer);
            for (MapObject object : bossArenaLayer.getObjects()) {
                if (!(object instanceof RectangleMapObject)) continue;
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                String name = object.getName();

                if ("bounds".equals(name)) {
                    arena.setBounds(rect);
                } else if ("trigger".equals(name)) {
                    arena.setTrigger(rect);
                } else if ("gate".equals(name)) {
                    arena.getGates().add(rect);
                }
            }
            room.setBossArena(arena);
        }

        return room;
    }

    public static ArrayList<BossModel> spawnBoss(RoomModel currentRoom) {
        ArrayList<BossModel> bosses = new ArrayList<>();
        for (SpawnPointModel spawn : currentRoom.getEnemySpawns()) {
            if ("falseknight".equals(spawn.getName())) {
                bosses.add(new BossModel(spawn.getX(), spawn.getY()));
            }
        }
        return bosses;
    }
    public static ArrayList<CrawlerModel> spawnCrawlers(RoomModel currentRoom, String name) {
        ArrayList<CrawlerModel> crawlers = new ArrayList<>();
        for (SpawnPointModel spawn : currentRoom.getEnemySpawns()) {
            if (name.equals(spawn.getName())) {
                crawlers.add(new CrawlerModel(spawn.getX(), spawn.getY()));
            }
        }
        return crawlers;
    }

    public static ArrayList<FlyModel> spawnFlies(RoomModel currentRoom, PlayerModel player) {
        ArrayList<FlyModel> fly = new ArrayList<>();
        for (SpawnPointModel spawn : currentRoom.getEnemySpawns()) {
            if ("winged".equals(spawn.getName())) {
                fly.add(new FlyModel(spawn.getX(), spawn.getY(), player));
            }
        }
        return fly;
    }

    public static ArrayList<HuskHornheadModel> spawnHusk(RoomModel currentRoom) {
        ArrayList<HuskHornheadModel> husks = new ArrayList<>();
        for (SpawnPointModel spawn : currentRoom.getEnemySpawns()) {
            if ("husk".equals(spawn.getName())) {
                husks.add(new HuskHornheadModel(spawn.getX(), spawn.getY()));
            }
        }
        return husks;
    }



}
