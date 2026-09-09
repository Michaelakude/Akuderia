package io.github.akuderia.WorldGen;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

import io.github.akuderia.WorldGen.CreateWorld.TerrainType;

public class WorldTileGenerator {

    // private final CreateWorld world = new CreateWorld(this.generateRandomSeed());
    private final CreateWorld world;
    private Long seed;
    private CreateWorld.TerrainType[][] map;
    private TiledMapTileLayer tiledMapLayer;
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
    private TiledMap tiledMap;
    private Texture grasssTexture = new Texture(Gdx.files.internal("backgrounds/Terrain/Tilemap_color1.png"));
    private TextureRegion grassRegion = new TextureRegion(grasssTexture, 90, 90, 16, 16);
    private StaticTiledMapTile grassTile = new StaticTiledMapTile(grassRegion);
    private float SCALE = 1/32f;

    public WorldTileGenerator(Long seed) {
        this.seed = seed;
        this.world = new CreateWorld(seed);
        this.world.create();
        this.map = this.world.getTerrainMap();
        this.tiledMapLayer = new TiledMapTileLayer(this.world.worldSize, this.world.worldSize, 16, 16);
        this.tiledMap = new TiledMap();
    }

    private TiledMapTileLayer generateTileMap() {
        for (int x = 0; x < tiledMapLayer.getWidth(); x++) {
            for (int y = 0; y < tiledMapLayer.getHeight(); y++) {
                TerrainType m = map[x][y]; // returns e.g water

                // Call fn takes in m and returns the tile associated ()
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(getTile(m));
                tiledMapLayer.setCell(x, y, cell);
                // Plot this tile's color into the debug image.

            }
        }

        return tiledMapLayer;
    }

    public OrthogonalTiledMapRenderer generate() {
        this.generateTileMap();
        tiledMap.getLayers().add(tiledMapLayer);
        orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, SCALE);
        return orthogonalTiledMapRenderer;
    }

    private TiledMapTile getTile(TerrainType terrain) {
        switch (terrain) {

            // case WATER:
            //     return Color.BLUE;

            case GRASS:
                return grassTile;

            // case HILL:
            //     return grassTile;

            // case MOUNTAIN:
            //     return Color.GRAY;

            // case PEAK:
            //     return Color.WHITE;
            default:
                return grassTile;
        }
    }

    private void generateRandomSeed() {
        if (seed != null) {
            Random gen = new Random();
            seed = gen.nextLong();
        }
    }
}
