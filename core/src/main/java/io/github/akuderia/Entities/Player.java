package io.github.akuderia.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Player extends GameObject {
    private static final float SCALE = 1 / 32f;
    private final Viewport gameViewport;
    private final Vector2 moveDirection = new Vector2();
    private static float SPEED = 3f;
    private Vector2 inputMovement = new Vector2();
    private boolean isSprint = false;

    public Player(float x, float y, Viewport gameViewport, Texture texture) {
        super(x, y, texture.getWidth() * SCALE, texture.getHeight() * SCALE, texture);
        this.gameViewport = gameViewport;
    }

    @Override
    public void update(float deltaTime) {
        move(deltaTime);
    }

    private void move(float deltaTime) {
        if(moveDirection.isZero()) return;
        float newX = rect.getX() + moveDirection.x * SPEED * deltaTime;
        float newY = rect.getY() + moveDirection.y * SPEED * deltaTime;

        newX = MathUtils.clamp(newX, 0, gameViewport.getWorldWidth() - rect.getWidth());
        newY = MathUtils.clamp(newY, 0, gameViewport.getWorldHeight() - rect.getHeight());

        rect.setPosition(newX, newY);
    }

    public void processInput() {
        inputMovement.setZero();
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            inputMovement.y += 1;
            System.out.println("W");
        }
        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            inputMovement.x -= 1;
            System.out.println("A");
        }
        if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            inputMovement.y -= 1;
            System.out.println("S");
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            inputMovement.x += 1;
            System.out.println("D");
        }

        // inputMovement.nor(); // Normalise diangle
        changeDirection(inputMovement);
    }

    public void updateLogic(float deltaTime) {
        update(deltaTime);
    }

    public void changeDirection(Vector2 direction) {
        moveDirection.set(direction);
    }

    public void respawn(float x, float y) {
        rect.setPosition(x, y);
    }
}