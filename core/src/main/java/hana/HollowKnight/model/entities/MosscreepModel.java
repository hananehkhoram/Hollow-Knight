package hana.HollowKnight.model.entities;

/**
 * Mosscreep: walks back and forth along the ground and turns around
 * whenever it hits a wall or reaches the edge of a platform.
 * Movement/collision itself is driven from AIController; this class
 * only holds the enemy's own state (facing, walk/turn/death) and reacts
 * to turn() calls.
 */
public class MosscreepModel extends EnemyModel {

    public enum State { WALK, TURN, DEATH }

    private static final float WIDTH = 100f;
    private static final float HEIGHT = 100f;

    private static final float SPEED = 60f;
    private static final int MAX_HEALTH = 1;
    private static final int CONTACT_DAMAGE = 1;
    private static final float TURN_DURATION = 0.35f;

    private State state = State.WALK;
    private float turnTimer = 0f;

    public MosscreepModel(float x, float y) {
        this(x, y, true);
    }

    public MosscreepModel(float x, float y, boolean startFacingRight) {
        super(x, y, WIDTH, HEIGHT, MAX_HEALTH, CONTACT_DAMAGE);
        this.facingRight = startFacingRight;
        this.velocityX = facingRight ? SPEED : -SPEED;
    }

    public State getState() {
        return state;
    }

    public void turn() {
        if (dead) return;

        facingRight = !facingRight;
        velocityX = facingRight ? SPEED : -SPEED;
        state = State.TURN;
        turnTimer = 0f;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (dead) {
            state = State.DEATH;
            velocityX = 0f;
            return;
        }

        if (state == State.TURN) {
            turnTimer += delta;
            if (turnTimer >= TURN_DURATION) {
                state = State.WALK;
            }
        } else {
            state = State.WALK;
        }
    }
}
