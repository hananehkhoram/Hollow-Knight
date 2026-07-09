package hana.HollowKnight.model.room;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class RoomModel {

    private final String mapPath;
    private final Array<Rectangle> hazards = new Array<>();
    private Rectangle breakableWall;
    private final Array<Rectangle> portablePlatforms = new Array<>();

    private float minX, minY, maxX, maxY;

    public RoomModel(String mapPath) {
        this.mapPath = mapPath;
    }

    public String getMapPath() { return mapPath; }

    public Array<Rectangle> getHazards() { return hazards; }

    public Rectangle getBreakableWall() { return breakableWall; }
    public void setBreakableWall(Rectangle r) { this.breakableWall = r; }

    public Array<Rectangle> getPortablePlatforms() { return portablePlatforms; }

    public void setBounds(float minX, float minY, float maxX, float maxY) {
        this.minX = minX; this.minY = minY;
        this.maxX = maxX; this.maxY = maxY;
    }
    public float getMinX() { return minX; }
    public float getMinY() { return minY; }
    public float getMaxX() { return maxX; }
    public float getMaxY() { return maxY; }
}
