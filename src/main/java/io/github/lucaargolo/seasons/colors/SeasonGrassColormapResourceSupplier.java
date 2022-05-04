package io.github.lucaargolo.seasons.colors;

import io.github.lucaargolo.seasons.utils.ModIdentifier;
import io.github.lucaargolo.seasons.utils.Season;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.util.RawTextureDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class SeasonGrassColormapResourceSupplier implements SimpleSynchronousResourceReloadListener {

    private static final Identifier SPRING_GRASS_COLORMAP = new ModIdentifier("textures/colormap/spring_grass.png");
    private static final Identifier SUMMER_GRASS_COLORMAP = new ModIdentifier("textures/colormap/summer_grass.png");
    private static final Identifier FALL_GRASS_COLORMAP = new ModIdentifier("textures/colormap/fall_grass.png");
    private static final Identifier WINTER_GRASS_COLORMAP = new ModIdentifier("textures/colormap/winter_grass.png");

    @Override
    public Identifier getFabricId() {
        return new ModIdentifier("season_grass");
    }

    @SuppressWarnings("deprecation")
    @Override
    public void reload(ResourceManager manager) {
        try {
            SeasonGrassColors.setColorMap(Season.SPRING, RawTextureDataLoader.loadRawTextureData(manager, SPRING_GRASS_COLORMAP));
            SeasonGrassColors.setColorMap(Season.SUMMER, RawTextureDataLoader.loadRawTextureData(manager, SUMMER_GRASS_COLORMAP));
            SeasonGrassColors.setColorMap(Season.FALL, RawTextureDataLoader.loadRawTextureData(manager, FALL_GRASS_COLORMAP));
            SeasonGrassColors.setColorMap(Season.WINTER, RawTextureDataLoader.loadRawTextureData(manager, WINTER_GRASS_COLORMAP));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load foliage color texture", e);
        }
    }
}
