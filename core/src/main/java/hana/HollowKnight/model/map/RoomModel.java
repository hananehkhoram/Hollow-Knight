package hana.HollowKnight.model.map;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import hana.HollowKnight.model.entities.MosscreepModel;

import java.util.ArrayList;


public class RoomModel {

    private final String mapPath;
    private final Array<Rectangle> hazards = new Array<>();
    private final Array<Rectangle> solidTiles = new Array<>();
    private final Array<SpawnPointModel> enemySpawns = new Array<>();
    private ArrayList<MosscreepModel> crawlers = new ArrayList<>();
    private BreakableWallModel breakableWall;
    private PortalModel portal;
    private Vector2 knightSpawn = new Vector2(0, 0);
    private float minX, minY, maxX, maxY;

    public RoomModel(String mapPath) {
        this.mapPath = mapPath;
    }

    public Array<Rectangle> getSolidTiles() {
        return solidTiles;
    }

    public String getMapPath() {
        return mapPath;
    }

    public Array<Rectangle> getHazards() {
        return hazards;
    }

    public Array<SpawnPointModel> getEnemySpawns() {
        return enemySpawns;
    }

    public BreakableWallModel getBreakableWall() {
        return breakableWall;
    }

    public void setBreakableWall(BreakableWallModel wall) {
        this.breakableWall = wall;
    }

    public PortalModel getPortal() {
        return portal;
    }

    public void setPortal(PortalModel portal) {
        this.portal = portal;
    }

    public void setBounds(float minX, float minY, float maxX, float maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public float getMinX() {
        return minX;
    }

    public float getMinY() {
        return minY;
    }

    public float getMaxX() {
        return maxX;
    }

    public float getMaxY() {
        return maxY;
    }

    public Vector2 getKnightSpawn() {
        return knightSpawn;
    }

    public void setKnightSpawn(float x, float y) {
        this.knightSpawn.set(x, y);
    }

    public ArrayList<MosscreepModel> getCrawlers() {
        return crawlers;
    }

    public void setCrawlers(ArrayList<MosscreepModel> crawlers) {
        this.crawlers = crawlers;
    }
}
