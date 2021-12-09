package com.yurisuika.seasons.colors;

import com.yurisuika.seasons.utils.Season;
import com.yurisuika.seasons.utils.ModIdentifier;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.util.RawTextureDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class SeasonGrassColormapResourceSupplier implements SimpleSynchronousResourceReloadListener {

    private static final Identifier EARLY_SPRING_GRASS_COLORMAP = new ModIdentifier("textures/colormap/early_spring_grass.png");
    private static final Identifier MID_SPRING_GRASS_COLORMAP = new ModIdentifier("textures/colormap/mid_spring_grass.png");
    private static final Identifier LATE_SPRING_GRASS_COLORMAP = new ModIdentifier("textures/colormap/late_spring_grass.png");
    private static final Identifier EARLY_SUMMER_GRASS_COLORMAP = new ModIdentifier("textures/colormap/early_summer_grass.png");
    private static final Identifier MID_SUMMER_GRASS_COLORMAP = new ModIdentifier("textures/colormap/mid_summer_grass.png");
    private static final Identifier LATE_SUMMER_GRASS_COLORMAP = new ModIdentifier("textures/colormap/late_summer_grass.png");
    private static final Identifier EARLY_AUTUMN_GRASS_COLORMAP = new ModIdentifier("textures/colormap/early_autumn_grass.png");
    private static final Identifier MID_AUTUMN_GRASS_COLORMAP = new ModIdentifier("textures/colormap/mid_autumn_grass.png");
    private static final Identifier LATE_AUTUMN_GRASS_COLORMAP = new ModIdentifier("textures/colormap/late_autumn_grass.png");
    private static final Identifier EARLY_WINTER_GRASS_COLORMAP = new ModIdentifier("textures/colormap/early_winter_grass.png");
    private static final Identifier MID_WINTER_GRASS_COLORMAP = new ModIdentifier("textures/colormap/mid_winter_grass.png");
    private static final Identifier LATE_WINTER_GRASS_COLORMAP = new ModIdentifier("textures/colormap/late_winter_grass.png");

    @Override
    public Identifier getFabricId() {
        return new ModIdentifier("season_grass");
    }

    @SuppressWarnings("deprecation")
    @Override
    public void reload(ResourceManager manager) {
        try {
            SeasonGrassColors.setColorMap(Season.EARLY_SPRING, RawTextureDataLoader.loadRawTextureData(manager, EARLY_SPRING_GRASS_COLORMAP));
            SeasonGrassColors.setColorMap(Season.MID_SPRING, RawTextureDataLoader.loadRawTextureData(manager, MID_SPRING_GRASS_COLORMAP));
            SeasonGrassColors.setColorMap(Season.LATE_SPRING, RawTextureDataLoader.loadRawTextureData(manager, LATE_SPRING_GRASS_COLORMAP));
            SeasonGrassColors.setColorMap(Season.EARLY_SUMMER, RawTextureDataLoader.loadRawTextureData(manager, EARLY_SUMMER_GRASS_COLORMAP));
            SeasonGrassColors.setColorMap(Season.MID_SUMMER, RawTextureDataLoader.loadRawTextureData(manager, MID_SUMMER_GRASS_COLORMAP));
            SeasonGrassColors.setColorMap(Season.LATE_SUMMER, RawTextureDataLoader.loadRawTextureData(manager, LATE_SUMMER_GRASS_COLORMAP));
            SeasonGrassColors.setColorMap(Season.EARLY_AUTUMN, RawTextureDataLoader.loadRawTextureData(manager, EARLY_AUTUMN_GRASS_COLORMAP));
            SeasonGrassColors.setColorMap(Season.MID_AUTUMN, RawTextureDataLoader.loadRawTextureData(manager, MID_AUTUMN_GRASS_COLORMAP));
            SeasonGrassColors.setColorMap(Season.LATE_AUTUMN, RawTextureDataLoader.loadRawTextureData(manager, LATE_AUTUMN_GRASS_COLORMAP));
            SeasonGrassColors.setColorMap(Season.EARLY_WINTER, RawTextureDataLoader.loadRawTextureData(manager, EARLY_WINTER_GRASS_COLORMAP));
            SeasonGrassColors.setColorMap(Season.MID_WINTER, RawTextureDataLoader.loadRawTextureData(manager, MID_WINTER_GRASS_COLORMAP));
            SeasonGrassColors.setColorMap(Season.LATE_WINTER, RawTextureDataLoader.loadRawTextureData(manager, LATE_WINTER_GRASS_COLORMAP));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load grass color texture", e);
        }
    }
}
