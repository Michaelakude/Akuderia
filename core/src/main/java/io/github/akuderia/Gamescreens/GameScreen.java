package io.github.akuderia.Gamescreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.ScreenUtils;

import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.akuderia.Entities.Player;

public class GameScreen extends ScreenAdapter {
    private static final float WORLD_WIDTH = 16f;
    private static final float WORLD_HEIGHT = 9f;
    private final Batch batch;
    private final Texture bgdTexture = new Texture(Gdx.files.internal("backgrounds/bgd.png"));
    private final Texture playerTexture = new Texture(Gdx.files.internal("characters/Player/idle/player.png"));
    private final Viewport gameViewport = new ExtendViewport(16f, 9f);
    private final Texture playerWalkTexture = new Texture( Gdx.files.internal( "characters/Player/walk/Sprite/walk.png"));
    private final Texture playerIdleTexture = new Texture( Gdx.files.internal( "characters/Player/idle/Sprite/idle.png"));
    private final Player player = new Player(WORLD_WIDTH/2f, WORLD_HEIGHT/2f, gameViewport, playerTexture, playerIdleTexture, playerWalkTexture );

    public GameScreen(GdxGame game) {
        this.batch = game.getBatch();
        bgdTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
    }
    @Override
	public void show () {
        super.show();
	}


    @Override
    public void render(float deltaTime) {
        player.processInput();
        player.updateLogic(deltaTime);
        ScreenUtils.clear(Color.GREEN);
        gameViewport.apply();
        batch.setProjectionMatrix(gameViewport.getCamera().combined);
        batch.begin();
        drawBackground();
        player.draw(batch);
        batch.end();
    }
    private void drawBackground() {
        // Calculate how many times the texture fits into the world dimensions
        float u2 = gameViewport.getWorldWidth() / WORLD_WIDTH;
        float v2 = gameViewport.getWorldHeight() / WORLD_HEIGHT;

        // Draw with custom UV coordinates to correctly repeat the texture
        batch.draw(bgdTexture,
            0, 0,
            gameViewport.getWorldWidth(),
            gameViewport.getWorldHeight(),
            0, 0,
            u2, v2
        );
    }

    @Override
    public void dispose() {
        bgdTexture.dispose();
        playerTexture.dispose();
    }
}
