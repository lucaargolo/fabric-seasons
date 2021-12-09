package com.yurisuika.seasons.colors;

import com.yurisuika.seasons.utils.Season;
import com.yurisuika.seasons.utils.ModIdentifier;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.util.RawTextureDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class SeasonFoliageColormapResourceSupplier implements SimpleSynchronousResourceReloadListener {

    private static final Identifier EARLY_SPRING_FOLIAGE_COLORMAP = new ModIdentifier("textures/colormap/early_spring_foliage.png");
    private static final Identifier MID_SPRING_FOLIAGE_COLORMAP = new ModIdentifier("textures/colormap/mid_spring_foliage.png");
    private static final Identifier LATE_SPRING_FOLIAGE_COLORMAP = new ModIdentifier("textures/colormap/late_spring_foliage.png");
    private static final Identifier EARLY_SUMMER_FOLIAGE_COLORMAP = new ModIdentifier("textures/colormap/early_summer_foliage.png");
    private static final Identifier MID_SUMMER_FOLIAGE_COLORMAP = new ModIdentifier("textures/colormap/mid_summer_foliage.png");
    private static final Identifier LATE_SUMMER_FOLIAGE_COLORMAP = new ModIdentifier("textures/colormap/late_summer_foliage.png");
    private static final Identifier EARLY_AUTUMN_FOLIAGE_COLORMAP = new ModIdentifier("textures/colormap/early_autumn_foliage.png");
    private static final Identifier MID_AUTUMN_FOLIAGE_COLORMAP = new ModIdentifier("textures/colormap/mid_autumn_foliage.png");
    private static final Identifier LATE_AUTUMN_FOLIAGE_COLORMAP = new ModIdentifier("textures/colormap/late_autumn_foliage.png");
    private static final Identifier EARLY_WINTER_FOLIAGE_COLORMAP = new ModIdentifier("textures/colormap/early_winter_foliage.png");
    private static final Identifier MID_WINTER_FOLIAGE_COLORMAP = new ModIdentifier("textures/colormap/mid_winter_foliage.png");
    private static final Identifier LATE_WINTER_FOLIAGE_COLORMAP = new ModIdentifier("textures/colormap/late_winter_foliage.png");

    @Override
    public Identifier getFabricId() {
        return new ModIdentifier("season_foliage");
    }

    @SuppressWarnings("deprecation")
    @Override
    public void reload(ResourceManager manager) {
        try {
            SeasonFoliageColors.setColorMap(Season.EARLY_SPRING, RawTextureDataLoader.loadRawTextureData(manager, EARLY_SPRING_FOLIAGE_COLORMAP));
            SeasonFoliageColors.setColorMap(Season.MID_SPRING, RawTextureDataLoader.loadRawTextureData(manager, MID_SPRING_FOLIAGE_COLORMAP));
            SeasonFoliageColors.setColorMap(Season.LATE_SPRING, RawTextureDataLoader.loadRawTextureData(manager, LATE_SPRING_FOLIAGE_COLORMAP));
            SeasonFoliageColors.setColorMap(Season.EARLY_SUMMER, RawTextureDataLoader.loadRawTextureData(manager, EARLY_SUMMER_FOLIAGE_COLORMAP));
            SeasonFoliageColors.setColorMap(Season.MID_SUMMER, RawTextureDataLoader.loadRawTextureData(manager, MID_SUMMER_FOLIAGE_COLORMAP));
            SeasonFoliageColors.setColorMap(Season.LATE_SUMMER, RawTextureDataLoader.loadRawTextureData(manager, LATE_SUMMER_FOLIAGE_COLORMAP));
            SeasonFoliageColors.setColorMap(Season.EARLY_AUTUMN, RawTextureDataLoader.loadRawTextureData(manager, EARLY_AUTUMN_FOLIAGE_COLORMAP));
            SeasonFoliageColors.setColorMap(Season.MID_AUTUMN, RawTextureDataLoader.loadRawTextureData(manager, MID_AUTUMN_FOLIAGE_COLORMAP));
            SeasonFoliageColors.setColorMap(Season.LATE_AUTUMN, RawTextureDataLoader.loadRawTextureData(manager, LATE_AUTUMN_FOLIAGE_COLORMAP));
            SeasonFoliageColors.setColorMap(Season.EARLY_WINTER, RawTextureDataLoader.loadRawTextureData(manager, EARLY_WINTER_FOLIAGE_COLORMAP));
            SeasonFoliageColors.setColorMap(Season.MID_WINTER, RawTextureDataLoader.loadRawTextureData(manager, MID_WINTER_FOLIAGE_COLORMAP));
            SeasonFoliageColors.setColorMap(Season.LATE_WINTER, RawTextureDataLoader.loadRawTextureData(manager, LATE_WINTER_FOLIAGE_COLORMAP));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load foliage color texture", e);
        }
    }
}
