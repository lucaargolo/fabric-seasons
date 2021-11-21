package com.yurisuika.seasons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.yurisuika.seasons.commands.SeasonCommand;
import com.yurisuika.seasons.mixin.WeatherAccessor;
import com.yurisuika.seasons.utils.ModConfig;
import com.yurisuika.seasons.utils.Season;
import com.yurisuika.seasons.utils.WeatherCache;
import com.yurisuika.seasons.utils.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Seasons implements ModInitializer {

    public static final String MOD_ID = "seasons";
    public static final Logger LOGGER = LogManager.getLogger("Seasons");

    public static ModConfig CONFIG;

    public static final JsonParser JSON_PARSER = new JsonParser();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static Block ORIGINAL_ICE;
    public static Block ORIGINAL_SNOW;

    public static HashMap<Item, Block> SEEDS_MAP = new HashMap<>();


    public static Identifier ASK_FOR_CONFIG = new Identifier(MOD_ID, "ask_for_config");
    public static Identifier ANSWER_CONFIG = new Identifier(MOD_ID, "answer_config");

    @Override
    public void onInitialize() {

        Path configPath = FabricLoader.getInstance().getConfigDir();
        File configFile = new File(configPath + File.separator + "seasons.json");

        LOGGER.info("Trying to read config file...");
        try {
            if (configFile.createNewFile()) {
                LOGGER.info("No config file found, creating a new one...");
                String json = GSON.toJson(JSON_PARSER.parse(GSON.toJson(new ModConfig())));
                try (PrintWriter out = new PrintWriter(configFile)) {
                    out.println(json);
                }
                CONFIG = new ModConfig();
                LOGGER.info("Successfully created default config file.");
            } else {
                LOGGER.info("A config file was found, loading it..");
                CONFIG = GSON.fromJson(new String(Files.readAllBytes(configFile.toPath())), ModConfig.class);
                if(CONFIG == null) {
                    throw new NullPointerException("The config file was empty.");
                }else{
                    LOGGER.info("Successfully loaded config file.");
                }
            }
        }catch (Exception exception) {
            LOGGER.error("There was an error creating/loading the config file!", exception);
            CONFIG = new ModConfig();
            LOGGER.warn("Defaulting to original config.");
        }

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> SeasonCommand.register(dispatcher));

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SEEDS_MAP.clear();
            Registry.ITEM.forEach(item -> {
                if(item instanceof BlockItem) {
                    Block block = ((BlockItem) item).getBlock();
                    if(block instanceof CropBlock || block instanceof StemBlock || block instanceof CocoaBlock || block instanceof SaplingBlock) {
                        Seasons.SEEDS_MAP.put(item, ((BlockItem) item).getBlock());
                    }
                }
            });
        });

        ServerTickEvents.END_SERVER_TICK.register(GreenhouseCache::tick);

        ServerPlayNetworking.registerGlobalReceiver(ASK_FOR_CONFIG, (server, player, handler, buf, responseSender) -> {
            String configJson = GSON.toJson(JSON_PARSER.parse(GSON.toJson(CONFIG)));
            PacketByteBuf configBuf = PacketByteBufs.create();
            configBuf.writeInt(configJson.length());
            configBuf.writeString(configJson, configJson.length());
            ServerPlayNetworking.send(player, ANSWER_CONFIG, configBuf);
        });
    }

    public static Season getCurrentSeason(World world) {
        RegistryKey<World> dimension = world.getRegistryKey();
        if (CONFIG.isValidInDimension(dimension)) {
            if(CONFIG.isSeasonLocked()) {
                return CONFIG.getLockedSeason();
            }
            if(CONFIG.isSeasonTiedWithSystemTime()) {
                return getCurrentSystemSeason();
            }
            int worldTime = Math.toIntExact(world.getTimeOfDay());
            int seasonTime = (worldTime / CONFIG.getSeasonLength());
            return Season.values()[seasonTime % 4];
        }
        return Season.SPRING;
    }

    @Environment(EnvType.CLIENT)
    public static Season getCurrentSeason() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if(player != null && player.world != null) {
            return getCurrentSeason(player.world);
        }
        return Season.SPRING;
    }

    private static Season getCurrentSystemSeason() {
        LocalDateTime date = LocalDateTime.now();
        int m = date.getMonthValue();
        int d = date.getDayOfMonth();
        Season season;

        if (CONFIG.isInNorthHemisphere()) {
            if (m == 1 || m == 2 || m == 3)
                season = Season.WINTER;
            else if (m == 4 || m == 5 || m == 6)
                season = Season.SPRING;
            else if (m == 7 || m == 8 || m == 9)
                season = Season.SUMMER;
            else
                season = Season.FALL;

            if (m == 3 && d > 19)
                season = Season.SPRING;
            else if (m == 6 && d > 20)
                season = Season.SUMMER;
            else if (m == 9 && d > 21)
                season = Season.FALL;
            else if (m == 12 && d > 20)
                season = Season.WINTER;
        } else {
            if (m == 1 || m == 2 || m == 3)
                season = Season.SUMMER;
            else if (m == 4 || m == 5 || m == 6)
                season = Season.FALL;
            else if (m == 7 || m == 8 || m == 9)
                season = Season.WINTER;
            else
                season = Season.SPRING;

            if (m == 3 && d > 19)
                season = Season.FALL;
            else if (m == 6 && d > 20)
                season = Season.WINTER;
            else if (m == 9 && d > 21)
                season = Season.SPRING;
            else if (m == 12 && d > 20)
                season = Season.SUMMER;
        }

        return season;
    }

    public static void injectBiomeTemperature(Biome biome, World world) {
        if(!CONFIG.doTemperatureChanges()) return;

        List<Biome.Category> ignoredCategories = Arrays.asList(Biome.Category.NONE, Biome.Category.NETHER, Biome.Category.THEEND, Biome.Category.OCEAN);
        if(ignoredCategories.contains(biome.getCategory())) return;

        Season season = Seasons.getCurrentSeason(world);

        Identifier biomeIdentifier = world.getRegistryManager().get(Registry.BIOME_KEY).getId(biome);
        Biome.Weather currentWeather = biome.weather;

        Biome.Weather originalWeather;
        if (!WeatherCache.hasCache(biomeIdentifier)) {
            originalWeather = new Biome.Weather(currentWeather.precipitation, currentWeather.temperature, currentWeather.temperatureModifier, currentWeather.downfall);
            WeatherCache.setCache(biomeIdentifier, originalWeather);
        } else {
            originalWeather = WeatherCache.getCache(biomeIdentifier);
        }

        if(originalWeather == null) {
            return;
        }
        float temp = originalWeather.temperature;
        if(biome.getCategory() == Biome.Category.JUNGLE || biome.getCategory() == Biome.Category.SWAMP) {
            //Jungle Biomes
            if (season == Season.WINTER) {
                ((WeatherAccessor) currentWeather).setPrecipitation(originalWeather.precipitation);
                ((WeatherAccessor) currentWeather).setTemperature(temp-0.1f);
            } else {
                ((WeatherAccessor) currentWeather).setPrecipitation(originalWeather.precipitation);
                ((WeatherAccessor) currentWeather).setTemperature(temp);
            }
        }else if(biome.getCategory() == Biome.Category.MESA) {
            //Badlands Biomes
            switch (season) {
                case SPRING -> {
                    ((WeatherAccessor) currentWeather).setPrecipitation(Biome.Precipitation.RAIN);
                    ((WeatherAccessor) currentWeather).setTemperature(temp);
                }
                case SUMMER -> {
                    ((WeatherAccessor) currentWeather).setPrecipitation(originalWeather.precipitation);
                    ((WeatherAccessor) currentWeather).setTemperature(temp + 0.2f);
                }
                case WINTER -> {
                    ((WeatherAccessor) currentWeather).setPrecipitation(Biome.Precipitation.SNOW);
                    ((WeatherAccessor) currentWeather).setTemperature(temp - 2.0f);
                }
                default -> {
                    ((WeatherAccessor) currentWeather).setPrecipitation(originalWeather.precipitation);
                    ((WeatherAccessor) currentWeather).setTemperature(temp);
                }
            }
        }else if(temp <= 0.1) {
            //Frozen Biomes
            switch (season) {
                case SUMMER -> {
                    ((WeatherAccessor) currentWeather).setPrecipitation(Biome.Precipitation.RAIN);
                    ((WeatherAccessor) currentWeather).setTemperature(temp + 0.3f);
                }
                case WINTER -> {
                    ((WeatherAccessor) currentWeather).setPrecipitation(Biome.Precipitation.SNOW);
                    ((WeatherAccessor) currentWeather).setTemperature(temp - 0.2f);
                }
                default -> {
                    ((WeatherAccessor) currentWeather).setPrecipitation(originalWeather.precipitation);
                    ((WeatherAccessor) currentWeather).setTemperature(temp);
                }
            }
        }else if(temp <= 0.3) {
            //Cold Biomes
            switch (season) {
                case SPRING -> {
                    ((WeatherAccessor) currentWeather).setPrecipitation(Biome.Precipitation.RAIN);
                    ((WeatherAccessor) currentWeather).setTemperature(temp);
                }
                case SUMMER -> {
                    ((WeatherAccessor) currentWeather).setPrecipitation(Biome.Precipitation.RAIN);
                    ((WeatherAccessor) currentWeather).setTemperature(temp + 0.2f);
                }
                case WINTER -> {
                    ((WeatherAccessor) currentWeather).setPrecipitation(Biome.Precipitation.SNOW);
                    ((WeatherAccessor) currentWeather).setTemperature(temp - 0.2f);
                }
                default -> {
                    ((WeatherAccessor) currentWeather).setPrecipitation(originalWeather.precipitation);
                    ((WeatherAccessor) currentWeather).setTemperature(temp);
                }
            }
        }else if(temp <= 0.95) {
            //Temperate Biomes
            switch (season) {
                case SUMMER -> {
                    ((WeatherAccessor) currentWeather).setPrecipitation(originalWeather.precipitation);
                    ((WeatherAccessor) currentWeather).setTemperature(temp + 0.2f);
                }
                case FALL -> {
                    ((WeatherAccessor) currentWeather).setPrecipitation(originalWeather.precipitation);
                    ((WeatherAccessor) currentWeather).setTemperature(temp - 0.1f);
                }
                case WINTER -> {
                    ((WeatherAccessor) currentWeather).setPrecipitation(Biome.Precipitation.SNOW);
                    ((WeatherAccessor) currentWeather).setTemperature(temp - 0.7f);
                }
                default -> {
                    ((WeatherAccessor) currentWeather).setPrecipitation(originalWeather.precipitation);
                    ((WeatherAccessor) currentWeather).setTemperature(temp);
                }
            }
        }else{
            //Hot biomes
            switch (season) {
                case SUMMER -> {
                    ((WeatherAccessor) currentWeather).setPrecipitation(originalWeather.precipitation);
                    ((WeatherAccessor) currentWeather).setTemperature(temp + 0.2f);
                }
                case WINTER -> {
                    ((WeatherAccessor) currentWeather).setPrecipitation(Biome.Precipitation.RAIN);
                    ((WeatherAccessor) currentWeather).setTemperature(temp - 0.2f);
                }
                default -> {
                    ((WeatherAccessor) currentWeather).setPrecipitation(originalWeather.precipitation);
                    ((WeatherAccessor) currentWeather).setTemperature(temp);
                }
            }
        }
    }

}
