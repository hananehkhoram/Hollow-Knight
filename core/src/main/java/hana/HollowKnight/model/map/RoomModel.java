package hana.HollowKnight.model.map;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;


public class RoomModel {

    private final String mapPath;
    private final Array<Rectangle> hazards = new Array<>();
    private BreakableWallModel breakableWall;
    private PortalModel portal;

    private float minX, minY, maxX, maxY;

    public RoomModel(String mapPath) {
        this.mapPath = mapPath;
    }

    public String getMapPath() { return mapPath; }

    public Array<Rectangle> getHazards() { return hazards; }

    public BreakableWallModel getBreakableWall() { return breakableWall; }
    public void setBreakableWall(BreakableWallModel wall) { this.breakableWall = wall; }

    public PortalModel getPortal() { return portal; }
    public void setPortal(PortalModel portal) { this.portal = portal; }

    public void setBounds(float minX, float minY, float maxX, float maxY) {
        this.minX = minX; this.minY = minY;
        this.maxX = maxX; this.maxY = maxY;
    }
    public float getMinX() { return minX; }
    public float getMinY() { return minY; }
    public float getMaxX() { return maxX; }
    public float getMaxY() { return maxY; }
}
