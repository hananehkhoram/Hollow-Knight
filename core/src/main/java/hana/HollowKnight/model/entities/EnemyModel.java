package hana.HollowKnight.model.entities;

/**
 * Common base for every enemy type (Crawler/Mosscreep, Hornhead, Spitter, Zote, ...).
 * Holds shared combat state (health, contact damage, death handling) on top of
 * the physics/position state already provided by Entity.
 */
public abstract class EnemyModel extends Entity {

    private static final float DEFAULT_DEATH_DURATION = 0.6f;

    protected int health;
    protected int maxHealth;
    protected int contactDamage;

    protected boolean dead = false;
    protected float deathTimer = 0f;
    protected float deathDuration = DEFAULT_DEATH_DURATION;
    private boolean hitByCurrentAttack = false;

    protected EnemyModel(float x, float y, float width, float height, int maxHealth, int contactDamage) {
        super(x, y, width, height);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.contactDamage = contactDamage;
    }

    public void takeDamage(int amount) {
        if (dead) return;

        health -= amount;
        if (health <= 0) {
            health = 0;
            die();
        } else {
            onHurt();
        }
    }

    /** Hook for subclasses that want a hurt reaction (flash, brief stagger, etc). */
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

    /** True once this enemy has already been hit by the player's current attack swing. */
    public boolean wasHitByCurrentAttack() {
        return hitByCurrentAttack;
    }

    public void markHitByCurrentAttack() {
        hitByCurrentAttack = true;
    }

    public void resetAttackHitFlag() {
        hitByCurrentAttack = false;
    }

    @Override
    public void update(float delta) {
        if (dead) {
            deathTimer += delta;
        }
    }
}
