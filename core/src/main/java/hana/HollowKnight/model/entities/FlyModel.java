package hana.HollowKnight.model.entities;

public class FlyModel extends EnemyModel {

    public enum State { IDLE, SPOTTED, ATTACK, COOLDOWN, DEATH, DEATH2 }

    private static final float WIDTH = 250;
    private static final float HEIGHT = 150;

    private static final float FLY_SPEED = 100f;
    private static final float ATTACK_SPEED = 450f;
    private static final float Y_TRACKING_SPEED = 80f;
    private static final int MAX_HEALTH = 2;
    private static final int CONTACT_DAMAGE = 1;

    private static final float SPOTTED_DURATION = 0.6f;
    private static final float COOLDOWN_DURATION = 1.5f;
    private static final float DETECTION_RANGE_X = 500f;
    private static final float DETECTION_RANGE_Y = 250f;

    private PlayerModel player;

    private State state = State.IDLE;
    private float stateTimer = 0f;
    private float attackTargetY = 0f;

    public FlyModel(float x, float y ,PlayerModel player) {
        super(x, y, WIDTH, HEIGHT, MAX_HEALTH, CONTACT_DAMAGE);
        this.facingRight = true;
        this.state = State.IDLE;
        this.player = player;
    }

    public State getState() {
        return state;
    }

    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (dead) {
            if (state != State.DEATH) {
                state = State.DEATH;
                velocityX = 0f;
            } else {
                state = State.DEATH2;
                velocityX = 0f;
            }
            return;
        }

        stateTimer += delta;

        switch (state) {
            case IDLE:
                velocityX = facingRight ? FLY_SPEED : -FLY_SPEED;

                if (Math.abs(player.getY() - this.y) > 10f) {
                    velocityY = player.getY() > this.y ? Y_TRACKING_SPEED : -Y_TRACKING_SPEED;
                } else {
                    velocityY = 0f;
                }

                if (isPlayerInSight(player)) {
                    state = State.SPOTTED;
                    stateTimer = 0f;
                    velocityX = 0f;
                    velocityY = 0f;
                    attackTargetY = player.getY();
                    facingRight = (player.getX() > this.x);
                }
                break;

            case SPOTTED:
                velocityX = 0f;
                velocityY = 0f;

                if (stateTimer >= SPOTTED_DURATION) {
                    state = State.ATTACK;
                    stateTimer = 0f;
                    velocityX = facingRight ? ATTACK_SPEED : -ATTACK_SPEED;
                }
                break;

            case ATTACK:
                velocityY = 0f;

                if (stateTimer >= 0.8f) {
                    state = State.COOLDOWN;
                    stateTimer = 0f;
                    velocityX = 0f;
                }
                break;

            case COOLDOWN:
                velocityX = 0f;
                velocityY = 0f;
                if (stateTimer >= COOLDOWN_DURATION) {
                    state = State.IDLE;
                    stateTimer = 0f;
                }
                break;
        }
    }

    private boolean isPlayerInSight(PlayerModel player) {
        float diffX = Math.abs(player.getX() - this.x);
        float diffY = Math.abs(player.getY() - this.y);
        return diffX <= DETECTION_RANGE_X && diffY <= DETECTION_RANGE_Y;
    }
}
