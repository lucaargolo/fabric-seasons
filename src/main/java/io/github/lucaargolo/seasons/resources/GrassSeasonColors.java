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

import static io.github.lucaargolo.seasons.FabricSeasons.MOD_NAME;

public class GrassSeasonColors implements SimpleSynchronousResourceReloadListener {

    private static final Identifier SPRING_GRASS_COLORMAP = new ModIdentifier("textures/colormap/spring_grass.png");
    private static final Identifier SUMMER_GRASS_COLORMAP = new ModIdentifier("textures/colormap/summer_grass.png");
    private static final Identifier FALL_GRASS_COLORMAP = new ModIdentifier("textures/colormap/fall_grass.png");
    private static final Identifier WINTER_GRASS_COLORMAP = new ModIdentifier("textures/colormap/winter_grass.png");

    private static int[] springColorMap = new int[65536];
    private static int[] summerColorMap = new int[65536];
    private static int[] fallColorMap = new int[65536];
    private static int[] winterColorMap = new int[65536];

    private static SeasonColor minecraftSwampGrass1 = new SeasonColor(0x4C763C, 0x4C763C, 0x4C763C, 0x4C763C);
    private static SeasonColor minecraftSwampGrass2 = new SeasonColor(0x6A7039, 0x6A7039, 0x6A7039, 0x6A7039);

    private static final HashMap<Identifier, SeasonColor> grassColorMap = new HashMap<>();

    public static Optional<Integer> getSeasonGrassColor(Biome biome, Identifier biomeIdentifier, Season season) {
        Optional<SeasonColor> colors;
        if(grassColorMap.containsKey(biomeIdentifier)) {
            colors = Optional.of(grassColorMap.get(biomeIdentifier));
        }else{
            colors = Optional.empty();
        }
        Optional<Integer> color = colors.map(seasonColor -> seasonColor.getColor(season));
        if(color.isEmpty() && FabricSeasons.CONFIG.isDefaultHSBShiftEnabled()) {
            Optional<Integer> defaultColor = biome.getEffects().getGrassColor();
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
        int k = j << 8 | i;
        return switch (season) {
            case SPRING -> k > springColorMap.length ? -65281 : springColorMap[k];
            case SUMMER -> k > summerColorMap.length ? -65281 : summerColorMap[k];
            case FALL -> k > fallColorMap.length ? -65281 : fallColorMap[k];
            case WINTER -> k > winterColorMap.length ? -65281 : winterColorMap[k];
        };
    }

    public static int getSwampColor1(Season season) {
        return minecraftSwampGrass1.getColor(season);
    }

    public static int getSwampColor2(Season season) {
        return minecraftSwampGrass2.getColor(season);
    }

    @Override
    public Identifier getFabricId() {
        return new ModIdentifier("grass_season_colors");
    }

    @SuppressWarnings("deprecation")
    @Override
    public void reload(ResourceManager manager) {
        try{
            Resource swampGrass1 = manager.getResource(new ModIdentifier("hardcoded/grass/swamp1.json")).orElseThrow();
            minecraftSwampGrass1 = new SeasonColor(JsonParser.parseReader(new InputStreamReader(swampGrass1.getInputStream(), StandardCharsets.UTF_8)));
            Resource swampGrass2 = manager.getResource(new ModIdentifier("hardcoded/grass/swamp2.json")).orElseThrow();
            minecraftSwampGrass2 = new SeasonColor(JsonParser.parseReader(new InputStreamReader(swampGrass2.getInputStream(), StandardCharsets.UTF_8)));
        }catch (Exception e) {
            FabricSeasons.LOGGER.error("["+MOD_NAME+"] Failed to load hardcoded grass colors", e);
        }
        grassColorMap.clear();
        manager.findResources("seasons/grass", id -> id.getPath().endsWith(".json")).forEach((id, resource) -> {
            String[] split = id.getPath().split("/");
            Identifier biomeIdentifier = new Identifier(id.getNamespace(), split[split.length-1].replace(".json", ""));
            try {
                SeasonColor colors = new SeasonColor(JsonParser.parseReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)));
                grassColorMap.put(biomeIdentifier, colors);
            }catch(Exception e) {
                FabricSeasons.LOGGER.error("["+MOD_NAME+"] Failed to load biome grass colors for: "+biomeIdentifier, e);
            }
        });
        if(!grassColorMap.isEmpty()) {
            FabricSeasons.LOGGER.info("["+MOD_NAME+"] Successfully loaded "+grassColorMap.size()+" custom grass colors.");
        }
        try {
            springColorMap = RawTextureDataLoader.loadRawTextureData(manager, SPRING_GRASS_COLORMAP);
            summerColorMap = RawTextureDataLoader.loadRawTextureData(manager, SUMMER_GRASS_COLORMAP);
            fallColorMap = RawTextureDataLoader.loadRawTextureData(manager, FALL_GRASS_COLORMAP);
            winterColorMap = RawTextureDataLoader.loadRawTextureData(manager, WINTER_GRASS_COLORMAP);
        } catch (IOException e) {
            FabricSeasons.LOGGER.error("["+MOD_NAME+"] Failed to load foliage color texture", e);
        }
    }
}
