package hana.HollowKnight.model.map;

import com.badlogic.gdx.math.Rectangle;

public class PortalModel {
    private final Rectangle bounds;
    private final String targetMathPath;
    private final float targetX;
    private final float targetY;

    public PortalModel(Rectangle bounds, String targetMathPath, float targetX, float targetY) {
        this.bounds = bounds;
        this.targetMathPath = targetMathPath;
        this.targetX = targetX;
        this.targetY = targetY;
    }

    public Rectangle getBounds() {return this.bounds;}
    public String getTargetMapPath() {return this.targetMathPath;}

    public float getTargetY() {
        return targetY;
    }

    public float getTargetX() {
        return targetX;
    }
}
