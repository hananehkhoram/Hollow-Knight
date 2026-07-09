package hana.HollowKnight.model.map;

import com.badlogic.gdx.math.Rectangle;

public class BreakableWallModel {
    private final Rectangle bounds;
    private boolean broken = false;

    public BreakableWallModel(Rectangle bounds) {
        this.bounds = bounds;
    }

    public Rectangle getBounds() { return bounds; }
    public boolean isBroken() { return broken; }
    public void breakWall() { broken = true; }
}
