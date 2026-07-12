package hana.HollowKnight.model.entities;

import com.badlogic.gdx.math.Rectangle;

public class ProjectileModel {
    private float x, y;
    private float width, height;
    private float velocityX, velocityY;
    private final ProjectileType type;
    private boolean active;

    public ProjectileModel(float x, float y, float width, float height, float velocityX, float velocityY, ProjectileType type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.type = type;
        this.active = true;
    }

    public void update(float delta) {
        if (type == ProjectileType.POGO) {
            velocityY -= 500f * delta;
        }

        this.x += velocityX * delta;
        this.y += velocityY * delta;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public ProjectileType getType() { return type; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
