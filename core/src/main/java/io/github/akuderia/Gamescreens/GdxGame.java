package io.github.akuderia.Gamescreens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;


public class GdxGame extends Game {

    // Batch responsible for drawing from windows to game window / drawing instructionms
    private Batch batch;
    // Renderin text
    private BitmapFont font;
    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter fontParams;

    @Override
    public void create() {
        batch = new SpriteBatch();
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/CherryCreamSoda-Regular.ttf"));
        fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParams.size = 24;
        fontParams.color = Color.RED;
        font = generator.generateFont(fontParams);
        generator.dispose();

        setScreen(new ControlScreen(this));
    }
    // if class implements disposable interface we should dispose
    @Override
    public void dispose() {
        super.dispose(); // active screen calls dispose
        batch.dispose();
        font.dispose();
    }

    public Batch getBatch() {
        return batch;
    };

    public BitmapFont getFont() {
        return font;
    };
}
