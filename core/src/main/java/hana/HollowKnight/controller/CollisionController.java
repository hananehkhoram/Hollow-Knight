package hana.HollowKnight.controller;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import hana.HollowKnight.model.entities.PlayerModel;
import hana.HollowKnight.model.map.BreakableWallModel;
import hana.HollowKnight.model.map.PortalModel;
import hana.HollowKnight.view.renderers.MapRenderer;

public class CollisionController {

    private static final float MAX_DELTA = 1f / 30f;
    private boolean wallDamagedThisAttack = false;

    private final PlayerModel player;
    private final Array<Rectangle> hazards;
    private final BreakableWallModel breakableWall;
    private final PortalModel portal;
    private final MapRenderer mapRenderer;

    public CollisionController(PlayerModel player,
                               Array<Rectangle> hazards,
                               BreakableWallModel breakableWall,
                               PortalModel portal,
                               MapRenderer mapRenderer) {
        this.player = player;
        this.hazards = hazards;
        this.breakableWall = breakableWall;
        this.portal = portal;
        this.mapRenderer = mapRenderer;
    }

    public void checkHazardCollisions(int damageAmount) {
        Rectangle playerBounds = player.getBounds();
        for (Rectangle hazard : hazards) {
            if (playerBounds.overlaps(hazard)) {
                player.takeDamage(damageAmount);
                player.applyKnockBack();
                player.setPosition(player.getLastSafeX(), player.getLastSafeY());
                return;
            }
        }
    }

    public void checkAttackOnBreakable() {
        if (breakableWall == null || breakableWall.isBroken()) return;

        if (player.isAttacking()) {
            if (!wallDamagedThisAttack && player.getAttackHitbox().overlaps(breakableWall.getBounds())) {
                breakableWall.breakWall();
                wallDamagedThisAttack = true;
                if (breakableWall.isBroken()) {
                    mapRenderer.setLayerVisibility("wall", false);
                    mapRenderer.setLayerVisibility("secret_room_back", true);
                }
            }
        } else {
            wallDamagedThisAttack = false;
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

    public void updateMovement(float delta, Array<Rectangle> solidTiles) {
        delta = Math.min(delta, MAX_DELTA);

        if (!player.isDashing()) {
            player.setVelocityY(player.getVelocityY() + PlayerModel.GRAVITY * delta);
        }

        player.setX(player.getX() + player.getVelocityX() * delta);
        resolveHorizontalCollisions(solidTiles);

        player.setY(player.getY() + player.getVelocityY() * delta);
        resolveVerticalCollisions(solidTiles);
    }

    public void resolveHorizontalCollisions(Array<Rectangle> solidTiles) {
        Rectangle playerBounds = player.getBounds();

        for (Rectangle tile : solidTiles) {
            if (playerBounds.overlaps(tile)) {
                if (player.getVelocityX() > 0) {
                    player.setX(tile.x - player.getWidth());
                } else if (player.getVelocityX() < 0) {
                    player.setX(tile.x + tile.width);
                }
                player.setVelocityX(0f);
                playerBounds = player.getBounds();
            }
        }

        if (breakableWall != null && !breakableWall.isBroken()) {
            Rectangle wallBounds = breakableWall.getBounds();
            if (playerBounds.overlaps(wallBounds)) {
                if (player.getVelocityX() > 0) {
                    player.setX(wallBounds.x - player.getWidth());
                } else if (player.getVelocityX() < 0) {
                    player.setX(wallBounds.x + wallBounds.width);
                }
                player.setVelocityX(0f);
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
                    player.setLastSafeX(player.getX());
                    player.setLastSafeY(player.getY());
                } else if (player.getVelocityY() > 0) {
                    player.setY(tile.y - player.getHeight());
                    player.setVelocityY(0f);
                }
                playerBounds = player.getBounds();
            }
        }
    }
}
