package io.github.akuderia.Animations;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * Manages sprite animations for a player/NPC/MOB entity.
 * Handles switching between animation states (e.g. idle, walk),
 * tracking playback time, and building Animation objects from sprite sheets.
 */
public class PlayerAnimationController {
    // The animation currently being played
    private Animation<TextureRegion> currentAnimation;
    // How long each frame lasts, in seconds
    private final float animationSpeed;
    // How long the current animation has been playing, used to determine which frame to show
    private float animationTime=0;

    // Getter for the current animation state name (e.g. "idle", "walk")
    public String getCurrentState() {
        return currentState;
    }

    // Getter for the previous animation state name, useful for transition logic
    public String getPreviousState() {
        return previousState;
    }

    // e.g state = idle/walk/
    // Name of the state currently playing
    private String currentState="null";
    // Name of the state that was playing before the last change
    private String previousState="null";

    // Maps a state name (e.g. "idle") to its corresponding Animation object
    private final HashMap<String, Animation<TextureRegion>> animationsMap = new HashMap<>();

    // Constructor: sets the frame duration used when building animations
    public PlayerAnimationController(float animationSpeed) {
        this.animationSpeed = animationSpeed;

    }

    // Call every frame to advance the animation timer by the elapsed time (delta)
    public void update(float delta) {
        animationTime+=delta;
    }

    // Returns the frame that should currently be displayed,
    // based on elapsed animation time and whether the animation should loop
    public TextureRegion getCurrentFrame() {
        return currentAnimation.getKeyFrame(animationTime, looping); // make sure it's not looping
    }

    // Whether the current animation should loop back to the start when it finishes
    private boolean looping=false;

    // Switches to a new animation state (e.g. from "idle" to "walk").
    // Resets the animation timer and updates the currently playing animation.
    public void changeState(String newState, boolean looping) {
        // check if state actually changed
        if(!newState.equals(currentState)){
            // Remember the old state before overwriting it
            previousState=currentState;
            currentState=newState;
            this.looping=looping;
            // Restart playback from the beginning of the new animation
            animationTime=0;
            try {
                // Look up the animation for the new state
                currentAnimation = animationsMap.get(newState);
            } catch (NullPointerException e){
                // Log an error if something goes wrong retrieving the animation
                Gdx.app.error("AnimationController", "Error getting current animation");
            }
        }
    }

    /// AnimationController Maker Things
    // Registers a set of animations under their corresponding state names,
    // so they can later be looked up by changeState()
    public void loadAnimations(String[] animationStates, Animation<TextureRegion>[] animations) {
        for (int i = 0; i < animationStates.length; i++) {
            animationsMap.put(animationStates[i], animations[i]);
        }
    }

    // Builds a single Animation from a sprite sheet by slicing it into
    // width x height frames and combining them into one animation sequence
    public Animation<TextureRegion> makeAnimation(Texture sheet, int width, int height) {
        // Use nearest-neighbor filtering to keep pixel art crisp (no blurring)
        sheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        // Split the sheet into a 2D grid of TextureRegions (rows x columns of frames)
        TextureRegion[][] tmp = TextureRegion.split(sheet, width,height);
        // Flatten the 2D grid into a single ordered list of frames
        List<TextureRegion> tmpFlattened = Arrays.stream(tmp).flatMap(Arrays::stream).toList();
        Array<TextureRegion> regions=new Array<>();

        for(TextureRegion region : tmpFlattened){
            // (No-op: reassigns each region's X/Y to their existing values)
            region.setRegionX(region.getRegionX());
            region.setRegionY(region.getRegionY());
            regions.add(region);
        }

        // Combine all frames into one Animation, played at animationSpeed per frame
        return new Animation<>(animationSpeed,regions);
    }

    public Animation<TextureRegion>[] makeDirectionalAnimations(
        Texture sheet,
        int frameWidth,
        int frameHeight
    ) {
        sheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        TextureRegion[][] frames = TextureRegion.split(sheet, frameWidth, frameHeight);

        int directions = frames.length;        // rows, derived from the actual sheet
        @SuppressWarnings("unchecked")
        Animation<TextureRegion>[] animations = new Animation[directions];

        for (int direction = 0; direction < directions; direction++) {
            int frameCount = frames[direction].length; // columns, derived from the actual sheet
            Array<TextureRegion> regions = new Array<>();
            for (int frame = 0; frame < frameCount; frame++) {
                regions.add(frames[direction][frame]);
            }
            animations[direction] = new Animation<>(animationSpeed, regions);
        }

        return animations;
    }
}