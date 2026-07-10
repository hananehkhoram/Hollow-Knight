package hana.HollowKnight.model.map;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * Represents the enclosed boss-fight room: the camera-clamp bounds, the
 * trigger zone that seals the room when the player walks in, and the gate
 * rectangles that get added to/removed from the room's solid tiles to lock
 * the exits.
 */
public class BossArena {

    private Rectangle bounds;
    private Rectangle trigger;
    private final Array<Rectangle> gates = new Array<>();

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
}
