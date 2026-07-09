package hana.HollowKnight.model.entities;

import com.badlogic.gdx.math.Rectangle;

public abstract class Entity {

    protected float x, y;
    protected float width, height;
    protected float velocityX, velocityY;

    protected boolean facingRight = true;
    protected boolean onGround = false;
    protected boolean alive = true;

    protected Entity(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void update(float delta) {
        // زیرکلاس‌ها این متد را override و منطق حرکت/AI خودشان را اضافه می‌کنند
    }

    // --- Getters / Setters ---
    public float getX() { return x; }
    public float getY() { return y; }
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void setPosition(float x, float y) { this.x = x; this.y = y;  this.velocityX = 0; this.velocityY = 0; }

    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public float getVelocityX() { return velocityX; }
    public float getVelocityY() { return velocityY; }
    public void setVelocityX(float velocityX) { this.velocityX = velocityX; }
    public void setVelocityY(float velocityY) { this.velocityY = velocityY; }

    public boolean isFacingRight() { return facingRight; }
    public void setFacingRight(boolean facingRight) { this.facingRight = facingRight; }

    public boolean isOnGround() { return onGround; }
    public void setOnGround(boolean onGround) { this.onGround = onGround; }

    public boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }

    public void landOn(float surfaceY) {
        this.y = surfaceY;
        this.velocityY = 0f;
        this.onGround = true;
    }

    public void hitCeiling(float surfaceY) {
        this.y = surfaceY - height;
        this.velocityY = 0f;
    }

    public void setGrounded(boolean grounded) {
        this.onGround = grounded;
    }
}
