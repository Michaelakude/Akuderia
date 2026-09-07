package io.github.akuderia.Gamescreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.akuderia.Entities.Player;
import io.github.akuderia.WorldGen.CreateWorld;

import com.badlogic.gdx.graphics.GL20;

public class GameScreen extends ScreenAdapter {
    private static final float SCALE = 1 / 32f;
    private static final float WORLD_WIDTH = 16f;
    private static final float WORLD_HEIGHT = 9f;
    private final Batch batch;
    private final Texture bgdTexture = new Texture(Gdx.files.internal("backgrounds/bgd.png"));
    private Viewport gameViewport = new ExtendViewport(16f, 9f);
    private final Texture playerWalkTexture = new Texture( Gdx.files.internal( "characters/Player/walk/Sprite/walk.png"));
    private final Texture playerIdleTexture = new Texture( Gdx.files.internal( "characters/Player/idle/Sprite/idle.png"));
    private final Texture playerRunTexture = new Texture( Gdx.files.internal( "characters/Player/run/Sprite/run.png"));
    private final Player player = new Player(WORLD_WIDTH/2f, WORLD_HEIGHT/2f, 0, gameViewport, playerIdleTexture, playerWalkTexture, playerRunTexture);
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private ShapeRenderer shadow = new ShapeRenderer();
    private final CreateWorld world = new CreateWorld(12345L);

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
        world.create();
	}

    public void updateCamera(Player player) {
        gameViewport.getCamera().position.set(player.getRect().x, player.getRect().y, 0);
        gameViewport.getCamera().update();
    }

    @Override
    public void render(float deltaTime) {
        player.processInput();
        player.updateLogic(deltaTime);
        ScreenUtils.clear(Color.GREEN);
        gameViewport.apply();
        this.updateCamera(player);

        batch.setProjectionMatrix(gameViewport.getCamera().combined);
        batch.begin();
        drawWorld();
        batch.end();

        drawShadow();
        batch.begin();
        player.draw(batch);
        batch.end();
        drawDebugBounds();
    }

    private void drawWorld() {
        batch.draw(world.getWorldTexture(),
            0, 0,
            gameViewport.getWorldWidth() + 30,
            gameViewport.getWorldHeight() + 30
        );
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

    private void drawDebugBounds() {
        shapeRenderer.setProjectionMatrix(gameViewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // full sprite rect in yellow
        shapeRenderer.setColor(Color.YELLOW);
        Rectangle rect = player.getRect();
        shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        shapeRenderer.end();
    }

    private void drawShadow() {
        shadow.setProjectionMatrix(gameViewport.getCamera().combined);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shadow.begin(ShapeRenderer.ShapeType.Filled);
        shadow.setColor(0f, 0f, 0f, 0.35f); // semi-transparent black
        Rectangle s = player.getShadowBounds();
        shadow.ellipse(s.x, s.y, s.width, s.height, 32);
        shadow.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    public void dispose() {
        bgdTexture.dispose();
        playerIdleTexture.dispose();
        playerWalkTexture.dispose();
        shapeRenderer.dispose();
        shadow.dispose();
    }
}
