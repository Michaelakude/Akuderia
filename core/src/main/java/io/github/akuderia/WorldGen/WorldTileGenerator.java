package io.github.akuderia.WorldGen;

import java.util.Random;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;

public class WorldRenderer {

    // private final CreateWorld world = new CreateWorld(this.generateRandomSeed());
    private final CreateWorld world;
    private TiledMap renderedWorld;
    private Long seed;
    private Viewport gameViewport;
    private OrthogonalTiledMapRenderer renderer;

    public WorldRenderer(Long seed, Viewport gameViewport) {
        this.seed = seed;
        this.world = new CreateWorld(seed);
        this.renderedWorld = new TiledMap();
    }

    private void generateRandomSeed() {
        if (seed != null) {
            Random gen = new Random();
            seed = gen.nextLong();
        }
    }
}
