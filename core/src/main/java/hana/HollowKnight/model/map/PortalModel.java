package hana.HollowKnight.model.map;

import com.badlogic.gdx.math.Rectangle;

public class PortalModel {
    private final Rectangle bounds;
    private final String targetMathPath;
    private final float x, y;

    public PortalModel(Rectangle bounds, String targetMathPath) {
        this.bounds = bounds;
        this.targetMathPath = targetMathPath;
        this.x = bounds.x;
        this.y = bounds.y;
    }

    public Rectangle getBounds() {return this.bounds;}
    public String getTargetMapPath() {return this.targetMathPath;}
    public float getX() {return this.x;}
    public float getY() {return this.y;}
}
