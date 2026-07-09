package hana.HollowKnight.model.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import hana.HollowKnight.controller.InputHandler;
import hana.HollowKnight.model.items.CharmType;
import hana.HollowKnight.view.audio.AudioManager;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class PlayerModel extends Entity {

    public static final float DEFAULT_WIDTH = 90;
    public static final float DEFAULT_HEIGHT = 140;

    public static final float MOVE_SPEED = 500;
    public static final float GRAVITY = -1400;
    public static final float JUMP_VELOCITY = 900;
    public static final float DOUBLE_JUMP_VELOCITY = 500;

    private static final float DASH_SPEED = 1000f;
    private static final float DASH_DURATION = 0.8f;
    private static final float DASH_COOLDOWN = 5f;

    private static final float ATTACK_DURATION = 0.22f;
    private static final float ATTACK_COOLDOWN = 0.28f;
    private static final float ATTACK_RANGE = 40f;

    private static final float INVULNERABILITY_DURATION = 0.9f;
    private static final float KNOCKBACK_DURATION = 1f;

    public static final float FOCUS_DURATION = 1.5f;
    private float actionTimer = 0f;
    public static final int DEFAULT_MAX_HEALTH = 5;
    public static final int DEFAULT_MAX_SOUL = 100;
    public static final int SOUL_PER_HIT = 11;
    public static final int MAX_NOTCHES = 3;
    public static final float FOOTSTEP_INTERVAL = 3;
    private float footstepTimer = 0;

    private int health;
    private int maxHealth;
    private int soul;
    private int maxSoul;
    private float soulAccumulator = 0f;

    private boolean jumping;
    private boolean doubleJumpUsed;
    private boolean dashing;
    private boolean attacking;
    private boolean isBeingKnockedBack = false;
    private boolean isFocusing = false;

    private float dashTimer;
    private float dashCooldownTimer;
    private float attackTimer;
    private float attackCooldownTimer;
    private float invulnerabilityTimer;
    private float knockbackTimer;

    private float lastSafeX;
    private float lastSafeY;

    private final Set<CharmType> unlockedCharms = EnumSet.noneOf(CharmType.class);
    private final Set<CharmType> equippedCharms = EnumSet.noneOf(CharmType.class);

    public PlayerModel() {
        this(100f, 100f);
    }

    public PlayerModel(float startX, float startY) {
        super(startX, startY, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.maxHealth = DEFAULT_MAX_HEALTH;
        this.health = maxHealth;
        this.maxSoul = DEFAULT_MAX_SOUL;
        this.soul = 0;
    }

    public void focus(float delta) {
        if (InputHandler.getInstance().isDown(InputHandler.PlayerAction.FOCUS_SOUL) && soul > 0) {

            if (InputHandler.getInstance().isJustPressed(InputHandler.PlayerAction.FOCUS_SOUL) || !isFocusing) {
                setFocusing(true);
                actionTimer = 0f;
                soulAccumulator = 0f;
            }

            velocityX = 0f;

            actionTimer += delta;

            float soulPerSecond = 11f / FOCUS_DURATION;
            soulAccumulator += soulPerSecond * delta;

            if (soulAccumulator >= 1f) {
                int soulToDeduct = (int) soulAccumulator;
                soul = Math.max(0, soul - soulToDeduct);
                soulAccumulator -= soulToDeduct;
            }

            if (actionTimer >= FOCUS_DURATION) {
                setHealth(Math.min(maxHealth, health + 1));
                AudioManager.getInstance().playFocusHealSound();

                actionTimer = 0f;
                soulAccumulator = 0f;
            }

        } else {
            if (isFocusing()) {
                setFocusing(false);
                actionTimer = 0f;
                soulAccumulator = 0f;
                AudioManager.getInstance().stopFocusSound();
            }
        }
    }
    public void moveLeft() {
        if (dashing || isBeingKnockedBack) return;
        velocityX = -MOVE_SPEED;
        facingRight = false;
    }

    public void moveRight() {
        if (dashing || isBeingKnockedBack) return;
        velocityX = MOVE_SPEED;
        facingRight = true;
    }

    public void stopMoving() {
        if (!dashing && !isBeingKnockedBack) velocityX = 0f;
    }

    public void jump() {
        if (onGround) {
            velocityY = JUMP_VELOCITY;
            onGround = false;
            jumping = true;
            doubleJumpUsed = false;
        } else if (!doubleJumpUsed) {
            velocityY = DOUBLE_JUMP_VELOCITY;
            doubleJumpUsed = true;
        }
    }

    public void cutJumpShort() {
        if (velocityY > 0) {
            velocityY *= 0.45f;
        }
    }

    public boolean isDoubleJumpUsed() {
        return doubleJumpUsed;
    }

    public void dash() {
        if (dashing || dashCooldownTimer > 0f) return;
        dashing = true;
        dashTimer = 0f;
        velocityY = 0f;
        velocityX = facingRight ? DASH_SPEED : -DASH_SPEED;
    }

    public void attack() {
        if (attacking || attackCooldownTimer > 0f) return;
        attacking = true;
        attackTimer = 0f;
    }

    public void applyKnockBack() {
        this.isBeingKnockedBack = true;
        this.knockbackTimer = KNOCKBACK_DURATION;
        float knockbackForceX = 350f;
        float knockbackForceY = 300f;
        this.velocityX = facingRight ? -knockbackForceX : knockbackForceX;
        this.velocityY = knockbackForceY;
        this.onGround = false;
    }

    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public Rectangle getAttackHitbox() {
        float hitboxX = facingRight ? (x + width) : (x - ATTACK_RANGE);
        return new Rectangle(hitboxX, y, ATTACK_RANGE, height);
    }

    public boolean isAttacking() { return attacking; }
    public boolean isDashing() { return dashing; }
    public boolean isJumping() { return jumping; }
    public boolean isInvincible() { return invulnerabilityTimer > 0f; }
    public float getInvulnerabilityTimer() { return invulnerabilityTimer; }

    public void takeDamage(int amount) {
        if (invulnerabilityTimer > 0f || !alive) return;
        health = Math.max(0, health - amount);
        AudioManager.getInstance().playGetDamageSound();
        invulnerabilityTimer = INVULNERABILITY_DURATION;
        if (health <= 0) {
            alive = false;
            AudioManager.getInstance().playHeroDeathSound();
        }
    }

    public void heal(int amount) {
        health = Math.min(maxHealth, health + amount);
    }

    public boolean isInvulnerable() {
        return invulnerabilityTimer > 0f;
    }

    public void gainSoulOnHit() {
        addSoul(SOUL_PER_HIT);
    }

    public void addSoul(int amount) {
        soul = Math.min(maxSoul, soul + amount);
    }

    public boolean useSoul(int cost) {
        if (soul < cost) return false;
        soul -= cost;
        return true;
    }

    public void unlockCharm(CharmType type) {
        unlockedCharms.add(type);
    }

    public boolean isCharmUnlocked(CharmType type) {
        return unlockedCharms.contains(type);
    }

    public boolean equipCharm(CharmType type) {
        if (!unlockedCharms.contains(type)) return false;
        if (equippedCharms.contains(type)) return true;
        if (getUsedNotches() + type.getNotchCost() > MAX_NOTCHES) return false;
        equippedCharms.add(type);
        return true;
    }

    public void unequipCharm(CharmType type) {
        equippedCharms.remove(type);
    }

    public int getUsedNotches() {
        int used = 0;
        for (CharmType c : equippedCharms) used += c.getNotchCost();
        return used;
    }

    public int getMaxNotches() {
        return MAX_NOTCHES;
    }

    public Set<CharmType> getEquippedCharms() {
        return equippedCharms;
    }

    public Set<CharmType> getUnlockedCharms() {
        return unlockedCharms;
    }

    @Override
    public void update(float delta) {

        if (isBeingKnockedBack) {
            knockbackTimer -= delta;
            if (knockbackTimer <= 0) {
                isBeingKnockedBack = false;
                velocityX = 0f;
            }
        }

        if (dashing) {
            dashTimer += delta;
            if (dashTimer >= DASH_DURATION) {
                dashing = false;
                dashCooldownTimer = DASH_COOLDOWN;
                velocityX = 0f;
            }
        }
        if (dashCooldownTimer > 0f) dashCooldownTimer -= delta;

        if (attacking) {
            attackTimer += delta;
            if (attackTimer >= ATTACK_DURATION) {
                attacking = false;
                attackCooldownTimer = ATTACK_COOLDOWN;
            }
        }
        if (attackCooldownTimer > 0f) attackCooldownTimer -= delta;

        if (invulnerabilityTimer > 0f) invulnerabilityTimer -= delta;

        if (footstepTimer > 0) {
            footstepTimer -= Gdx.graphics.getDeltaTime();
        }

        if (this.isOnGround() && Math.abs(this.getVelocityX()) > 0.1f && !this.isDashing() && !this.isFocusing()) {
            if (footstepTimer <= 0) {
                AudioManager.getInstance().playMovingSound();
                footstepTimer = FOOTSTEP_INTERVAL;
            }
        } else {
            footstepTimer = 0f;
        }
    }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public int getMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }

    public int getSoul() { return soul; }
    public void setSoul(int soul) { this.soul = soul; }

    public int getMaxSoul() { return maxSoul; }
    public void setMaxSoul(int maxSoul) { this.maxSoul = maxSoul; }

    public List<String> getUnlockedCharmNames() {
        List<String> names = new ArrayList<>();
        for (CharmType c : unlockedCharms) names.add(c.name());
        return names;
    }

    public void setUnlockedCharmsByName(List<String> names) {
        unlockedCharms.clear();
        if (names == null) return;
        for (String n : names) unlockedCharms.add(CharmType.valueOf(n));
    }

    public List<String> getEquippedCharmNames() {
        List<String> names = new ArrayList<>();
        for (CharmType c : equippedCharms) names.add(c.name());
        return names;
    }

    public void setEquippedCharmsByName(List<String> names) {
        equippedCharms.clear();
        if (names == null) return;
        for (String n : names) equippedCharms.add(CharmType.valueOf(n));
    }

    public boolean isFocusing() {
        return isFocusing;
    }

    public void setFocusing(boolean focusing) {
        isFocusing = focusing;
    }

    public float getLastSafeY() {
        return lastSafeY;
    }

    public void setLastSafeY(float lastSafeY) {
        this.lastSafeY = lastSafeY;
    }

    public float getLastSafeX() {
        return lastSafeX;
    }

    public void setLastSafeX(float lastSafeX) {
        this.lastSafeX = lastSafeX;
    }
}
