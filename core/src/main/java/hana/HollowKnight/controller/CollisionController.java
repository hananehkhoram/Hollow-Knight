package hana.HollowKnight.controller;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import hana.HollowKnight.model.entities.PlayerModel;

public class CollisionController {
    private Array<Rectangle> hazards;
    private PlayerModel player;
    public CollisionController(PlayerModel playerModel,  Array<Rectangle> hazards){
        this.player = playerModel;
        this.hazards = hazards;
    }
    public boolean checkHazardCollisions() {
        if (player.isInvincible()) {
            return false;
        }

        Rectangle playerBounds = player.getBounds();
        for (Rectangle hazard : hazards) {
            if (playerBounds.overlaps(hazard)) {

                player.takeDamage(1);
                player.startInvincibility();
                player.applyKnockback();

                return true;
            }
        }
    }
}
