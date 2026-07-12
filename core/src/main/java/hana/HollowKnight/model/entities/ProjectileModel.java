package hana.HollowKnight.model.entities;

import com.badlogic.gdx.math.Rectangle;

import java.util.HashSet;
import java.util.Set;

public class ProjectileModel {
    private final ProjectileType type;
    private float x, y;
    private float width, height;
    private float velocityX, velocityY;
    private boolean active;

    private final Set<Object> hitTargets = new HashSet<>();

    public ProjectileModel(float spawnCenterX, float spawnCenterY, boolean isFacingRight, ProjectileType type) {
        if (type == ProjectileType.POGO) {
            this.width = 150;
            this.height = 200;
            this.velocityX = 0;
            this.velocityY = -1000;
        }else {
            this.width = 317;
            this.height = 100;
            this.velocityX = isFacingRight ? 2000 : -2000;
            this.velocityY = 0;
        }
        this.x = spawnCenterX - width / 2f;
        this.y = spawnCenterY - height / 2f;
        this.type = type;
        this.active = true;
    }

    public void update(float delta) {
        if (type == ProjectileType.POGO) {
            velocityY += PlayerModel.GRAVITY * 2 * delta;
        }

        this.x += velocityX * delta;
        this.y += velocityY * delta;
    }

    public boolean tryRegisterHit(Object target) {
        return hitTargets.add(target);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public ProjectileType getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
