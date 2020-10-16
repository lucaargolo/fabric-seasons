package io.github.lucaargolo.seasons;

import io.github.lucaargolo.seasons.block.SeasonDetectorBlock;
import io.github.lucaargolo.seasons.colors.SeasonFoliageColormapResourceSupplier;
import io.github.lucaargolo.seasons.colors.SeasonGrassColormapResourceSupplier;
import io.github.lucaargolo.seasons.item.SeasonCalendarItem;
import io.github.lucaargolo.seasons.mixin.WeatherMixin;
import io.github.lucaargolo.seasons.utils.ModIdentifier;
import io.github.lucaargolo.seasons.utils.WeatherCache;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Arrays;
import java.util.List;

public class FabricSeasons implements ModInitializer {

    public static final String MOD_ID = "seasons";
    public static final long SEASON_LENGTH = 672000L;

    public static final SeasonDetectorBlock SEASON_DETECTOR = Registry.register(Registry.BLOCK, new ModIdentifier("season_detector"), new SeasonDetectorBlock(FabricBlockSettings.copyOf(Blocks.DAYLIGHT_DETECTOR)));
    public static final BlockEntityType<BlockEntity> SEASON_DETECTOR_ENTITY = BlockEntityType.Builder.create(() -> SEASON_DETECTOR.createBlockEntity(null), SEASON_DETECTOR).build(null);
    public static final BlockItem SEASON_DETECTOR_ITEM = Registry.register(Registry.ITEM, new ModIdentifier("season_detector"), new BlockItem(SEASON_DETECTOR, new Item.Settings().group(ItemGroup.REDSTONE)));
    public static final SeasonCalendarItem SEASON_CALENDAR = Registry.register(Registry.ITEM, new ModIdentifier("season_calendar"), new SeasonCalendarItem((new Item.Settings()).group(ItemGroup.TOOLS)));

    @Override
    public void onInitialize() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SeasonGrassColormapResourceSupplier());
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SeasonFoliageColormapResourceSupplier());
    }

    public static Season getCurrentSeason(World world) {
        int seasonTime = (int) (world.getTimeOfDay()/ SEASON_LENGTH);
        return Season.values()[seasonTime % 4];
    }

    @Environment(EnvType.CLIENT)
    public static Season getCurrentSeason() {
        World world = MinecraftClient.getInstance().world;
        long worldTime = (world != null) ? world.getTimeOfDay() : 0;
        int seasonTime = (int) (worldTime / SEASON_LENGTH);
        return Season.values()[seasonTime % 4];
    }

    public static void injectBiomeSeason(Biome biome, World world) {

        List<Biome.Category> ignoredCategories = Arrays.asList(Biome.Category.NONE, Biome.Category.NETHER, Biome.Category.THEEND, Biome.Category.OCEAN);
        if(ignoredCategories.contains(biome.getCategory())) return;

        Season season = FabricSeasons.getCurrentSeason(world);

        Identifier biomeIdentifier = world.getRegistryManager().get(Registry.BIOME_KEY).getId(biome);
        Biome.Weather currentWeather = biome.weather;

        Biome.Weather originalWeather;
        if (!WeatherCache.hasCache(biomeIdentifier)) {
            originalWeather = new Biome.Weather(currentWeather.precipitation, currentWeather.temperature, currentWeather.temperatureModifier, currentWeather.downfall);
            WeatherCache.setCache(biomeIdentifier, originalWeather);
        } else {
            originalWeather = WeatherCache.getCache(biomeIdentifier);
        }

        float temp = originalWeather.temperature;
        if(biome.getCategory() == Biome.Category.JUNGLE || biome.getCategory() == Biome.Category.SWAMP) {
            //Jungle Biomes
            if (season == Season.WINTER) {
                ((WeatherMixin) currentWeather).setPrecipitation(originalWeather.precipitation);
                ((WeatherMixin) currentWeather).setTemperature(temp-0.1f);
            } else {
                ((WeatherMixin) currentWeather).setPrecipitation(originalWeather.precipitation);
                ((WeatherMixin) currentWeather).setTemperature(temp);
            }
        }else if(temp <= 0.1) {
            //Frozen Biomes
            switch (season) {
                case SUMMER:
                    ((WeatherMixin) currentWeather).setPrecipitation(Biome.Precipitation.RAIN);
                    ((WeatherMixin) currentWeather).setTemperature(temp+0.3f);
                    break;
                case WINTER:
                    ((WeatherMixin) currentWeather).setPrecipitation(Biome.Precipitation.SNOW);
                    ((WeatherMixin) currentWeather).setTemperature(temp-0.2f);
                    break;
                default:
                    ((WeatherMixin) currentWeather).setPrecipitation(originalWeather.precipitation);
                    ((WeatherMixin) currentWeather).setTemperature(temp);
            }
        }else if(temp <= 0.3) {
            //Cold Biomes
            switch (season) {
                case SPRING:
                    ((WeatherMixin) currentWeather).setPrecipitation(Biome.Precipitation.RAIN);
                    ((WeatherMixin) currentWeather).setTemperature(temp);
                    break;
                case SUMMER:
                    ((WeatherMixin) currentWeather).setPrecipitation(Biome.Precipitation.RAIN);
                    ((WeatherMixin) currentWeather).setTemperature(temp+0.2f);
                    break;
                case WINTER:
                    ((WeatherMixin) currentWeather).setPrecipitation(Biome.Precipitation.SNOW);
                    ((WeatherMixin) currentWeather).setTemperature(temp-0.2f);
                    break;
                default:
                    ((WeatherMixin) currentWeather).setPrecipitation(originalWeather.precipitation);
                    ((WeatherMixin) currentWeather).setTemperature(temp);
            }
        }else if(temp <= 0.95) {
            //Temperate Biomes
            switch (season) {
                case SUMMER:
                    ((WeatherMixin) currentWeather).setPrecipitation(originalWeather.precipitation);
                    ((WeatherMixin) currentWeather).setTemperature(temp+0.2f);
                    break;
                case FALL:
                    ((WeatherMixin) currentWeather).setPrecipitation(originalWeather.precipitation);
                    ((WeatherMixin) currentWeather).setTemperature(temp-0.1f);
                    break;
                case WINTER:
                    ((WeatherMixin) currentWeather).setPrecipitation(Biome.Precipitation.SNOW);
                    ((WeatherMixin) currentWeather).setTemperature(temp-0.7f);
                    break;
                default:
                    ((WeatherMixin) currentWeather).setPrecipitation(originalWeather.precipitation);
                    ((WeatherMixin) currentWeather).setTemperature(temp);
            }
        }else{
            //Hot biomes
            switch (season) {
                case SUMMER:
                    ((WeatherMixin) currentWeather).setPrecipitation(originalWeather.precipitation);
                    ((WeatherMixin) currentWeather).setTemperature(temp+0.2f);
                    break;
                case WINTER:
                    ((WeatherMixin) currentWeather).setPrecipitation(Biome.Precipitation.RAIN);
                    ((WeatherMixin) currentWeather).setTemperature(temp-0.2f);
                    break;
                default:
                    ((WeatherMixin) currentWeather).setPrecipitation(originalWeather.precipitation);
                    ((WeatherMixin) currentWeather).setTemperature(temp);
            }
        }
    }

}
