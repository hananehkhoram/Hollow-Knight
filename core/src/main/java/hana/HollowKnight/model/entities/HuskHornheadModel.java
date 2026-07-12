package hana.HollowKnight.model.entities;

import com.badlogic.gdx.math.Rectangle;

public class HuskHornheadModel extends EnemyModel {

    public enum State {
        WALK, IDLE, TURN, ATTACK_ANTICIPATE, ATTACK_LUNGE, ATTACK_COOLDOWN, DEATH_AIR, DEATH_LAND
    }

    private static final float WIDTH = 239;
    private static final float HEIGHT = 219;
    private static final int MAX_HEALTH = 3;
    private static final int CONTACT_DAMAGE = 1;

    private static final float WALK_SPEED = 70f;
    private static final float CHARGE_SPEED = 420f;

    private static final float WALK_DURATION = 3f;
    private static final float REST_DURATION = 1.5f;
    private static final float TURN_DURATION = 0.3f;
    private static final float ANTICIPATE_DURATION = 0.5f;
    private static final float MAX_LUNGE_DURATION = 2.5f;
    private static final float COOLDOWN_DURATION = 0.8f;

    private static final float VISION_WIDTH = 500f;
    private static final float VISION_HEIGHT = 150f;

    private State state = State.WALK;
    private float stateTimer = 0f;
    private float walkTimer = 0f;

    public HuskHornheadModel(float x, float y) {
        this(x, y, true);
    }

    public HuskHornheadModel(float x, float y, boolean startFacingRight) {
        super(x, y, WIDTH, HEIGHT, MAX_HEALTH, CONTACT_DAMAGE);
        this.facingRight = startFacingRight;
        this.velocityX = facingRight ? WALK_SPEED : -WALK_SPEED;
    }

    public State getState() {
        return state;
    }

    public float getStateTimer() {
        return stateTimer;
    }

    public Rectangle getVisionBounds() {
        float visionX = facingRight ? (getX() + width) : (getX() - VISION_WIDTH);
        float visionY = getY() - (VISION_HEIGHT - height) / 2f;
        return new Rectangle(visionX, visionY, VISION_WIDTH, VISION_HEIGHT);
    }

    public void turn() {
        if (dead || state == State.ATTACK_LUNGE || state == State.ATTACK_ANTICIPATE) return;
        facingRight = !facingRight;
        velocityX = facingRight ? WALK_SPEED : -WALK_SPEED;
        state = State.TURN;
        stateTimer = 0f;
    }

    public void beginAnticipate(boolean playerIsToTheRight) {
        if (dead || state == State.ATTACK_ANTICIPATE || state == State.ATTACK_LUNGE) return;
        facingRight = playerIsToTheRight;
        velocityX = 0f;
        state = State.ATTACK_ANTICIPATE;
        stateTimer = 0f;
    }

    public void endLunge() {
        if (state != State.ATTACK_LUNGE) return;
        velocityX = 0f;
        state = State.ATTACK_COOLDOWN;
        stateTimer = 0f;
    }

    private void startLunge() {
        velocityX = facingRight ? CHARGE_SPEED : -CHARGE_SPEED;
        state = State.ATTACK_LUNGE;
        stateTimer = 0f;
    }

    private void startRest() {
        velocityX = 0f;
        state = State.IDLE;
        stateTimer = 0f;
    }

    private void resumeWalk() {
        velocityX = facingRight ? WALK_SPEED : -WALK_SPEED;
        state = State.WALK;
        stateTimer = 0f;
        walkTimer = 0f;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (dead) {
            state = onGround ? State.DEATH_LAND : State.DEATH_AIR;
            if (!isBeingKnockedBack()) velocityX = 0f;
            return;
        }

        if (isBeingKnockedBack()) return;

        stateTimer += delta;

        switch (state) {
            case WALK:
                walkTimer += delta;
                if (walkTimer >= WALK_DURATION) {
                    startRest();
                }
                break;

            case IDLE:
                if (stateTimer >= REST_DURATION) {
                    resumeWalk();
                }
                break;

            case TURN:
                if (stateTimer >= TURN_DURATION) {
                    state = State.WALK;
                }
                break;

            case ATTACK_ANTICIPATE:
                if (stateTimer >= ANTICIPATE_DURATION) {
                    startLunge();
                }
                break;

            case ATTACK_LUNGE:
                if (stateTimer >= MAX_LUNGE_DURATION) {
                    endLunge();
                }
                break;

            case ATTACK_COOLDOWN:
                if (stateTimer >= COOLDOWN_DURATION) {
                    resumeWalk();
                }
                break;

            default:
                break;
        }
    }
}
