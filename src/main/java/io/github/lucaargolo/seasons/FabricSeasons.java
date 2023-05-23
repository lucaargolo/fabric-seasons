package io.github.lucaargolo.seasons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import io.github.lucaargolo.seasons.commands.SeasonCommand;
import io.github.lucaargolo.seasons.mixed.BiomeMixed;
import io.github.lucaargolo.seasons.mixin.WeatherAccessor;
import io.github.lucaargolo.seasons.resources.CropConfigs;
import io.github.lucaargolo.seasons.utils.*;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

public class FabricSeasons implements ModInitializer {

    private static final LongArraySet temporaryMeltableCache = new LongArraySet();
    public static final String MOD_ID = "seasons";
    public static final String MOD_NAME = "Fabric Seasons";

    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static ModConfig CONFIG;

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static HashMap<Item, Block> SEEDS_MAP = new HashMap<>();

    public static Identifier ASK_FOR_CONFIG = new ModIdentifier("ask_for_config");
    public static Identifier ANSWER_CONFIG = new ModIdentifier("anwer_config");

    public static Identifier UPDATE_CROPS = new ModIdentifier("update_crops");


    @Override
    public void onInitialize() {

        Path configPath = FabricLoader.getInstance().getConfigDir();
        File configFile = new File(configPath + File.separator + "seasons.json");

        LOGGER.info("["+MOD_NAME+"] Trying to read config file...");
        try {
            if (configFile.createNewFile()) {
                LOGGER.info("["+MOD_NAME+"] No config file found, creating a new one...");
                String json = GSON.toJson(JsonParser.parseString(GSON.toJson(new ModConfig())));
                try (PrintWriter out = new PrintWriter(configFile)) {
                    out.println(json);
                }
                CONFIG = new ModConfig();
                LOGGER.info("["+MOD_NAME+"] Successfully created default config file.");
            } else {
                LOGGER.info("["+MOD_NAME+"] A config file was found, loading it..");
                CONFIG = GSON.fromJson(new String(Files.readAllBytes(configFile.toPath())), ModConfig.class);
                if(CONFIG == null) {
                    throw new NullPointerException("["+MOD_NAME+"] The config file was empty.");
                }else{
                    LOGGER.info("["+MOD_NAME+"] Successfully loaded config file.");
                }
            }
        }catch (Exception exception) {
            LOGGER.error("["+MOD_NAME+"] There was an error creating/loading the config file!", exception);
            CONFIG = new ModConfig();
            LOGGER.warn("["+MOD_NAME+"] Defaulting to original config.");
        }

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, ignored) -> SeasonCommand.register(dispatcher));

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SEEDS_MAP.clear();
            Registry.ITEM.forEach(item -> {
                if(item instanceof BlockItem) {
                    Block block = ((BlockItem) item).getBlock();
                    if(block instanceof SeasonalFertilizable) {
                        FabricSeasons.SEEDS_MAP.put(item, ((BlockItem) item).getBlock());
                    }
                }
            });
        });

        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
            PacketByteBuf buf = PacketByteBufs.create();
            CropConfigs.getDefaultCropConfig().toBuf(buf);
            CropConfigs.toBuf(buf);
            ServerPlayNetworking.send(player, UPDATE_CROPS, buf);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            GreenhouseCache.tick(server);
            temporaryMeltableCache.clear();
        });

        ServerPlayNetworking.registerGlobalReceiver(ASK_FOR_CONFIG, (server, player, handler, buf, responseSender) -> {
            String configJson = GSON.toJson(JsonParser.parseString(GSON.toJson(CONFIG)));
            PacketByteBuf configBuf = PacketByteBufs.create();
            configBuf.writeString(configJson);
            ServerPlayNetworking.send(player, ANSWER_CONFIG, configBuf);
        });

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new CropConfigs());
    }

    public static void setMeltable(BlockPos blockPos) {
        temporaryMeltableCache.add(blockPos.asLong());
    }

    public static boolean isMeltable(BlockPos blockPos) {
        return temporaryMeltableCache.contains(blockPos.asLong());
    }

    public static PlacedMeltablesState getPlacedMeltablesState(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(PlacedMeltablesState::createFromNbt, PlacedMeltablesState::new, "seasons_placed_meltables");
    }

    public static ReplacedMeltablesState getReplacedMeltablesState(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(ReplacedMeltablesState::createFromNbt, ReplacedMeltablesState::new, "seasons_replaced_meltables");
    }
    public static long getTimeToNextSeason(World world) {
        RegistryKey<World> dimension = world.getRegistryKey();
        if (CONFIG.isValidInDimension(dimension) && !CONFIG.isSeasonLocked()) {
            if(CONFIG.isSeasonTiedWithSystemTime()) {
                return getTimeToNextSystemSeason() * 24000;
            }
            long springTime = world.getTimeOfDay() % CONFIG.getYearLength();
            long summerTime = springTime - CONFIG.getSpringLength();
            long fallTime = summerTime - CONFIG.getSummerLength();
            long winterTime = fallTime - CONFIG.getFallLength();

            long seasonTime = switch (getCurrentSeason(world)) {
                case SPRING -> springTime;
                case SUMMER -> summerTime;
                case FALL -> fallTime;
                case WINTER -> winterTime;
            };
            return getCurrentSeason().getSeasonLength() - seasonTime;
        }
        return Long.MAX_VALUE;
    }

    public static Season getNextSeason(World world) {
        RegistryKey<World> dimension = world.getRegistryKey();
        if (CONFIG.isValidInDimension(dimension)) {
            if(CONFIG.isSeasonLocked()) {
                return CONFIG.getLockedSeason();
            }
            if(CONFIG.isSeasonTiedWithSystemTime()) {
                return getCurrentSystemSeason().getNext();
            }

            long springTime = world.getTimeOfDay() % CONFIG.getYearLength();
            long summerTime = springTime - CONFIG.getSpringLength();
            long fallTime = summerTime - CONFIG.getSummerLength();
            long winterTime = fallTime - CONFIG.getFallLength();

            long seasonTime = switch (getCurrentSeason(world)) {
                case SPRING -> springTime;
                case SUMMER -> summerTime;
                case FALL -> fallTime;
                case WINTER -> winterTime;
            };

            long worldTime = world.getTimeOfDay() + seasonTime;

            springTime = worldTime % CONFIG.getYearLength();
            summerTime = springTime - CONFIG.getSpringLength();
            fallTime = summerTime - CONFIG.getSummerLength();
            winterTime = fallTime - CONFIG.getFallLength();

            if(winterTime >= 0 && CONFIG.getWinterLength() > 0) {
                return Season.WINTER;
            }else if(fallTime >= 0 && CONFIG.getFallLength() > 0) {
                return Season.FALL;
            }else if(summerTime >= 0 && CONFIG.getSummerLength() > 0) {
                return Season.SUMMER;
            }else if(springTime >= 0 && CONFIG.getSpringLength() > 0) {
                return Season.SPRING;
            }
        }
        return Season.SPRING;
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

            long springTime = world.getTimeOfDay() % CONFIG.getYearLength();
            long summerTime = springTime - CONFIG.getSpringLength();
            long fallTime = summerTime - CONFIG.getSummerLength();
            long winterTime = fallTime - CONFIG.getFallLength();

            if(winterTime >= 0 && CONFIG.getWinterLength() > 0) {
                return Season.WINTER;
            }else if(fallTime >= 0 && CONFIG.getFallLength() > 0) {
                return Season.FALL;
            }else if(summerTime >= 0 && CONFIG.getSummerLength() > 0) {
                return Season.SUMMER;
            }else if(springTime >= 0 && CONFIG.getSpringLength() > 0) {
                return Season.SPRING;
            }
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

    private static long getTimeToNextSystemSeason() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextSeasonStart;

        Season currentSeason = getCurrentSystemSeason();
        if(CONFIG.isInNorthHemisphere()) {
            nextSeasonStart = switch (currentSeason) {
                case WINTER -> LocalDateTime.of(now.getYear(), 3, 20, 0, 0);
                case SPRING -> LocalDateTime.of(now.getYear(), 6, 21, 0, 0);
                case SUMMER -> LocalDateTime.of(now.getYear(), 9, 22, 0, 0);
                case FALL -> LocalDateTime.of(now.getYear(), 12, 21, 0, 0);
            };
        }else{
            nextSeasonStart = switch (currentSeason) {
                case SUMMER -> LocalDateTime.of(now.getYear(), 3, 20, 0, 0);
                case FALL -> LocalDateTime.of(now.getYear(), 6, 21, 0, 0);
                case WINTER -> LocalDateTime.of(now.getYear(), 9, 22, 0, 0);
                case SPRING -> LocalDateTime.of(now.getYear(), 12, 21, 0, 0);
            };
        }

        Duration timeToNextSeason = Duration.between(now, nextSeasonStart);
        return timeToNextSeason.toDays();
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

    private static final TagKey<Biome> IGNORED_CATEGORIES_TAG = TagKey.of(Registry.BIOME_KEY, new Identifier(FabricSeasons.MOD_ID, "ignored"));
    private static final TagKey<Biome> JUNGLE_LIKE_TAG = TagKey.of(Registry.BIOME_KEY, new Identifier(FabricSeasons.MOD_ID, "jungle_like"));

    @SuppressWarnings("ConstantValue")
    public static void injectBiomeTemperature(RegistryEntry<Biome> entry, World world) {
        if(entry.isIn(IGNORED_CATEGORIES_TAG))
            return;

        // legacy, prefer use of tag where possible
        Biome biome = entry.value();
        Identifier biomeId = entry.getKey().orElse(BiomeKeys.PLAINS).getValue();
        if(!CONFIG.doTemperatureChanges(biomeId)) return;

        Biome.Weather currentWeather = biome.weather;
        Biome.Weather originalWeather = ((BiomeMixed) (Object) biome).getOriginalWeather();
        if (originalWeather == null) {
            originalWeather = new Biome.Weather(currentWeather.precipitation, currentWeather.temperature, currentWeather.temperatureModifier, currentWeather.downfall);
            ((BiomeMixed) (Object) biome).setOriginalWeather(originalWeather);
        }
        WeatherAccessor weatherAccessor = ((WeatherAccessor) (Object) currentWeather);
        assert weatherAccessor != null;

        Season season = FabricSeasons.getCurrentSeason(world);
        boolean isJungle = entry.isIn(JUNGLE_LIKE_TAG);
        Pair<Biome.Precipitation, Float> modifiedWeather = getSeasonWeather(season, biomeId, isJungle, originalWeather.precipitation, originalWeather.temperature);
        weatherAccessor.setPrecipitation(modifiedWeather.getLeft());
        weatherAccessor.setTemperature(modifiedWeather.getRight());
    }

    public static Pair<Biome.Precipitation, Float> getSeasonWeather(Season season, Identifier biomeId, boolean jungle, Biome.Precipitation precipitation, float temp) {
        if(!CONFIG.doTemperatureChanges(biomeId)) {
            return new Pair<>(precipitation, temp);
        }
        if(jungle) {
            //Jungle Biomes
            if (season == Season.WINTER) {
                return new Pair<>(precipitation, temp-0.1f);
            } else {
                return new Pair<>(precipitation, temp);
            }
        }else if(temp <= 0.1) {
            //Frozen Biomes
            switch (season) {
                case SUMMER -> {
                    return new Pair<>(Biome.Precipitation.RAIN, temp + 0.3f);
                }
                case WINTER -> {
                    return new Pair<>(Biome.Precipitation.SNOW, temp - 0.2f);
                }
                default -> {
                    return new Pair<>(precipitation, temp);
                }
            }
        }else if(temp <= 0.3) {
            //Cold Biomes
            switch (season) {
                case SPRING -> {
                    return new Pair<>(Biome.Precipitation.RAIN, temp);
                }
                case SUMMER -> {
                    return new Pair<>(Biome.Precipitation.RAIN, temp + 0.2f);
                }
                case WINTER -> {
                    return new Pair<>(Biome.Precipitation.SNOW, temp - 0.2f);
                }
                default -> {
                    return new Pair<>(precipitation, temp);
                }
            }
        }else if(temp <= 0.95) {
            //Temperate Biomes
            switch (season) {
                case SUMMER -> {
                    return new Pair<>(precipitation, temp + 0.2f);
                }
                case FALL -> {
                    return new Pair<>(precipitation, temp - 0.1f);
                }
                case WINTER -> {
                    return new Pair<>(Biome.Precipitation.SNOW, temp - 0.7f);
                }
                default -> {
                    return new Pair<>(precipitation, temp);
                }
            }
        }else{
            //Hot biomes
            switch (season) {
                case SUMMER -> {
                    return new Pair<>(precipitation, temp + 0.2f);
                }
                case WINTER -> {
                    return new Pair<>(Biome.Precipitation.RAIN, temp - 0.2f);
                }
                default -> {
                    return new Pair<>(precipitation, temp);
                }
            }
        }
    }

}
