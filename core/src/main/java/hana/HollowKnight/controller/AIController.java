package hana.HollowKnight.controller;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import hana.HollowKnight.model.entities.MosscreepModel;
import hana.HollowKnight.model.entities.EnemyModel;
import hana.HollowKnight.model.entities.PlayerModel;

/**
 * Drives simple ground-patrol AI for enemies that don't need pathfinding.
 * For now this only handles MosscreepModel (Mosscreep); other enemy types
 * (Hornhead, Spitter, Zote, ...) will likely need their own update method
 * here or their own controller once their behavior is implemented.
 */
public class AIController {

    private static final float MAX_DELTA = 1f / 30f;
    private static final float GRAVITY = -1400f;
    private static final float EDGE_PROBE_DEPTH = 6f;
    private static final float EDGE_PROBE_WIDTH = 2f;

    public void updateCrawler(MosscreepModel crawler, float delta, Array<Rectangle> solidTiles, PlayerModel player) {
        delta = Math.min(delta, MAX_DELTA);

        if (crawler.isDead()) {
            crawler.update(delta);
            return;
        }

        crawler.savePrevPosition();

        crawler.setVelocityY(crawler.getVelocityY() + GRAVITY * delta);
        crawler.setY(crawler.getY() + crawler.getVelocityY() * delta);
        resolveVerticalCollisions(crawler, solidTiles);
        checkPlayerInteraction(crawler, player, 1);

        crawler.setX(crawler.getX() + crawler.getVelocityX() * delta);
        boolean hitWall = resolveHorizontalCollisions(crawler, solidTiles);

        boolean atLedge = crawler.isOnGround() && !hitWall && isAtLedge(crawler, solidTiles);

        if (hitWall || atLedge) {
            crawler.turn();
        }

        crawler.update(delta);
    }

    private void resolveVerticalCollisions(MosscreepModel crawler, Array<Rectangle> solidTiles) {
        crawler.setOnGround(false);
        Rectangle bounds = crawler.getBounds();

        for (Rectangle tile : solidTiles) {
            if (bounds.overlaps(tile)) {
                if (crawler.getVelocityY() <= 0) {
                    crawler.setY(tile.y + tile.height);
                    crawler.setVelocityY(0f);
                    crawler.setOnGround(true);
                } else {
                    crawler.setY(tile.y - crawler.getHeight());
                    crawler.setVelocityY(0f);
                }
                bounds = crawler.getBounds();
            }
        }
    }

    /** Stops the crawler and flips it around if it walked into a solid tile. */
    private boolean resolveHorizontalCollisions(MosscreepModel crawler, Array<Rectangle> solidTiles) {
        Rectangle bounds = crawler.getBounds();

        for (Rectangle tile : solidTiles) {
            if (bounds.overlaps(tile)) {
                if (crawler.getVelocityX() > 0) {
                    crawler.setX(tile.x - crawler.getWidth());
                } else if (crawler.getVelocityX() < 0) {
                    crawler.setX(tile.x + tile.width);
                }
                return true;
            }
        }
        return false;
    }

    /** True if there is no ground tile just past the crawler's leading edge. */
    private boolean isAtLedge(MosscreepModel crawler, Array<Rectangle> solidTiles) {
        float probeX = crawler.isFacingRight()
            ? crawler.getX() + crawler.getWidth()
            : crawler.getX() - EDGE_PROBE_WIDTH;
        float probeY = crawler.getY() - EDGE_PROBE_DEPTH;

        Rectangle probe = new Rectangle(probeX, probeY, EDGE_PROBE_WIDTH, EDGE_PROBE_DEPTH);

        for (Rectangle tile : solidTiles) {
            if (tile.overlaps(probe)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Handles combat between the player and a single enemy:
     * - if the player is mid-attack and its hitbox overlaps the enemy, the enemy takes damage
     *   (only once per swing, tracked via wasHitByCurrentAttack)
     * - if the enemy's body overlaps the player and the player isn't invincible, the player
     *   takes the enemy's contact damage and gets knocked back
     */
    public void checkPlayerInteraction(EnemyModel enemy, PlayerModel player, int attackDamage) {
        if (enemy.isDead()) return;

        if (player.isAttacking()) {
            if (!enemy.wasHitByCurrentAttack() && player.getAttackHitbox().overlaps(enemy.getBounds())) {
                enemy.takeDamage(attackDamage);
                enemy.markHitByCurrentAttack();
                player.gainSoulOnHit();
            }
        } else {
            enemy.resetAttackHitFlag();
        }

        if (!enemy.isDead() && !player.isInvincible() && enemy.getBounds().overlaps(player.getBounds())) {
            player.takeDamage(enemy.getContactDamage());
            player.applyKnockBack();
        }
    }
}
