package hana.HollowKnight.controller;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import hana.HollowKnight.model.entities.CrawlerModel;
import hana.HollowKnight.model.entities.EnemyModel;
import hana.HollowKnight.model.entities.FlyModel;
import hana.HollowKnight.model.entities.PlayerModel;
import hana.HollowKnight.model.stats.GameStats;
import hana.HollowKnight.view.audio.AudioManager;

public class AIController {

    private static final float MAX_DELTA = 1f / 30f;
    private static final float GRAVITY = -1400f;
    private static final float EDGE_PROBE_DEPTH = 6f;
    private static final float EDGE_PROBE_WIDTH = 2f;
    private GameController controller;

    public AIController(GameController controller) {
        this.controller = controller;
    }

    public void updateFly(FlyModel fly, float delta, Array<Rectangle> solidTiles, PlayerModel player) {
        delta = Math.min(delta, MAX_DELTA);
        GameStats gameStats = controller.getModel().getStats();
        if (fly.isDead()) {
            gameStats.recordEnemyKilled("fly");
            applyGravityEffect(fly, delta, solidTiles);
            fly.update(delta);
            return;
        }

        fly.savePrevPosition();

        if (fly.isBeingKnockedBack()) {
            fly.setVelocityY(fly.getVelocityY() + GRAVITY * delta);
        }

        moveAndResolveCollisions(fly, delta, solidTiles);
        checkPlayerInteraction(fly, player, 1);
        fly.update(delta);
    }

    public void updateCrawler(CrawlerModel crawler, float delta, Array<Rectangle> solidTiles, PlayerModel player) {
        delta = Math.min(delta, MAX_DELTA);
        GameStats gameStats = controller.getModel().getStats();

        if (crawler.isDead()) {
            gameStats.recordEnemyKilled("crawler");
            applyGravityEffect(crawler, delta, solidTiles);
            crawler.update(delta);
            return;
        }

        crawler.savePrevPosition();
        crawler.setVelocityY(crawler.getVelocityY() + GRAVITY * delta);

        moveAndResolveCollisions(crawler, delta, solidTiles);
        checkPlayerInteraction(crawler, player, 1);

        if (!crawler.isBeingKnockedBack() && (crawler.getVelocityX() == 0f || isAtLedge(crawler, solidTiles))) {
            crawler.turn();
        }

        crawler.update(delta);
    }

    private void applyGravityEffect(EnemyModel enemy, float delta, Array<Rectangle> solidTiles) {
        enemy.setVelocityY(enemy.getVelocityY() + GRAVITY * delta);
        enemy.setY(enemy.getY() + enemy.getVelocityY() * delta);
        resolveVerticalCollisions(enemy, solidTiles);

        enemy.setX(enemy.getX() + enemy.getVelocityX() * delta);
        resolveHorizontalCollisions(enemy, solidTiles);
    }

    private void moveAndResolveCollisions(EnemyModel enemy, float delta, Array<Rectangle> solidTiles) {
        enemy.setX(enemy.getX() + enemy.getVelocityX() * delta);
        resolveHorizontalCollisions(enemy, solidTiles);

        enemy.setY(enemy.getY() + enemy.getVelocityY() * delta);
        resolveVerticalCollisions(enemy, solidTiles);
    }

    private void resolveVerticalCollisions(EnemyModel enemy, Array<Rectangle> solidTiles) {
        if (enemy instanceof CrawlerModel) {
            ((CrawlerModel) enemy).setOnGround(false);
        }
        Rectangle bounds = enemy.getBounds();

        for (Rectangle tile : solidTiles) {
            if (bounds.overlaps(tile)) {
                if (enemy.getVelocityY() <= 0) {
                    enemy.setY(tile.y + tile.height);
                    enemy.setVelocityY(0f);
                    if (enemy instanceof CrawlerModel) {
                        ((CrawlerModel) enemy).setOnGround(true);
                    }
                } else {
                    enemy.setY(tile.y - enemy.getHeight());
                    enemy.setVelocityY(0f);
                }
                bounds = enemy.getBounds();
            }
        }
    }

    private void resolveHorizontalCollisions(EnemyModel enemy, Array<Rectangle> solidTiles) {
        Rectangle bounds = enemy.getBounds();

        for (Rectangle tile : solidTiles) {
            if (bounds.overlaps(tile)) {
                if (enemy.getVelocityX() > 0) {
                    enemy.setX(tile.x - enemy.getWidth());
                    if (enemy instanceof FlyModel && !enemy.isBeingKnockedBack()) enemy.setFacingRight(false);
                } else if (enemy.getVelocityX() < 0) {
                    enemy.setX(tile.x + tile.width);
                    if (enemy instanceof FlyModel && !enemy.isBeingKnockedBack()) enemy.setFacingRight(true);
                }
                if (!enemy.isBeingKnockedBack()) {
                    enemy.setVelocityX(0f);
                }
                return;
            }
        }
    }

    private boolean isAtLedge(CrawlerModel crawler, Array<Rectangle> solidTiles) {
        if (!crawler.isOnGround()) return false;

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

    public void checkPlayerInteraction(EnemyModel enemy, PlayerModel player, int attackDamage) {
        if (enemy.isDead()) return;

        handlePlayerAttackingEnemy(enemy, player, attackDamage);
        handleEnemyAttackingPlayer(enemy, player);
    }

    private void handlePlayerAttackingEnemy(EnemyModel enemy, PlayerModel player, int attackDamage) {
        if (player.isAttacking()) {
            if (!enemy.wasHitByCurrentAttack() && player.getAttackHitbox().overlaps(enemy.getBounds())) {
                enemy.takeDamage(attackDamage, player.isFacingRight());
                enemy.markHitByCurrentAttack();
                player.gainSoulOnHit();

                if (enemy.isDead()) {
                    player.addPlayerKillsCount();
                }
            }
        } else {
            enemy.resetAttackHitFlag();
        }
    }

    private void handleEnemyAttackingPlayer(EnemyModel enemy, PlayerModel player) {
        if (!enemy.isDead() && !player.isInvincible() && enemy.getBounds().overlaps(player.getBounds())) {
            player.takeDamage(enemy.getContactDamage());
            AudioManager.getInstance().playGetDamageSound();
            player.applyKnockBack();
        }
    }
}
