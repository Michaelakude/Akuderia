package io.github.akuderia.Entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

import io.github.akuderia.Entities.GameObject;

public abstract class GameObject {
    protected final Rectangle rect;
    protected Texture texture;

    public GameObject(float x, float y, float w, float h, Texture texture) {
        this.rect = new Rectangle(x, y, w, h);
        this.texture = texture;
    }

    public GameObject(float x, float y, float w, float h) {
        this(x, y, w, h, null);
    }

    public boolean overlaps(GameObject other) {
        return rect.overlaps(other.rect);
    }

    public void draw(Batch batch) {
        if (texture == null) { return; }
        batch.draw(texture, rect.x, rect.y, rect.width, rect.height);
    }

    abstract public void update(float deltaTime);
}
