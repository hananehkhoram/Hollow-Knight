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
        boolean grounded = false;

        for (Rectangle tile : solidTiles) {
            Rectangle playerBounds = player.getBounds();
            if (!playerBounds.overlaps(tile)) continue;

            // میزان تداخل (overlap) در هر جهت را حساب می‌کنیم
            float overlapLeft = (playerBounds.x + playerBounds.width) - tile.x;
            float overlapRight = (tile.x + tile.width) - playerBounds.x;
            float overlapTop = (tile.y + tile.height) - playerBounds.y;
            float overlapBottom = (playerBounds.y + playerBounds.height) - tile.y;

            float minOverlapX = Math.min(overlapLeft, overlapRight);
            float minOverlapY = Math.min(overlapTop, overlapBottom);

            if (minOverlapX < minOverlapY) {
                // برخورد افقی -> این یک دیوار است، نه زمین
                if (overlapLeft < overlapRight) {
                    player.setX(tile.x - player.getWidth());
                } else {
                    player.setX(tile.x + tile.width);
                }
                player.setVelocityX(0f);
            } else {
                // برخورد عمودی -> زمین یا سقف
                if (overlapTop < overlapBottom) {
                    player.landOn(tile.y + tile.height);
                    grounded = true;
                } else {
                    player.hitCeiling(tile.y);
                }
            }
        }

        player.setGrounded(grounded);
    }
}
