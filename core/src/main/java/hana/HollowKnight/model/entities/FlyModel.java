package hana.HollowKnight.model.entities;

public class FlyModel extends EnemyModel {

    public enum State { IDLE, SPOTTED, ATTACK, COOLDOWN, DEATH, DEATH2 }

    private static final float WIDTH = 200;
    private static final float HEIGHT = 100;

    private static final float FLY_SPEED = 100f;
    private static final float ATTACK_SPEED = 450f;
    private static final float MAX_Y_TRACKING_SPEED = 120f;
    private static final int MAX_HEALTH = 2;
    private static final int CONTACT_DAMAGE = 1;

    private static final float SPOTTED_DURATION = 0.6f;
    private static final float COOLDOWN_DURATION = 1.5f;
    private static final float DETECTION_RANGE_X = 800f;
    private static final float DETECTION_RANGE_Y = 500f;

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
        if (state != State.DEATH &&  state != State.DEATH2) {
                state = State.DEATH;
                if (!isBeingKnockedBack) {
                    velocityX = 0f;
                    return;
                }
            } else if (state == State.DEATH) {
                state = State.DEATH2;
                if (!isBeingKnockedBack){
                    velocityX = 0f;
                    return;
                }
            }
            return;
        }

        if (isBeingKnockedBack) {
            return;
        }

        stateTimer += delta;

        switch (state) {
            case IDLE:
                velocityX = facingRight ? FLY_SPEED : -FLY_SPEED;

                float distanceY = player.getY() - this.y;
                if (Math.abs(distanceY) > 5f) {
                    velocityY = distanceY * 2.5f;
                    if (velocityY > MAX_Y_TRACKING_SPEED) velocityY = MAX_Y_TRACKING_SPEED;
                    if (velocityY < -MAX_Y_TRACKING_SPEED) velocityY = -MAX_Y_TRACKING_SPEED;
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
                float targetDiffY = attackTargetY - this.y;
                if (Math.abs(targetDiffY) > 5f) {
                    velocityY = targetDiffY * 4f;
                } else {
                    velocityY = 0f;
                }

                if (stateTimer >= 0.6f) {
                    state = State.COOLDOWN;
                    stateTimer = 0f;
                    velocityX = 0f;
                    velocityY = 0f;
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
