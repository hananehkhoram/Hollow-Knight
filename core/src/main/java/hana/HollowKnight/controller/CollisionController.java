package hana.HollowKnight.controller;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import hana.HollowKnight.model.entities.PlayerModel;
import hana.HollowKnight.model.map.BreakableWallModel;
import hana.HollowKnight.model.map.PortalModel;

public class CollisionController {

    private final PlayerModel player;
    private final Array<Rectangle> hazards;
    private final BreakableWallModel breakableWall;
    private final PortalModel portal;

    public CollisionController(PlayerModel player,
                               Array<Rectangle> hazards,
                               BreakableWallModel breakableWall,
                               PortalModel portal) {
        this.player = player;
        this.hazards = hazards;
        this.breakableWall = breakableWall;
        this.portal = portal;
    }

    public void checkHazardCollisions(int damageAmount) {
        Rectangle playerBounds = player.getBounds();
        for (Rectangle hazard : hazards) {
            if (playerBounds.overlaps(hazard)) {
                player.takeDamage(damageAmount);
                player.applyKnockBack();
                return;
            }
        }
    }

    public void checkAttackOnBreakable() {
        if (breakableWall == null || breakableWall.isBroken() || !player.isAttacking()) return;
        if (player.getAttackHitbox().overlaps(breakableWall.getBounds())) {
            breakableWall.breakWall();
        }
    }

    public boolean isBlockedByBreakable(Rectangle nextBounds) {
        if (breakableWall == null || breakableWall.isBroken()) return false;
        return nextBounds.overlaps(breakableWall.getBounds());
    }

    public PortalModel checkPortalCollision() {
        if (portal == null) return null;
        return player.getBounds().overlaps(portal.getBounds()) ? portal : null;
    }

    public void resolveGroundCollisions(Array<Rectangle> solidTiles) {
        Rectangle playerBounds = player.getBounds();
        boolean grounded = false;

        for (Rectangle tile : solidTiles) {
            if (!playerBounds.overlaps(tile)) continue;
            if (player.getVelocityY() <= 0 && playerBounds.y < tile.y + tile.height) {
                player.landOn(tile.y + tile.height);
                grounded = true;
            }
            else if (player.getVelocityY() > 0 && playerBounds.y + playerBounds.height > tile.y) {
                player.hitCeiling(tile.y);
            }
        }

        player.setGrounded(grounded);
    }
}
