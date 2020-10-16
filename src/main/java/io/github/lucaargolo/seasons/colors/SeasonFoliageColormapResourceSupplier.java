package io.github.lucaargolo.seasons.colors;

import io.github.lucaargolo.seasons.Season;
import io.github.lucaargolo.seasons.utils.ModIdentifier;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.util.RawTextureDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class SeasonFoliageColormapResourceSupplier implements SimpleSynchronousResourceReloadListener {

    private static final Identifier SPRING_FOLIAGE_COLORMAP = new ModIdentifier("textures/colormap/spring_foliage.png");
    private static final Identifier SUMMER_FOLIAGE_COLORMAP = new ModIdentifier("textures/colormap/summer_foliage.png");
    private static final Identifier FALL_FOLIAGE_COLORMAP = new ModIdentifier("textures/colormap/fall_foliage.png");
    private static final Identifier WINTER_FOLIAGE_COLORMAP = new ModIdentifier("textures/colormap/winter_foliage.png");

    @Override
    public Identifier getFabricId() {
        return new ModIdentifier("season_foliage");
    }

    @Override
    public void apply(ResourceManager manager) {
        try {
            SeasonFoliageColors.setColorMap(Season.SPRING, RawTextureDataLoader.loadRawTextureData(manager, SPRING_FOLIAGE_COLORMAP));
            SeasonFoliageColors.setColorMap(Season.SUMMER, RawTextureDataLoader.loadRawTextureData(manager, SUMMER_FOLIAGE_COLORMAP));
            SeasonFoliageColors.setColorMap(Season.FALL, RawTextureDataLoader.loadRawTextureData(manager, FALL_FOLIAGE_COLORMAP));
            SeasonFoliageColors.setColorMap(Season.WINTER, RawTextureDataLoader.loadRawTextureData(manager, WINTER_FOLIAGE_COLORMAP));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load foliage color texture", e);
        }
    }
}
