package hana.HollowKnight.model.entities;

import com.badlogic.gdx.math.Rectangle;

public class ProjectileModel {
    private final ProjectileType type;
    private float x, y;
    private float width, height;
    private float velocityX, velocityY;
    private boolean active;

    public ProjectileModel(float spawnCenterX, float spawnCenterY, boolean isFacingRight, ProjectileType type) {
        if (type == ProjectileType.POGO) {
            this.width = 150;
            this.height = 200;
            this.velocityX = 0;
            this.velocityY = -1000;
        }else {
            this.width = 100;
            this.height = 100;
            this.velocityX = isFacingRight ? 50 : -50;
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
        } else if (type == ProjectileType.VENEGFUL) {
//            velocityY += 500f * delta;
            velocityX -= 0;
        }

        this.x += velocityX * delta;
        this.y += velocityY * delta;
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
