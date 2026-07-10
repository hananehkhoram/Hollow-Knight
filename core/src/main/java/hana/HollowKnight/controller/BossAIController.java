package hana.HollowKnight.controller;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import hana.HollowKnight.model.entities.BossModel;
import hana.HollowKnight.model.entities.PlayerModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Drives the False Knight boss.
 *
 * decideNextMove(): distance-based weighted random pick with a hard
 * anti-spam rule (never repeat the previous Move).
 *
 * Each Move is then played out as a chain of BossModel.State animation
 * clips (e.g. CHARGE = TURN -> RUN_ANTIC -> RUN; LEAP_OFFENSIVE = JUMP ->
 * JUMP_ATTACK -> JUMP_ATTACK_HIT). runCurrentMove() advances that chain
 * frame by frame, reading real physics state (onGround, velocityY sign)
 * for the transitions that need it (landing, apex) and plain timers for
 * the rest. Ground-slam/shockwave hitboxes are also checked here since
 * they extend past the boss's own body.
 */
public class BossAIController {

    private static final float MAX_DELTA = 1f / 30f;
    private static final float GRAVITY = -1400f;

    private static final float IDLE_DECISION_DELAY = 0.5f;

    // Mace Slam: ATTACK_ANTIC -> ATTACK (hit lands here) -> ATTACK_RECOVER
    private static final float ATTACK_ANTIC_DURATION = 0.3f;
    private static final float ATTACK_DURATION = 0.25f;
    private static final float ATTACK_RECOVER_DURATION = 0.3f;
    private static final float MACE_SLAM_RANGE = 90f;
    private static final int MACE_SLAM_DAMAGE = 1;

    // Charge: TURN -> RUN_ANTIC -> RUN
    private static final float TURN_DURATION = 0.15f;
    private static final float RUN_ANTIC_DURATION = 0.25f;
    private static final float CHARGE_DURATION = 0.9f;
    private static final float CHARGE_SPEED = 320f;

    // Leap Offensive: JUMP -> JUMP_ATTACK -> JUMP_ATTACK_HIT
    private static final float LEAP_MIN_AIRTIME = 0.15f;
    private static final float JUMP_ATTACK_HIT_DURATION = 0.15f;
    private static final float LEAP_OFFENSIVE_SPEED_X = 260f;
    private static final float LEAP_OFFENSIVE_SPEED_Y = 620f;
    private static final float LEAP_HIT_RANGE = 90f;
    private static final int LEAP_DAMAGE = 1;

    // Leap Defensive: JUMP -> LAND
    private static final float LAND_DURATION = 0.2f;
    private static final float LEAP_DEFENSIVE_SPEED_X = 220f;
    private static final float LEAP_DEFENSIVE_SPEED_Y = 500f;

    // Power Mace Slam (phase 2 only): JUMP -> JUMP_ATTACK -> JUMP_ATTACK_HIT (+ shockwave)
    private static final float POWER_SLAM_HOP_SPEED = 500f;
    private static final float POWER_SLAM_RANGE = 90f;
    private static final float SHOCKWAVE_INITIAL_SPEED = 250f;
    private static final float SHOCKWAVE_ACCELERATION = 300f;
    private static final float SHOCKWAVE_MAX_TRAVEL = 500f;
    private static final float SHOCKWAVE_HEIGHT = 40f;

    private static final float NEAR_DISTANCE = 160f;
    private static final float FAR_DISTANCE = 350f;

    private final Random random = new Random();

    public void updateBoss(BossModel boss, float delta, PlayerModel player, Array<Rectangle> solidTiles) {
        delta = Math.min(delta, MAX_DELTA);

        if (boss.isDead() && !boss.isDeathSequenceActive()) {
            boss.update(delta);
            return;
        }

        boss.savePrevPosition();

        boss.setVelocityY(boss.getVelocityY() + GRAVITY * delta);
        boss.setY(boss.getY() + boss.getVelocityY() * delta);
        resolveVertical(boss, solidTiles);

        boss.setX(boss.getX() + boss.getVelocityX() * delta);
        boolean hitWall = resolveHorizontal(boss, solidTiles);

        if (!boss.isStunSequenceActive() && !boss.isDeathSequenceActive()) {
            runCurrentMove(boss, delta, player, hitWall);
        } else {
            boss.setVelocityX(0f);
        }

        if (boss.isShockwaveActive()) {
            updateShockwave(boss, delta, player);
        }

        boss.update(delta);
    }

