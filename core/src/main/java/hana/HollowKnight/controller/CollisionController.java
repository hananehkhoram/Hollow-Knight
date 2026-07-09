package hana.HollowKnight.controller;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import hana.HollowKnight.model.entities.PlayerModel;
import hana.HollowKnight.model.map.BreakableWallModel;
import hana.HollowKnight.model.map.PortalModel;

public class CollisionController {

    private static final float EDGE_EPSILON = 2f;

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
    public void resolveHorizontalCollisions(Array<Rectangle> solidTiles) {
        Rectangle playerBounds = player.getBounds();
        for (Rectangle tile : solidTiles) {
            if (playerBounds.overlaps(tile)) {
                if (player.getVelocityX() > 0) {
                    player.setX(tile.x - player.getWidth());
                }
                else if (player.getVelocityX() < 0) {
                    player.setX(tile.x + tile.width);
                }
                player.setVelocityX(0f);
                playerBounds = player.getBounds();
            }
        }
    }

    public void resolveVerticalCollisions(Array<Rectangle> solidTiles) {
        player.setOnGround(false);
        Rectangle playerBounds = player.getBounds();

        for (Rectangle tile : solidTiles) {
            if (playerBounds.overlaps(tile)) {
                if (player.getVelocityY() <= 0) {
                    player.setY(tile.y + tile.height);
                    player.setVelocityY(0f);
                    player.setOnGround(true);
                }
                else if (player.getVelocityY() > 0) {
                    player.setY(tile.y - player.getHeight());
                    player.setVelocityY(0f);
                }
                playerBounds = player.getBounds();
            }
        }
    }

    public void resolveGroundCollisions(Array<Rectangle> solidTiles) {
        player.setOnGround(false);

        Rectangle playerBounds = player.getBounds();

        for (Rectangle tile : solidTiles) {
            if (playerBounds.overlaps(tile)) {
                if (player.getVelocityY() <= 0 && (player.getPrevY() >= tile.y + tile.height - EDGE_EPSILON)) {
                    player.setY(tile.y + tile.height);
                    player.setVelocityY(0f);
                    player.setOnGround(true);
                }

                else if (player.getVelocityY() > 0 && (player.getPrevY() + player.getHeight() <= tile.y + EDGE_EPSILON)) {
                    player.setY(tile.y - player.getHeight());
                    player.setVelocityY(0f);
                }
                else {
                    if (player.getVelocityX() > 0) {
                        player.setX(tile.x - player.getWidth());
                    } else if (player.getVelocityX() < 0) {
                        player.setX(tile.x + tile.width);
                    }
                    player.setVelocityX(0f);
                }
            }
        }
    }

}
