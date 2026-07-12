package hana.HollowKnight.model.entities;

public class BossModel extends EnemyModel {

    public enum State {
        IDLE, TURN,
        RUN_ANTIC, RUN,
        ATTACK_ANTIC, ATTACK, ATTACK_RECOVER,
        JUMP, JUMP_ATTACK, LAND,
        BODY, BODY_STUN, STUN_RECOVER,
        DEATH_FALL, DEATH_HIT, DEATH_LAND
    }

    public enum Move { MACE_SLAM, CHARGE, LEAP_OFFENSIVE, LEAP_DEFENSIVE, MACE_SLAM_POWER }

    private static final float WIDTH = 100;
    private static final float HEIGHT = 100;

    private static final int MAX_HEALTH = 16;
    private static final int CONTACT_DAMAGE = 2;

    private static final float BODY_DURATION = 0.4f;
    private static final float BODY_STUN_DURATION = 3f;
    private static final float STUN_RECOVER_DURATION = 1f;
    private static final float STUN_DAMAGE_MULTIPLIER = 2f;

    private static final float DEATH_FALL_DURATION = 0.4f;
    private static final float DEATH_HIT_DURATION = 0.2f;

    public static final float PHASE2_SPEED_MULTIPLIER = 1.5f;

    private static final float DAMAGE_WINDOW = 1f;
    private static final int DAMAGE_WINDOW_HIT_THRESHOLD = 3;

    private State state = State.IDLE;
    private float stateTimer = 0f;

    private Move currentMove = null;
    private Move lastMove = null;

    private boolean phase2 = false;
    private boolean staggerTriggered = false;

    private float damageWindowTimer = 0f;
    private int hitsInWindow = 0;
    private boolean defensiveLeapPending = false;

    private boolean shockwaveActive = false;
    private float shockwaveX;
    private float shockwaveSpeed;

    public BossModel(float x, float y) {
        super(x, y, WIDTH, HEIGHT, MAX_HEALTH, CONTACT_DAMAGE);
        this.facingRight = true;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
        this.stateTimer = 0f;
    }

    public float getStateTimer() {
        return stateTimer;
    }

    public Move getCurrentMove() {
        return currentMove;
    }

    public Move getLastMove() {
        return lastMove;
    }

    public void startMove(Move move) {
        this.currentMove = move;
        this.lastMove = move;
    }

    public void endMove() {
        this.currentMove = null;
    }

    public boolean isAvailableForNextMove() {
        return state == State.IDLE && currentMove == null;
    }

    public boolean isPhase2() {
        return phase2;
    }

    public boolean isStunSequenceActive() {
        return state == State.BODY || state == State.BODY_STUN || state == State.STUN_RECOVER;
    }

    public boolean isVulnerableStunned() {
        return state == State.BODY_STUN;
    }

    public boolean isDeathSequenceActive() {
        return state == State.DEATH_FALL || state == State.DEATH_HIT || state == State.DEATH_LAND;
    }

    public boolean isDefensiveLeapPending() {
        return defensiveLeapPending;
    }

    public void clearDefensiveLeapPending() {
        defensiveLeapPending = false;
    }

    public boolean isShockwaveActive() {
        return shockwaveActive;
    }

    public float getShockwaveX() {
        return shockwaveX;
    }

    public float getShockwaveSpeed() {
        return shockwaveSpeed;
    }

    public void startShockwave(float startX, float initialSpeed) {
        shockwaveActive = true;
        shockwaveX = startX;
        shockwaveSpeed = initialSpeed;
    }

    public void updateShockwave(float delta, float acceleration) {
        if (!shockwaveActive) return;
        shockwaveSpeed += acceleration * delta;
        shockwaveX += shockwaveSpeed * (facingRight ? 1 : -1) * delta;
    }

    public void stopShockwave() {
        shockwaveActive = false;
    }

    @Override
    public void takeDamage(int amount, boolean isFacingRight) {
        if (dead) return;

        int finalAmount = isVulnerableStunned() ? Math.round(amount * STUN_DAMAGE_MULTIPLIER) : amount;
        super.takeDamage(finalAmount, isFacingRight);
        if (dead) return;

        hitsInWindow++;
        damageWindowTimer = 0f;
        if (hitsInWindow >= DAMAGE_WINDOW_HIT_THRESHOLD) {
            defensiveLeapPending = true;
            hitsInWindow = 0;
        }

        if (!staggerTriggered && !isStunSequenceActive() && health <= maxHealth / 2) {
            staggerTriggered = true;
            currentMove = null;
            velocityX = 0f;
            shockwaveActive = false;
            setState(State.BODY);
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (dead && !isDeathSequenceActive()) {
            setState(State.DEATH_FALL);
        }

        stateTimer += delta;

        damageWindowTimer += delta;
        if (damageWindowTimer >= DAMAGE_WINDOW) {
            damageWindowTimer = 0f;
            hitsInWindow = 0;
        }

        switch (state) {
            case BODY:
                if (stateTimer >= BODY_DURATION) setState(State.BODY_STUN);
                break;
            case BODY_STUN:
                if (stateTimer >= BODY_STUN_DURATION) setState(State.STUN_RECOVER);
                break;
            case STUN_RECOVER:
                if (stateTimer >= STUN_RECOVER_DURATION) {
                    phase2 = true;
                    setState(State.IDLE);
                }
                break;
            case DEATH_FALL:
                if (stateTimer >= DEATH_FALL_DURATION) setState(State.DEATH_HIT);
                break;
            case DEATH_HIT:
                if (stateTimer >= DEATH_HIT_DURATION) setState(State.DEATH_LAND);
                break;
            default:
                break;
        }
    }
}