    private float speedMultiplier(BossModel boss) {
        return boss.isPhase2() ? BossModel.PHASE2_SPEED_MULTIPLIER : 1f;
    }

    // ---------------------------------------------------------------
    // Move sequencing
    // ---------------------------------------------------------------

    private void runCurrentMove(BossModel boss, float delta, PlayerModel player, boolean hitWall) {
        if (boss.isAvailableForNextMove()) {
            boss.setVelocityX(0f);
            if (boss.isOnGround() && boss.getStateTimer() >= IDLE_DECISION_DELAY) {
                BossModel.Move next = decideNextMove(boss, player);
                boss.setFacingRight(player.getX() >= boss.getX());
                boss.startMove(next);
                beginMove(boss, next);
            }
            return;
        }

        BossModel.Move move = boss.getCurrentMove();
        if (move == null) return;

        switch (move) {
            case MACE_SLAM:
                runMaceSlam(boss, delta, player);
                break;
            case CHARGE:
                runCharge(boss, hitWall);
                break;
            case LEAP_OFFENSIVE:
                runLeapOffensive(boss, delta, player);
                break;
            case LEAP_DEFENSIVE:
                runLeapDefensive(boss);
                break;
            case MACE_SLAM_POWER:
                runPowerSlam(boss, delta, player);
                break;
        }
    }

    /** One-time setup (velocity/state) the moment a move starts. */
    private void beginMove(BossModel boss, BossModel.Move move) {
        switch (move) {
            case MACE_SLAM:
                boss.setState(BossModel.State.ATTACK_ANTIC);
                boss.setVelocityX(0f);
                break;
            case CHARGE:
                boss.setState(BossModel.State.TURN);
                boss.setVelocityX(0f);
                break;
            case LEAP_OFFENSIVE: {
                float dir = boss.isFacingRight() ? 1f : -1f;
                boss.setState(BossModel.State.JUMP);
                boss.setVelocityX(dir * LEAP_OFFENSIVE_SPEED_X * speedMultiplier(boss));
                boss.setVelocityY(LEAP_OFFENSIVE_SPEED_Y);
                boss.setOnGround(false);
                break;
            }
            case LEAP_DEFENSIVE: {
                float dir = boss.isFacingRight() ? 1f : -1f;
                boss.setState(BossModel.State.JUMP);
                boss.setVelocityX(-dir * LEAP_DEFENSIVE_SPEED_X * speedMultiplier(boss));
                boss.setVelocityY(LEAP_DEFENSIVE_SPEED_Y);
                boss.setOnGround(false);
                break;
            }
            case MACE_SLAM_POWER:
                boss.setState(BossModel.State.JUMP);
                boss.setVelocityX(0f);
                boss.setVelocityY(POWER_SLAM_HOP_SPEED);
                boss.setOnGround(false);
                break;
        }
    }

    private void runMaceSlam(BossModel boss, float delta, PlayerModel player) {
        switch (boss.getState()) {
            case ATTACK_ANTIC:
                if (boss.getStateTimer() >= ATTACK_ANTIC_DURATION) {
                    boss.setState(BossModel.State.ATTACK);
                }
                break;
            case ATTACK:
                if (isJustEntered(boss, delta)) {
                    checkGroundSlamHit(boss, player, MACE_SLAM_RANGE, MACE_SLAM_DAMAGE);
                }
                if (boss.getStateTimer() >= ATTACK_DURATION) {
                    boss.setState(BossModel.State.ATTACK_RECOVER);
                }
                break;
            case ATTACK_RECOVER:
                if (boss.getStateTimer() >= ATTACK_RECOVER_DURATION) {
                    finishMove(boss);
                }
                break;
            default:
                break;
        }
    }

