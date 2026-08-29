package io.github.akuderia.Gamescreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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
    private final GlyphLayout layout = new GlyphLayout();

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

        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            Gdx.app.log("Space pressed", "pressed  ");
            game.setScreen(new GameScreen(game));
            dispose();
            return;
        }

        // Clears window with specific color
        ScreenUtils.clear(Color.BLACK);
        // updates camera
        viewport.apply();
        // How things should scale, be transform, be rendered etc
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        layout.setText(font, "W / A / S / D - Move");
        font.draw(batch, layout, viewport.getWorldWidth() / 2 - layout.width / 2, viewport.getWorldHeight() / 3 * 2);
        batch.end();
    }
}