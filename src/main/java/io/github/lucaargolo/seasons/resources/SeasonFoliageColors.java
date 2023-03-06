package io.github.lucaargolo.seasons.resources;

import com.google.gson.JsonParser;
import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.utils.SeasonColor;
import io.github.lucaargolo.seasons.utils.ModIdentifier;
import io.github.lucaargolo.seasons.utils.Season;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.util.RawTextureDataLoader;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Optional;

public class SeasonFoliageColors implements SimpleSynchronousResourceReloadListener {

    private static final Identifier SPRING_FOLIAGE_COLORMAP = new ModIdentifier("textures/colormap/spring_foliage.png");
    private static final Identifier SUMMER_FOLIAGE_COLORMAP = new ModIdentifier("textures/colormap/summer_foliage.png");
    private static final Identifier FALL_FOLIAGE_COLORMAP = new ModIdentifier("textures/colormap/fall_foliage.png");
    private static final Identifier WINTER_FOLIAGE_COLORMAP = new ModIdentifier("textures/colormap/winter_foliage.png");

    private static int[] springColorMap = new int[65536];
    private static int[] summerColorMap = new int[65536];
    private static int[] fallColorMap = new int[65536];
    private static int[] winterColorMap = new int[65536];

    private static SeasonColor minecraftDefaultFoliage = new SeasonColor(0x48B518, 0x4CE00B, 0xD2CF1E, 0xC6DFB6);
    private static SeasonColor minecraftSpruceFoliage = new SeasonColor(0x619961, 0x619961, 0x619961, 0x619961);
    private static SeasonColor minecraftBirchFoliage = new SeasonColor(0x80A755, 0x81B844, 0xD66800, 0x665026);

    private static HashMap<Identifier, SeasonColor> foliageColorMap = new HashMap<>();

    public static Optional<Integer> getSeasonFoliageColor(Biome biome, Identifier biomeIdentifier, Season season) {
        Optional<SeasonColor> colors;
        if(foliageColorMap.containsKey(biomeIdentifier)) {
            colors = Optional.of(foliageColorMap.get(biomeIdentifier));
        }else{
            colors = Optional.empty();
        }
        Optional<Integer> color = colors.map(seasonColor -> seasonColor.getColor(season));
        if(color.isEmpty() && FabricSeasons.CONFIG.isDefaultHSBShiftEnabled()) {
            Optional<Integer> defaultColor = biome.getEffects().getFoliageColor();
            if(defaultColor.isPresent()) {
                return Optional.of(FabricSeasons.CONFIG.getShiftedColor(season, defaultColor.get()));
            }
        }
        return color;
    }

    public static int getColor(Season season, double temperature, double humidity) {
        humidity *= temperature;
        int i = (int)((1.0D - temperature) * 255.0D);
        int j = (int)((1.0D - humidity) * 255.0D);
        return switch (season) {
            case SPRING -> springColorMap[j << 8 | i];
            case SUMMER -> summerColorMap[j << 8 | i];
            case FALL -> fallColorMap[j << 8 | i];
            case WINTER -> winterColorMap[j << 8 | i];
        };
    }


    public static int getSpruceColor(Season season) {
        return minecraftSpruceFoliage.getColor(season);
    }

    public static int getBirchColor(Season season) {
        return minecraftBirchFoliage.getColor(season);
    }

    public static int getDefaultColor(Season season) {
        return minecraftDefaultFoliage.getColor(season);
    }

    @Override
    public Identifier getFabricId() {
        return new ModIdentifier("season_foliage");
    }

    @SuppressWarnings("deprecation")
    @Override
    public void reload(ResourceManager manager) {
        try{
            Resource spruceFoliage = manager.getResource(new ModIdentifier("hardcoded/foliage/spruce.json")).orElseThrow();
            minecraftSpruceFoliage = new SeasonColor(JsonParser.parseReader(new InputStreamReader(spruceFoliage.getInputStream(), StandardCharsets.UTF_8)));
            Resource birchFoliage = manager.getResource(new ModIdentifier("hardcoded/foliage/birch.json")).orElseThrow();
            minecraftBirchFoliage = new SeasonColor(JsonParser.parseReader(new InputStreamReader(birchFoliage.getInputStream(), StandardCharsets.UTF_8)));
            Resource defaultFoliage = manager.getResource(new ModIdentifier("hardcoded/foliage/default.json")).orElseThrow();
            minecraftDefaultFoliage = new SeasonColor(JsonParser.parseReader(new InputStreamReader(defaultFoliage.getInputStream(), StandardCharsets.UTF_8)));
        }catch (Exception e) {
            FabricSeasons.LOGGER.error("Failed to load hardcoded foliage colors", e);
        }
        foliageColorMap.clear();
        manager.findResources("seasons/foliage", id -> id.getPath().endsWith(".json")).forEach((id, resource) -> {
            String[] split = id.getPath().split("/");
            Identifier biomeIdentifier = new Identifier(id.getNamespace(), split[split.length-1].replace(".json", ""));
            try {
                SeasonColor colors = new SeasonColor(JsonParser.parseReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)));
                foliageColorMap.put(biomeIdentifier, colors);
            }catch(Exception e) {
                FabricSeasons.LOGGER.error("Failed to load biome foliage colors for: "+biomeIdentifier, e);
            }
        });
        if(!foliageColorMap.isEmpty()) {
            FabricSeasons.LOGGER.info("Successfully loaded "+foliageColorMap.size()+" custom foliage colors.");
        }
        try {
            springColorMap = RawTextureDataLoader.loadRawTextureData(manager, SPRING_FOLIAGE_COLORMAP);
            summerColorMap = RawTextureDataLoader.loadRawTextureData(manager, SUMMER_FOLIAGE_COLORMAP);
            fallColorMap = RawTextureDataLoader.loadRawTextureData(manager, FALL_FOLIAGE_COLORMAP);
            winterColorMap = RawTextureDataLoader.loadRawTextureData(manager, WINTER_FOLIAGE_COLORMAP);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load foliage color texture", e);
        }
    }
}
