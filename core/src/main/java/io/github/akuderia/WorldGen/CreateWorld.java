package io.github.akuderia.WorldGen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.github.czyzby.noise4j.map.Grid;
import com.github.czyzby.noise4j.map.generator.noise.NoiseGenerator;

public class CreateWorld {

    public enum TerrainType {
        WATER,
        GRASS,
        HILL,
        MOUNTAIN,
        PEAK
    }

    private final int worldSize;

    // The base seed for this world. Every noise octave derives its own seed from this,
    // so the same seed value always regenerates the exact same terrain.
    private final long seed;

    // Raw noise output, one float per cell (typically in the 0..1 range).
    private Grid heightMap;

    // Classified terrain type per cell, derived from heightMap.
    private TerrainType[][] terrainMap;

    // Debug-only visual: a flat-colored image of the generated terrain,
    private Texture worldTexture;

    public CreateWorld(long seed) {
        this.worldSize = 4096;
        this.seed = seed;
    }
    public int getWorldMid() {
        return worldSize/2;
    }
    // Runs the full generation pipeline: noise -> classify -> build debug texture.
    public void create() {
        heightMap = new Grid(worldSize);
        terrainMap = new TerrainType[worldSize][worldSize];

        NoiseGenerator generator = new NoiseGenerator();
        // radius = how big, mod = how mucg layer affects whole map
        noiseStage(heightMap, generator, 16, 0.6f, seed);
        noiseStage(heightMap, generator, 16, 0.2f, seed + 1);
        noiseStage(heightMap, generator, 8, 0.1f, seed + 2);
        noiseStage(heightMap, generator, 4, 0.1f, seed + 3);
        noiseStage(heightMap, generator, 1, 0.05f, seed + 4);
        normalize(heightMap);

        // Build a debug image: one pixel per tile, colored by terrain type.
        Pixmap pixmap = new Pixmap(worldSize, worldSize, Format.RGBA8888);

        for (int x = 0; x < worldSize; x++) {
            for (int y = 0; y < worldSize; y++) {

                // Read the raw noise value for this cell
                float height = heightMap.get(x, y);

                // ...and quantize it into a discrete terrain band.
                TerrainType terrain = getTerrain(height);
                terrainMap[x][y] = terrain;

                // Plot this tile's color into the debug image.
                pixmap.drawPixel(x, y, Color.rgba8888(getColor(terrain)));
            }
        }

        // Upload the finished pixmap to the GPU as a texture, then free the CPU-side pixmap
        worldTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    private static void noiseStage(
            Grid grid,
            NoiseGenerator noiseGenerator,
            int radius,
            float modifier,
            long stageSeed) {

        noiseGenerator.setRadius(radius);     // how "wide" this octave's features are
        noiseGenerator.setModifier(modifier); // how much this octave contributes to the final value
        noiseGenerator.setSeed((int) stageSeed);    // deterministic seed for this specific octave
        noiseGenerator.generate(grid);
    }

    // Converts a raw noise float (roughly 0..1) into a discrete terrain band.
    // These thresholds are the "sea level," "hill line," etc. of your world —
    // tune these numbers to change how much of the map is water/mountains/etc.
    private TerrainType getTerrain(float value) {

        if (value < 0.30f)
            return TerrainType.WATER;

        if (value < 0.60f)
            return TerrainType.GRASS;

        if (value < 0.80f)
            return TerrainType.HILL;

        if (value < 0.90f)
            return TerrainType.MOUNTAIN;

        return TerrainType.PEAK;
    }


    private Color getColor(TerrainType terrain) {
        switch (terrain) {

            case WATER:
                return Color.BLUE;

            case GRASS:
                return Color.GREEN;

            case HILL:
                return new Color(0.5f, 0.35f, 0.15f, 1f); // brown

            case MOUNTAIN:
                return Color.GRAY;

            case PEAK:
                return Color.WHITE;

            default:
                // Should never happen — magenta makes any missed case obvious/visible
                return Color.MAGENTA;
        }
    }

    public Grid getHeightMap() {
        return heightMap;
    }

    public TerrainType[][] getTerrainMap() {
        return terrainMap;
    }

    public float getHeight(int x, int y) {
        return heightMap.get(x, y);
    }

    public TerrainType getTerrainType(int x, int y) {
        return terrainMap[x][y];
    }

    public int getElevationLevel(int x, int y) {
        return getTerrainType(x, y).ordinal();
    }

    public Texture getWorldTexture() {
        return worldTexture;
    }

    private void normalize(Grid grid) {
        float min = Float.MAX_VALUE;
        float max = -Float.MAX_VALUE;

        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                float value = grid.get(x, y);
                if (value < min) min = value;
                if (value > max) max = value;
            }
        }

        float range = max - min;
        if (range <= 0f) return; // avoid divide-by-zero on a flat grid

        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                float value = grid.get(x, y);
                grid.set(x, y, (value - min) / range);
            }
        }
    }

    public void dispose() {
        if (worldTexture != null) {
            worldTexture.dispose();
        }
    }
}