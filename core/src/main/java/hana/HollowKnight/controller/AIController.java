package hana.HollowKnight.controller;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import hana.HollowKnight.model.entities.CrawlerModel;
import hana.HollowKnight.model.entities.EnemyModel;
import hana.HollowKnight.model.entities.FlyModel;
import hana.HollowKnight.model.entities.PlayerModel;
import hana.HollowKnight.view.audio.AudioManager;

public class AIController {

    private static final float MAX_DELTA = 1f / 30f;
    private static final float GRAVITY = -1400f;
    private static final float EDGE_PROBE_DEPTH = 6f;
    private static final float EDGE_PROBE_WIDTH = 2f;

    public void updateFly(FlyModel fly, float delta, Array<Rectangle> solidTiles, PlayerModel player) {
        delta = Math.min(delta, MAX_DELTA);

        if (fly.isDead()) {
            fly.setVelocityY(fly.getVelocityY() + GRAVITY * delta);
            fly.setY(fly.getY() + fly.getVelocityY() * delta);

            Rectangle bounds = fly.getBounds();
            for (Rectangle tile : solidTiles) {
                if (bounds.overlaps(tile)) {
                    if (fly.getVelocityY() <= 0) {
                        fly.setY(tile.y + tile.height);
                        fly.setVelocityY(0f);
                    } else {
                        fly.setY(tile.y - fly.getHeight());
                        fly.setVelocityY(0f);
                    }
                    break;
                }
            }
            fly.update(delta);
            return;
        }

        fly.savePrevPosition();

        if (fly.isBeingKnockedBack()) {
            fly.setVelocityY(fly.getVelocityY() + GRAVITY * delta);
        }

        fly.setX(fly.getX() + fly.getVelocityX() * delta);
        Rectangle bounds = fly.getBounds();
        for (Rectangle tile : solidTiles) {
            if (bounds.overlaps(tile)) {
                if (fly.getVelocityX() > 0) {
                    fly.setX(tile.x - fly.getWidth());
                    if (!fly.isBeingKnockedBack()) fly.setFacingRight(false);
                } else if (fly.getVelocityX() < 0) {
                    fly.setX(tile.x + tile.width);
                    if (!fly.isBeingKnockedBack()) fly.setFacingRight(true);
                }
                break;
            }
        }

        fly.setY(fly.getY() + fly.getVelocityY() * delta);
        bounds = fly.getBounds();
        for (Rectangle tile : solidTiles) {
            if (bounds.overlaps(tile)) {
                if (fly.getVelocityY() > 0) {
                    fly.setY(tile.y - fly.getHeight());
                } else if (fly.getVelocityY() < 0) {
                    fly.setY(tile.y + tile.height);
                }
                fly.setVelocityY(0f);
                break;
            }
        }

        checkPlayerInteraction(fly, player, 1);
        fly.update(delta);
    }

    public void updateCrawler(CrawlerModel crawler, float delta, Array<Rectangle> solidTiles, PlayerModel player) {
        delta = Math.min(delta, MAX_DELTA);

        if (crawler.isDead()) {
            crawler.setVelocityY(crawler.getVelocityY() + GRAVITY * delta);
            crawler.setY(crawler.getY() + crawler.getVelocityY() * delta);
            resolveVerticalCollisions(crawler, solidTiles);

            crawler.setX(crawler.getX() + crawler.getVelocityX() * delta);
            resolveHorizontalCollisions(crawler, solidTiles);

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

        if (!crawler.isBeingKnockedBack()) {
            boolean atLedge = crawler.isOnGround() && !hitWall && isAtLedge(crawler, solidTiles);
            if (hitWall || atLedge) {
                crawler.turn();
            }
        }

        crawler.update(delta);
    }

    private void resolveVerticalCollisions(CrawlerModel crawler, Array<Rectangle> solidTiles) {
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

    private boolean resolveHorizontalCollisions(CrawlerModel crawler, Array<Rectangle> solidTiles) {
        Rectangle bounds = crawler.getBounds();

        for (Rectangle tile : solidTiles) {
            if (bounds.overlaps(tile)) {
                if (crawler.getVelocityX() > 0) {
                    crawler.setX(tile.x - crawler.getWidth());
                } else if (crawler.getVelocityX() < 0) {
                    crawler.setX(tile.x + tile.width);
                }
                if (!crawler.isBeingKnockedBack()) {
                    crawler.setVelocityX(0f);
                }
                return true;
            }
        }
        return false;
    }

    private boolean isAtLedge(CrawlerModel crawler, Array<Rectangle> solidTiles) {
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

        if (!enemy.isDead() && !player.isInvincible() && enemy.getBounds().overlaps(player.getBounds())) {
            player.takeDamage(enemy.getContactDamage());
            AudioManager.getInstance().playGetDamageSound();
            player.applyKnockBack();
        }
    }}