    private void runCharge(BossModel boss, boolean hitWall) {
        switch (boss.getState()) {
            case TURN:
                if (boss.getStateTimer() >= TURN_DURATION) {
                    boss.setState(BossModel.State.RUN_ANTIC);
                }
                break;
            case RUN_ANTIC:
                if (boss.getStateTimer() >= RUN_ANTIC_DURATION) {
                    boss.setState(BossModel.State.RUN);
                    float dir = boss.isFacingRight() ? 1f : -1f;
                    boss.setVelocityX(dir * CHARGE_SPEED * speedMultiplier(boss));
                }
                break;
            case RUN:
                if (hitWall || boss.getStateTimer() >= CHARGE_DURATION) {
                    boss.setVelocityX(0f);
                    finishMove(boss);
                }
                break;
            default:
                break;
        }
    }

    private void runLeapOffensive(BossModel boss, float delta, PlayerModel player) {
        switch (boss.getState()) {
            case JUMP:
                if (boss.getStateTimer() >= LEAP_MIN_AIRTIME && boss.getVelocityY() < 0) {
                    boss.setState(BossModel.State.JUMP_ATTACK);
                }
                break;
            case JUMP_ATTACK:
                if (boss.getStateTimer() >= JUMP_ATTACK_HIT_DURATION) {
                    finishMove(boss);
                }
                break;
            default:
                break;
        }
    }

    private void runLeapDefensive(BossModel boss) {
        switch (boss.getState()) {
            case JUMP:
                if (boss.getStateTimer() >= LEAP_MIN_AIRTIME && boss.isOnGround()) {
                    boss.setState(BossModel.State.LAND);
                }
                break;
            case LAND:
                if (boss.getStateTimer() >= LAND_DURATION) {
                    finishMove(boss);
                }
                break;
            default:
                break;
        }
    }

    private void runPowerSlam(BossModel boss, float delta, PlayerModel player) {
        switch (boss.getState()) {
            case JUMP:
                if (boss.getStateTimer() >= LEAP_MIN_AIRTIME && boss.getVelocityY() < 0) {
                    boss.setState(BossModel.State.JUMP_ATTACK);
                }
                break;
            case JUMP_ATTACK:
                if (boss.getStateTimer() >= JUMP_ATTACK_HIT_DURATION) {
                    finishMove(boss);
                }
                break;
            default:
                break;
        }
    }

    private void finishMove(BossModel boss) {
        boss.endMove();
        boss.setState(BossModel.State.IDLE);
    }

    private boolean isJustEntered(BossModel boss, float delta) {
        return boss.getStateTimer() <= delta;
    }

    // ---------------------------------------------------------------
    // Move selection: distance-based weights + randomization + anti-spam
    // ---------------------------------------------------------------

    private BossModel.Move decideNextMove(BossModel boss, PlayerModel player) {
        float distance = Math.abs(player.getX() - boss.getX());

        List<BossModel.Move> candidates = new ArrayList<>();
        List<Float> weights = new ArrayList<>();

        addCandidate(candidates, weights, BossModel.Move.MACE_SLAM,
            distance <= NEAR_DISTANCE ? 5f : 1f, boss.getLastMove());
        addCandidate(candidates, weights, BossModel.Move.CHARGE,
            distance >= FAR_DISTANCE ? 5f : (distance >= NEAR_DISTANCE ? 2f : 0.5f), boss.getLastMove());
        addCandidate(candidates, weights, BossModel.Move.LEAP_OFFENSIVE,
            distance > NEAR_DISTANCE ? 3f : 1f, boss.getLastMove());

        if (boss.isPhase2()) {
            addCandidate(candidates, weights, BossModel.Move.MACE_SLAM_POWER,
                distance <= NEAR_DISTANCE ? 3f : 0.5f, boss.getLastMove());
        }

        // Forced/likely defensive leap after taking rapid damage.
        if (boss.isDefensiveLeapPending()) {
            boss.clearDefensiveLeapPending();
            if (boss.getLastMove() != BossModel.Move.LEAP_DEFENSIVE) {
                return BossModel.Move.LEAP_DEFENSIVE;
            }
        } else {
            addCandidate(candidates, weights, BossModel.Move.LEAP_DEFENSIVE, 0.3f, boss.getLastMove());
        }

        if (candidates.isEmpty()) {
            return BossModel.Move.MACE_SLAM; // fallback, should be rare
        }

        float total = 0f;
        for (float w : weights) total += w;

        float roll = random.nextFloat() * total;
        float cumulative = 0f;
        for (int i = 0; i < candidates.size(); i++) {
            cumulative += weights.get(i);
            if (roll <= cumulative) {
                return candidates.get(i);
            }
        }
        return candidates.get(candidates.size() - 1);
    }

