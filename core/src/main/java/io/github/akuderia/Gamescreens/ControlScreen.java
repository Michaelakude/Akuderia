package io.github.akuderia.Gamescreens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ControlScreen extends ScreenAdapter {
    private final GdxGame game;
    private final Batch batch;
    private final BitmapFont font;
    // visible area of window
    // screeen viewport covers screen
    private final Viewport viewport = new ScreenViewport();

    public ControlScreen(GdxGame game) {
        this.game = game;
        this.batch = game.getBatch();
        this.font = game.getFont();
    }
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
    @Override
    public void render(float delta) {
        // Clears window with specific color
        ScreenUtils.clear(Color.BLACK);
        // updates camera
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        font.draw(batch, "Welcome to Akuderia", 100, 100);

        batch.end();
    }
    public GdxGame getGame() {
        return game;
    }
}