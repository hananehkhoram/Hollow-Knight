package hana.HollowKnight.model.map;

import com.badlogic.gdx.math.Rectangle;
import hana.HollowKnight.view.audio.AudioManager;

public class BreakableWallModel {
    private final Rectangle bounds;
    private boolean broken = false;
    private int breakHits = 1;

    public BreakableWallModel(Rectangle bounds) {
        this.bounds = bounds;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isBroken() {
        return broken;
    }

    public void breakWall() {
        if (breakHits == 3) {broken = true;
            AudioManager.getInstance().playBreakWall3();
        }
        else{
            switch (breakHits) {
                case 1: AudioManager.getInstance().playBreakWall1();
                case 2: AudioManager.getInstance().playBreakWall2();
            }
            breakHits++;
        }
    }
}
