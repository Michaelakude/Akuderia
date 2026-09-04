package io.github.akuderia.Entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

import io.github.akuderia.Entities.GameObject;

public abstract class GameObject {
    protected final Rectangle rect;
    protected Texture texture;
    protected float z;
    protected float gravity;
    protected float terminalVelocity;

    public GameObject(float x, float y, float z, float w, float h, Texture texture) {
        this.rect = new Rectangle(x, y, w, h);
        this.z = z;
        this.texture = texture;
        this.gravity = 31.25f;
        this.terminalVelocity = 750;
    }

    public GameObject(float x, float y,float z, float w, float h) {
        this(x, y, z, w, h, null);
    }

    public boolean overlaps(GameObject other) {
        return rect.overlaps(other.rect);
    }

    public void draw(Batch batch) {
        if (texture == null) { return; }
        batch.draw(texture, rect.x, rect.y, rect.width, rect.height);
    }

    public Rectangle getRect() {
        return rect;
    }

    public float getGravity() {
        return gravity;
    }

    public float getVT() {
        return terminalVelocity;
    }

    protected void setGravity(float gravity) {
        this.gravity = gravity;
    }

    protected void setVT(float terminalVelocity) {
        this.terminalVelocity = terminalVelocity;
    }

    abstract public void update(float deltaTime);
}
