package hana.HollowKnight.model.map;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class BossArena {

    private Rectangle bounds;
    private Rectangle trigger;
    private final Array<Rectangle> gates = new Array<>();
    private MapLayer gateLayer;

    private boolean locked = false;

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    public Rectangle getTrigger() {
        return trigger;
    }

    public void setTrigger(Rectangle trigger) {
        this.trigger = trigger;
    }

    public Array<Rectangle> getGates() {
        return gates;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setGateVisibility(boolean visible) {
        gateLayer.setVisible(visible);
    }

    public void setGateLayer(MapLayer gateLayer) {
        this.gateLayer = gateLayer;
    }
}
