package hana.HollowKnight.controller;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import hana.HollowKnight.model.entities.PlayerModel;
import hana.HollowKnight.model.entities.ZoteModel;
import hana.HollowKnight.model.map.BreakableWallModel;
import hana.HollowKnight.model.map.PortalModel;
import hana.HollowKnight.view.audio.AudioManager;
import hana.HollowKnight.view.renderers.MapRenderer;

public class CollisionController {

    private static final float MAX_DELTA = 1f / 30f;
    private final PlayerModel player;
    private final Array<Rectangle> hazards;
    private final BreakableWallModel breakableWall;
    private final PortalModel portal;
    private final MapRenderer mapRenderer;
    private final ZoteModel zote;
    private boolean wallDamagedThisAttack = false;

    public CollisionController(PlayerModel player,
                               Array<Rectangle> hazards,
                               BreakableWallModel breakableWall,
                               PortalModel portal,
                               MapRenderer mapRenderer,
                               ZoteModel zote) {
        this.player = player;
        this.hazards = hazards;
        this.breakableWall = breakableWall;
        this.portal = portal;
        this.mapRenderer = mapRenderer;
        this.zote = zote;
    }

    public void checkHazardCollisions(int damageAmount) {
        Rectangle playerBounds = player.getBounds();
        for (Rectangle hazard : hazards) {
            if (playerBounds.overlaps(hazard)) {
                player.takeDamage(damageAmount);
                AudioManager.getInstance().playGetDamageSound();
                player.applyKnockBack();

                player.setPosition(player.getLastSafeX() - (hazard.x - player.getX()) / 2, player.getLastSafeY());
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
                    mapRenderer.setLayerVisibility("voidheart", true);
                }
            }
        } else {
            wallDamagedThisAttack = false;
        }
    }


    public PortalModel checkPortalCollision() {
        if (portal == null) return null;
        return player.getBounds().overlaps(portal.getBounds()) ? portal : null;
    }

    public void updateMovement(float delta, Array<Rectangle> solidTiles, Array<Rectangle> walls) {
        delta = Math.min(delta, MAX_DELTA);

        if (!player.isDashing() || !player.isMantis()) {
            player.setVelocityY(player.getVelocityY() + PlayerModel.GRAVITY * delta);
        }

        if (player.isDashing()) {
            player.setVelocityY(0);
        }

        player.setX(player.getX() + player.getVelocityX() * delta);
        resolveHorizontalCollisions(solidTiles, walls);

        player.setY(player.getY() + player.getVelocityY() * delta);
        resolveVerticalCollisions(solidTiles);
    }

    public void resolveHorizontalCollisions(Array<Rectangle> solidTiles, Array<Rectangle> walls) {
        Rectangle playerBounds = player.getBounds();
        boolean hitWall = false;

        for (Rectangle tile : solidTiles) {
            if (playerBounds.overlaps(tile)) {
                boolean isMantisWall = walls.contains(tile, false);

                if (isMantisWall) {
                    hitWall = true;
                }

                if (player.getVelocityX() > 0) {
                    player.setX(tile.x - player.getWidth());
                    if (InputHandler.getInstance().isDown(InputHandler.PlayerAction.MOVE_RIGHT) && !player.isOnGround() && isMantisWall) {
                        player.mantis();
                    } else {
                        player.setMantis(false);
                    }
                } else if (player.getVelocityX() < 0) {
                    player.setX(tile.x + tile.width);
                    if (InputHandler.getInstance().isDown(InputHandler.PlayerAction.MOVE_LEFT) && !player.isOnGround() && isMantisWall) {
                        player.mantis();
                    } else {
                        player.setMantis(false);
                    }
                }

                player.setVelocityX(0f);
                playerBounds = player.getBounds();
            }
        }

        if (!hitWall) {
            player.setMantis(false);
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

    public void resolveHorizontalCollisionsForZote(Array<Rectangle> solidTiles) {
        Rectangle playerBounds = zote.getBounds();

        for (Rectangle tile : solidTiles) {
            if (playerBounds.overlaps(tile)) {
                if (zote.getVelocityX() > 0) {
                    zote.setX(tile.x - player.getWidth());
                } else if (zote.getVelocityX() < 0) {
                    zote.setX(tile.x + tile.width);
                }

                zote.setVelocityX(0f);
            }
        }
    }

    public void resolveVerticalCollisionsForZote(Array<Rectangle> solidTiles) {
        zote.setOnGround(false);
        Rectangle playerBounds = zote.getBounds();

        for (Rectangle tile : solidTiles) {
            if (playerBounds.overlaps(tile)) {
                if (zote.getVelocityY() <= 0) {
                    zote.setY(tile.y + tile.height);
                    zote.setVelocityY(0f);
                    zote.setOnGround(true);
                } else {
                    zote.setState(ZoteModel.States.FALL);
                    }
                }
            }
        }


    public void checkZoteCollosions() {
        Rectangle playerBounds = player.getBounds();
        Rectangle zoteBounds = zote.getBounds();
        if (playerBounds.overlaps(zoteBounds) && player.isAttacking()) {
            if (zote.getState() == ZoteModel.States.IDLE || zote.getState() == ZoteModel.States.TALK)
                zote.setState(ZoteModel.States.ATTACK);
        }
    }

    public void updateMovementZote(float delta, Array<Rectangle> solidTiles) {
        delta = Math.min(delta, MAX_DELTA);
        if (player.isFacingRight()) {zote.setFacingRight(false);}
        else zote.setFacingRight(true);

        zote.setX(zote.getX() + zote.getVelocityX() * delta);
        resolveHorizontalCollisionsForZote(solidTiles);

        zote.setVelocityY(zote.getVelocityY() + PlayerModel.GRAVITY * delta);
        zote.setY(zote.getY() + zote.getVelocityY() * delta);
        resolveVerticalCollisionsForZote(solidTiles);
        checkZoteCollosions();
    }

}
