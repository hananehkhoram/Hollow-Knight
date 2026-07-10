package hana.HollowKnight.model.entities;

public abstract class EnemyModel extends Entity {

    private static final float DEFAULT_DEATH_DURATION = 0.6f;
    protected static final float KNOCKBACK_DURATION = 0.2f;

    protected int health;
    protected int maxHealth;
    protected int contactDamage;

    protected boolean dead = false;
    protected float deathTimer = 0f;
    protected float deathDuration = DEFAULT_DEATH_DURATION;
    private boolean hitByCurrentAttack = false;

    protected boolean isBeingKnockedBack = false;
    protected float knockbackTimer = 0f;
    public static final float KNOCKBACK_FORCE_Y = 200f;
    public static final float KNOCKBACK_FORCE_X = 400f;

    protected static float knockBackForceY = KNOCKBACK_FORCE_Y;
    protected static float knockBackForceX = KNOCKBACK_FORCE_X;

    public static void setKnockBackForceY(float y) {
        knockBackForceY = y;
    }
    public static void setKnockBackForceX(float x) {
        knockBackForceX = x;
    }

    protected float animationTimer = 0f;

    protected EnemyModel(float x, float y, float width, float height, int maxHealth, int contactDamage) {
        super(x, y, width, height);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.contactDamage = contactDamage;
    }

    public void takeDamage(int amount, boolean playerFacingRight) {
        if (dead) return;

        health -= amount;
        applyKnockBack(playerFacingRight);
        if (health <= 0) {
            health = 0;
            die();
        } else {
            onHurt();
        }
    }

    public void applyKnockBack(boolean playerFacingRight) {
        this.isBeingKnockedBack = true;
        this.knockbackTimer = KNOCKBACK_DURATION;
        this.velocityX = playerFacingRight ? knockBackForceX : -knockBackForceX;
        this.velocityY = knockBackForceY;
        this.onGround = false;
    }

    protected void onHurt() {
    }

    protected void die() {
        dead = true;
        setAlive(false);
        deathTimer = 0f;
    }

    public boolean isDead() {
        return dead;
    }

    public boolean isDeathAnimationFinished() {
        return dead && deathTimer >= deathDuration;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getContactDamage() {
        return contactDamage;
    }

    public boolean wasHitByCurrentAttack() {
        return hitByCurrentAttack;
    }

    public void markHitByCurrentAttack() {
        hitByCurrentAttack = true;
    }

    public void resetAttackHitFlag() {
        hitByCurrentAttack = false;
    }

    public boolean isBeingKnockedBack() {
        return isBeingKnockedBack;
    }

    public float getAnimationTimer() {
        return animationTimer;
    }

    public void resetAnimationTimer() {
        this.animationTimer = 0f;
    }

    @Override
    public void update(float delta) {
        animationTimer += delta;

        if (dead) {
            deathTimer += delta;
        }

        if (isBeingKnockedBack) {
            knockbackTimer -= delta;
            if (knockbackTimer <= 0) {
                isBeingKnockedBack = false;
                velocityX = 0f;
            }
        }
    }
}
