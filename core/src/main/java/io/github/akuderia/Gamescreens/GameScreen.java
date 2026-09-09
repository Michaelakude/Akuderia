package io.github.akuderia.Gamescreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.akuderia.Entities.Player;
import io.github.akuderia.WorldGen.WorldTileGenerator;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class GameScreen extends ScreenAdapter {
    private static final float WORLD_WIDTH = 16f;
    private static final float WORLD_HEIGHT = 9f;
    private final Batch batch;
    private Viewport gameViewport = new ExtendViewport(16f, 9f);
    private final Texture playerWalkTexture = new Texture( Gdx.files.internal( "characters/Player/walk/Sprite/walk.png"));
    private final Texture playerIdleTexture = new Texture( Gdx.files.internal( "characters/Player/idle/Sprite/idle.png"));
    private final Texture playerRunTexture = new Texture( Gdx.files.internal( "characters/Player/run/Sprite/run.png"));
    private final Player player = new Player(WORLD_WIDTH/2f, WORLD_HEIGHT/2f, 0, gameViewport, playerIdleTexture, playerWalkTexture, playerRunTexture);
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private ShapeRenderer shadow = new ShapeRenderer();
    private final WorldTileGenerator world = new WorldTileGenerator(12345L);
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;

    public GameScreen(GdxGame game) {
        this.batch = game.getBatch();
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
    }

    @Override
	public void show () {
        super.show();
        orthogonalTiledMapRenderer = world.generate();
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
        orthogonalTiledMapRenderer.setView((OrthographicCamera) gameViewport.getCamera());
        orthogonalTiledMapRenderer.render();

        drawShadow();
        batch.begin();
        player.draw(batch);
        batch.end();
        drawDebugBounds();
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
        playerIdleTexture.dispose();
        playerWalkTexture.dispose();
        playerRunTexture.dispose();
        shapeRenderer.dispose();
        shadow.dispose();
    }
}