    private void addCandidate(List<BossModel.Move> candidates, List<Float> weights,
                              BossModel.Move move, float weight, BossModel.Move lastMove) {
        if (move == lastMove) return; // anti-spam: never repeat immediately
        candidates.add(move);
        weights.add(weight);
    }

    // ---------------------------------------------------------------
    // Attack hitboxes
    // ---------------------------------------------------------------

    private void checkGroundSlamHit(BossModel boss, PlayerModel player, float range, int damage) {
        float hitboxX = boss.isFacingRight() ? boss.getX() + boss.getWidth() : boss.getX() - range;
        Rectangle hitbox = new Rectangle(hitboxX, boss.getY(), boss.getWidth() + range, boss.getHeight() * 0.5f);

        if (hitbox.overlaps(player.getBounds()) && !player.isInvincible()) {
            player.takeDamage(damage);
            player.applyKnockBack();
        }
    }

    private void updateShockwave(BossModel boss, float delta, PlayerModel player) {
        float startX = boss.isFacingRight() ? boss.getX() + boss.getWidth() : boss.getX();
        boss.updateShockwave(delta, SHOCKWAVE_ACCELERATION);

        float traveled = Math.abs(boss.getShockwaveX() - startX);
        Rectangle waveBox = new Rectangle(
            boss.isFacingRight() ? boss.getShockwaveX() : boss.getShockwaveX() - 10f,
            boss.getY(), 10f, SHOCKWAVE_HEIGHT);

        if (waveBox.overlaps(player.getBounds()) && !player.isInvincible()) {
            player.takeDamage(MACE_SLAM_DAMAGE * 2);
            player.applyKnockBack();
            boss.stopShockwave();
            return;
        }

        if (traveled >= SHOCKWAVE_MAX_TRAVEL) {
            boss.stopShockwave();
        }
    }

    // ---------------------------------------------------------------
    // Ground/wall physics
    // ---------------------------------------------------------------

    private void resolveVertical(BossModel boss, Array<Rectangle> solidTiles) {
        boss.setOnGround(false);
        Rectangle bounds = boss.getBounds();

        for (Rectangle tile : solidTiles) {
            if (bounds.overlaps(tile)) {
                if (boss.getVelocityY() <= 0) {
                    boss.setY(tile.y + tile.height);
                    boss.setVelocityY(0f);
                    boss.setOnGround(true);
                } else {
                    boss.setY(tile.y - boss.getHeight());
                    boss.setVelocityY(0f);
                }
                bounds = boss.getBounds();
            }
        }
    }

    private boolean resolveHorizontal(BossModel boss, Array<Rectangle> solidTiles) {
        Rectangle bounds = boss.getBounds();

        for (Rectangle tile : solidTiles) {
            if (bounds.overlaps(tile)) {
                if (boss.getVelocityX() > 0) {
                    boss.setX(tile.x - boss.getWidth());
                } else if (boss.getVelocityX() < 0) {
                    boss.setX(tile.x + tile.width);
                }
                boss.setVelocityX(0f);
                return true;
            }
        }
        return false;
    }
}
