package io.github.akuderia.Animations;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
// "characters/Player/walk/Sprite/walk.png"

public class PlayerAnimationController {
    // animation being played
    private Animation<TextureRegion> currentAnimation;
    // How long each frame lasts
    private final float animationSpeed;
    // How long the current animation has been playing
    private float animationTime=0;
    // current animation state
    public String getCurrentState() {
        return currentState;
    }

    public String getPreviousState() {
        return previousState;
    }

    // e.g state = idle/walk/
    private String currentState="null";
    private String previousState="null";

    private final HashMap<String, Animation<TextureRegion>> animationsMap = new HashMap<>();

    public PlayerAnimationController(float animationSpeed) {
        this.animationSpeed = animationSpeed;

    }

    public void update(float delta) {
        animationTime+=delta;
    }

    public TextureRegion getCurrentFrame() {
        return currentAnimation.getKeyFrame(animationTime, looping); // make sure it's not looping
    }

    private boolean looping=false;

    public void changeState(String newState, boolean looping) {
        // check if state actually changed
        if(!newState.equals(currentState)){
            previousState=currentState;
            currentState=newState;
            this.looping=looping;
            animationTime=0;
            try {
                currentAnimation = animationsMap.get(newState);
            } catch (NullPointerException e){
                Gdx.app.error("AnimationController", "Error getting current animation");
            }
        }
    }

    /// AnimationController Maker Things
    public void loadAnimations(String[] animationStates, Animation<TextureRegion>[] animations) {
        for (int i = 0; i < animationStates.length; i++) {
            animationsMap.put(animationStates[i], animations[i]);
        }
    }

    public Animation<TextureRegion> makeAnimation(Texture sheet,int width,int height) {
        sheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        TextureRegion[][] tmp = TextureRegion.split(sheet, width,height);
        List<TextureRegion> tmpFlattened = Arrays.stream(tmp).flatMap(Arrays::stream).toList();
        Array<TextureRegion> regions=new Array<>();

        for(TextureRegion region : tmpFlattened){
            region.setRegionX(region.getRegionX());
            region.setRegionY(region.getRegionY());
            regions.add(region);
        }

        return new Animation<>(animationSpeed,regions);
    }

    public Animation<TextureRegion>[] makeDirectionalAnimations(
        Texture sheet,
        int frameWidth,
        int frameHeight
    ) {
        sheet.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        TextureRegion[][] frames = TextureRegion.split(
            sheet,
            frameWidth,
            frameHeight
        );

        @SuppressWarnings("unchecked")
        Animation<TextureRegion>[] animations = new Animation[8];

        for (int direction = 0; direction < 8; direction++) {

            Array<TextureRegion> regions = new Array<>();

            for (int frame = 0; frame < 8; frame++) {
                regions.add(frames[direction][frame]);
            }

            animations[direction] = new Animation<>(
                animationSpeed,
                regions
            );
        }

        return animations;
    }
}