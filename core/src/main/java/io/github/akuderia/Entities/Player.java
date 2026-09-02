package io.github.akuderia.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.akuderia.Animations.PlayerAnimationController;

public class Player extends GameObject {
    private static final float SCALE = 1 / 32f;
    private final Viewport gameViewport;
    private final Vector2 moveDirection = new Vector2();
    private static float SPEED = 3f;
    private Vector2 inputMovement = new Vector2();
    private final PlayerAnimationController animationController;
    private String lastDirection = "s";
    private static final float FRAME_SIZE = 64 * SCALE;
    private static final float PAD_LEFT   = 26 * SCALE;
    private static final float PAD_BOTTOM = 20 * SCALE;

    // Offset from rect's position to where the full frame should be drawn
    private static final float DRAW_OFFSET_X = -PAD_LEFT;
    private static final float DRAW_OFFSET_Y = -PAD_BOTTOM;

    public Player(float x, float y, Viewport gameViewport, Texture idleSheet, Texture walkSheet) {
        super(x, y, 12 * SCALE, 27 * SCALE, idleSheet);
        this.gameViewport = gameViewport;
        animationController = new PlayerAnimationController(0.1f);
        Animation<TextureRegion>[] idleAnimations = animationController.makeDirectionalAnimations( idleSheet, 64, 64 );
        Animation<TextureRegion>[] walkAnimations = animationController.makeDirectionalAnimations( walkSheet, 64, 64 );

        String[] idleStates = { "idle_nw", "idle_w", "idle_sw", "idle_s", "idle_se", "idle_e", "idle_ne", "idle_n" };
        String[] walkStates = { "walk_nw", "walk_w", "walk_sw", "walk_s", "walk_se", "walk_e", "walk_ne", "walk_n" };

        animationController.loadAnimations(idleStates, idleAnimations);
        animationController.loadAnimations(walkStates, walkAnimations);
        animationController.changeState("idle_s", true );
    }

    @Override
    public void update(float deltaTime) {
        animationController.update(deltaTime);
        move(deltaTime);
        updateAnimation();
    }

    @Override
    public void draw(Batch batch) {
        TextureRegion currentFrame = animationController.getCurrentFrame();
        if (currentFrame == null) { return; }
        // Draw the full frame, offset so it lines up around the tight rect
        batch.draw(
            currentFrame,
            rect.x + DRAW_OFFSET_X,
            rect.y + DRAW_OFFSET_Y,
            FRAME_SIZE,
            FRAME_SIZE
        );
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
        }
        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            inputMovement.x -= 1;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            inputMovement.y -= 1;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            inputMovement.x += 1;
        }

        // inputMovement.nor(); // Normalise diagonal
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

    private String getDirectionName() {
        if (moveDirection.x < 0 && moveDirection.y > 0)
            { lastDirection = "nw";
        }
        else if (moveDirection.x < 0 && moveDirection.y == 0) { lastDirection = "w"; }
        else if (moveDirection.x < 0 && moveDirection.y < 0) { lastDirection = "sw"; }
        else if (moveDirection.x == 0 && moveDirection.y < 0) { lastDirection = "s"; }
        else if (moveDirection.x > 0 && moveDirection.y < 0) { lastDirection = "se"; }
        else if (moveDirection.x > 0 && moveDirection.y == 0) { lastDirection = "e"; }
        else if (moveDirection.x > 0 && moveDirection.y > 0) { lastDirection = "ne"; }
        else if (moveDirection.x == 0 && moveDirection.y > 0) { lastDirection = "n"; } return lastDirection;
    }

    private void updateAnimation() {
        if (moveDirection.isZero()) {
            animationController.changeState( "idle_" + lastDirection, true );
        } else {
            String direction = getDirectionName(); animationController.changeState( "walk_" + direction, true );
        }
    }
    public Rectangle getRect() {
        return rect;
    }
}