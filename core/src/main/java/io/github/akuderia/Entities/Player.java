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
    private static float SPEED = 2f;
    private static float SprintSpeed = SPEED * 1.8f;

    private Vector2 inputMovement = new Vector2();
    private final PlayerAnimationController animationController;
    private String lastDirection = "s";
    private static final float FRAME_SIZE = 64 * SCALE;
    private static final float PAD_LEFT   = 26 * SCALE;
    private static final float PAD_BOTTOM = 20 * SCALE;

    // Offset from rect's position to where the full frame should be drawn
    private static final float DRAW_OFFSET_X = -PAD_LEFT;
    private static final float DRAW_OFFSET_Y = -PAD_BOTTOM;

    private boolean isSprint = false;
    private boolean isJump = false;
    private boolean wasOnFloor = true;
    private float verticalVelocity = 0f; // z-axis velocity for the hop

    private static final float JUMP_HEIGHT = 1.3f;
    private static final float JUMP_TIME_TO_PEAK = 0.4f;

    private static final float JUMP_GRAVITY = (2f * JUMP_HEIGHT) / (JUMP_TIME_TO_PEAK * JUMP_TIME_TO_PEAK);
    private static final float JUMP_VELOCITY = (2f * JUMP_HEIGHT) / JUMP_TIME_TO_PEAK;
    private static final float FALL_GRAVITY_MULTIPLIER = 2f;
    private static final float SHADOW_MIN_SCALE = 0.5f; // how small the shadow gets at the peak

    public Player(float x, float y, float z, Viewport gameViewport, Texture idleSheet, Texture walkSheet, Texture runSheet) {
        super(x, y, z, 12 * SCALE, 27 * SCALE, idleSheet);
        this.gameViewport = gameViewport;
        animationController = new PlayerAnimationController(0.1f);
        Animation<TextureRegion>[] idleAnimations = animationController.makeDirectionalAnimations( idleSheet, 64, 64 );
        Animation<TextureRegion>[] walkAnimations = animationController.makeDirectionalAnimations( walkSheet, 64, 64 );
        Animation<TextureRegion>[] runAnimations = animationController.makeDirectionalAnimations( runSheet, 64, 64 );

        setGravity(JUMP_GRAVITY);

        String[] idleStates = { "idle_nw", "idle_w", "idle_sw", "idle_s", "idle_se", "idle_e", "idle_ne", "idle_n" };
        String[] walkStates = { "walk_nw", "walk_w", "walk_sw", "walk_s", "walk_se", "walk_e", "walk_ne", "walk_n" };
        String[] runStates = { "run_nw", "run_w", "run_sw", "run_s", "run_se", "run_e", "run_ne", "run_n" };

        animationController.loadAnimations(idleStates, idleAnimations);
        animationController.loadAnimations(walkStates, walkAnimations);
        animationController.loadAnimations(runStates, runAnimations);
        animationController.changeState("idle_s", true );
    }

    @Override
    public void update(float deltaTime) {
        animationController.update(deltaTime);
        move(deltaTime);
        jump(deltaTime);
        updateAnimation();
    }

    @Override
    public void draw(Batch batch) {
        TextureRegion currentFrame = animationController.getCurrentFrame();
        if (currentFrame == null) { return; }
        batch.draw(
            currentFrame,
            rect.x + DRAW_OFFSET_X,
            rect.y + DRAW_OFFSET_Y + z,
            FRAME_SIZE,
            FRAME_SIZE
        );
    }

    private void move(float deltaTime) {
        if (moveDirection.isZero()) return;
        float speed = isSprint ? SprintSpeed : SPEED;
        float newX = rect.getX() + moveDirection.x * speed * deltaTime;
        float newY = rect.getY() + moveDirection.y * speed * deltaTime;
        newX = MathUtils.clamp(newX, 0, gameViewport.getWorldWidth() - rect.getWidth());
        newY = MathUtils.clamp(newY, 0, gameViewport.getWorldHeight() - rect.getHeight());
        rect.setPosition(newX, newY);
    }

    private boolean canJump() {
        return wasOnFloor;
    }

    private void jump(float deltaTime) {
        if (isJump) {
            if (canJump()) {
                verticalVelocity = JUMP_VELOCITY;
                wasOnFloor = false;
            }
            isJump = false;
        }

        // Gravity only applies while airborne; grounded players just sit at z = 0.
        if (!wasOnFloor) {
            float g = getGravity();
            if (verticalVelocity < 0) {
                g *= FALL_GRAVITY_MULTIPLIER; // fall faster than we rose, purely for feel
            }
            verticalVelocity -= g * deltaTime;
        }

        // Terminal velocity clamp (falling speed cap)
        if (verticalVelocity < -getVT()) {
            verticalVelocity = -getVT();
        }

        z += verticalVelocity * deltaTime;

        // Landing check
        if (z <= 0f) {
            z = 0f;
            verticalVelocity = 0f;
            wasOnFloor = true;
        }
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
        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            isSprint = true;
        }
        if(!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            isSprint = false;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            isJump = true;
        }

        inputMovement.nor(); // Normalise diagonal
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
            animationController.changeState("idle_" + lastDirection, true);
        } else {
            String direction = getDirectionName();
            String prefix = isSprint ? "run_" : "walk_";
            animationController.changeState(prefix + direction, true);
        }
    }

    public Rectangle getShadowBounds() {
        // Shrink shadow as the player rises, based on how close z is to the jump's peak height.
        // Clamped so it never goes below SHADOW_MIN_SCALE or above 1.
        float heightRatio = MathUtils.clamp(z / JUMP_HEIGHT, 0f, 1f);
        float scale = MathUtils.lerp(1f, SHADOW_MIN_SCALE, heightRatio);

        float baseWidth = 18 * SCALE;
        float baseHeight = 10 * SCALE;
        float width = baseWidth * scale;
        float height = baseHeight * scale;

        float baseX = rect.x - (2 * SCALE);
        float baseY = rect.y - (7 * SCALE);
        float centerX = baseX + baseWidth / 2f;
        float centerY = baseY + baseHeight / 2f;

        return new Rectangle(
            centerX - width / 2f,
            centerY - height / 2f,
            width, height
        );
    }
}