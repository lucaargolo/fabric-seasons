package io.github.lucaargolo.seasons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import io.github.lucaargolo.seasons.commands.SeasonCommand;
import io.github.lucaargolo.seasons.mixed.BiomeMixed;
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
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
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
            Registries.ITEM.forEach(item -> {
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
        long springLength = CONFIG.getSpringLength();
        long summerLength = CONFIG.getSummerLength();
        long fallLength = CONFIG.getFallLength();
        long winterLength = CONFIG.getWinterLength();
        RegistryKey<World> dimension = world.getRegistryKey();
        if (CONFIG.isValidInDimension(dimension) && !CONFIG.isSeasonLocked()) {
            if(CONFIG.isSeasonTiedWithSystemTime()) {
                return getTimeToNextSystemSeason() * 24000;
            }

            Season currentSeason = getCurrentSeason(world);

            long[] seasonLengthArray = new long[]{springLength, summerLength,  fallLength, winterLength};
            Season[] seasonArray = new Season[]{Season.SPRING, Season.SUMMER,  Season.FALL, Season.WINTER};

            int startSeasonIndex = switch (CONFIG.getStartingSeason()) {
                case SPRING -> 0;
                case SUMMER -> 1;
                case FALL -> 2;
                case WINTER -> 3;
            };

            long season1LimitYTD = seasonLengthArray[startSeasonIndex];
            long season2LimitYTD = season1LimitYTD + seasonLengthArray[(startSeasonIndex + 1) % 4];
            long season3LimitYTD = season2LimitYTD + seasonLengthArray[(startSeasonIndex + 2) % 4];
            long yearLength = season3LimitYTD + seasonLengthArray[(startSeasonIndex + 3) % 4];
            long timeOfYear = world.getTimeOfDay() % yearLength;

            if(currentSeason == seasonArray[startSeasonIndex]) {
                return season1LimitYTD - timeOfYear;
            } else if(currentSeason == seasonArray[(startSeasonIndex + 1) % 4]) {
                return season2LimitYTD - timeOfYear;
            } else if (currentSeason == seasonArray[(startSeasonIndex + 2) % 4]) {
                return season3LimitYTD - timeOfYear;
            } else if (currentSeason == seasonArray[(startSeasonIndex + 3) % 4]) {
                return yearLength - timeOfYear;
            }
        }
        return Long.MAX_VALUE;
    }

    public static Season getNextSeason(World world, Season currentSeason) {
        RegistryKey<World> dimension = world.getRegistryKey();
        if (CONFIG.isValidInDimension(dimension)) {
            if(CONFIG.isSeasonLocked()) {
                return CONFIG.getLockedSeason();
            }
            if(CONFIG.isSeasonTiedWithSystemTime()) {
                return getCurrentSystemSeason().getNext();
            }

            return switch (getCurrentSeason(world)) {
                case SPRING -> Season.SUMMER;
                case SUMMER -> Season.FALL;
                case FALL -> Season.WINTER;
                case WINTER -> Season.SPRING;
            };
        }
        return Season.SPRING;
    }

    public static Season getCurrentSeason(World world) {
        long springLength = CONFIG.getSpringLength();
        long summerLength = CONFIG.getSummerLength();
        long fallLength = CONFIG.getFallLength();
        long winterLength = CONFIG.getWinterLength();
        RegistryKey<World> dimension = world.getRegistryKey();
        if (CONFIG.isValidInDimension(dimension)) {
            if(CONFIG.isSeasonLocked()) {
                return CONFIG.getLockedSeason();
            }else if(CONFIG.isSeasonTiedWithSystemTime()) {
                return getCurrentSystemSeason();
            }else if(CONFIG.isValidStartingSeason() && springLength >= 0 && summerLength >= 0 && fallLength >= 0 && winterLength >= 0) {
                long[] seasonLengthArray = new long[]{springLength, summerLength, fallLength, winterLength};
                Season[] seasonArray = new Season[]{Season.SPRING, Season.SUMMER,  Season.FALL, Season.WINTER};

                int startSeasonIndex = switch (CONFIG.getStartingSeason()) {
                    case SPRING -> 0;
                    case SUMMER -> 1;
                    case FALL -> 2;
                    case WINTER -> 3;
                };

                long season1LimitYTD = seasonLengthArray[startSeasonIndex];
                long season2LimitYTD = season1LimitYTD + seasonLengthArray[(startSeasonIndex + 1) % 4];
                long season3LimitYTD = season2LimitYTD + seasonLengthArray[(startSeasonIndex + 2) % 4];
                long yearLength = season3LimitYTD + seasonLengthArray[(startSeasonIndex + 3) % 4];
                long timeOfYear = world.getTimeOfDay() % yearLength;

                if(timeOfYear < season1LimitYTD) {
                    return seasonArray[startSeasonIndex];
                } else if(timeOfYear < season2LimitYTD) {
                    return seasonArray[(startSeasonIndex + 1) % 4];
                } else if (timeOfYear < season3LimitYTD) {
                    return seasonArray[(startSeasonIndex + 2) % 4];
                } else if (timeOfYear < yearLength) {
                    return seasonArray[(startSeasonIndex + 3) % 4];
                }
            }
        }
        return Season.SPRING;
    }

    @Environment(EnvType.CLIENT)
    public static Season getCurrentSeason() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if(player != null && player.getWorld() != null) {
            return getCurrentSeason(player.getWorld());
        }
        return Season.SPRING;
    }

    private static long getTimeToNextSystemSeason() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextSeasonStart;

        Season currentSeason = getCurrentSystemSeason();
        if(CONFIG.isInNorthHemisphere()) {
            nextSeasonStart = switch (currentSeason) {
                case WINTER -> LocalDateTime.of(now.getYear(), 2, 25, 0, 0);
                case SPRING -> LocalDateTime.of(now.getYear(), 6, 1, 0, 0);
                case SUMMER -> LocalDateTime.of(now.getYear(), 9, 1, 0, 0);
                case FALL -> LocalDateTime.of(now.getYear(), 11, 26, 0, 0);
            };
        }else{
            nextSeasonStart = switch (currentSeason) {
                case WINTER -> LocalDateTime.of(now.getYear(), 8, 25, 0, 0);
                case SPRING -> LocalDateTime.of(now.getYear(), 12, 1, 0, 0);
                case SUMMER -> LocalDateTime.of(now.getYear(), 3, 1, 0, 0);
                case FALL -> LocalDateTime.of(now.getYear(), 5, 26, 0, 0);
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
            if(m == 3 || m == 4 || m == 5) {
                season = Season.SPRING;
            }else if(m == 6 || m == 7 || m == 8) {
                season = Season.SUMMER;
            }else if(m == 9 || m == 10) {
                season = Season.FALL;
            }else if(m == 11) {
                if(d < 26)
                    season = Season.FALL;
                else
                    season = Season.WINTER;
            }else if(m == 12 || m == 1) {
                season = Season.WINTER;
            }else if(m == 2) {
                if(d < 25)
                    season = Season.WINTER;
                else
                    season = Season.SPRING;
            }else{
                throw new IllegalArgumentException("Invalid Month number: " + m);
            }
        }else{
            if(m == 9 || m == 10 || m == 11) {
                season = Season.SPRING;
            }else if(m == 12 || m == 1 || m == 2) {
                season = Season.SUMMER;
            }else if(m == 3 || m == 4) {
                season = Season.FALL;
            }else if(m == 5) {
                if(d < 26)
                    season = Season.FALL;
                else
                    season = Season.WINTER;
            }else if(m == 6 || m == 7) {
                season = Season.WINTER;
            }else if(m == 8) {
                if(d < 25)
                    season = Season.WINTER;
                else
                    season = Season.SPRING;
            }else{
                throw new IllegalArgumentException("Invalid Month number: " + m);
            }
        }
        return season;
    }

    private static final TagKey<Biome> IGNORED_CATEGORIES_TAG = TagKey.of(RegistryKeys.BIOME, new Identifier(FabricSeasons.MOD_ID, "ignored"));

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
            originalWeather = new Biome.Weather(currentWeather.hasPrecipitation(), currentWeather.temperature(), currentWeather.temperatureModifier(), currentWeather.downfall());
            ((BiomeMixed) (Object) biome).setOriginalWeather(originalWeather);
        }
        Season season = FabricSeasons.getCurrentSeason(world);

        Pair<Boolean, Float> modifiedWeather = getSeasonWeather(season, biomeId, originalWeather.hasPrecipitation, originalWeather.temperature);
        currentWeather.hasPrecipitation = modifiedWeather.getLeft();
        currentWeather.temperature = modifiedWeather.getRight();
    }

    public static Pair<Boolean, Float> getSeasonWeather(Season season, Identifier biomeId, Boolean hasPrecipitation, float temp) {
        if(!CONFIG.doTemperatureChanges(biomeId)) {
            return new Pair<>(hasPrecipitation, temp);
        }
        if(CONFIG.isSnowForcedInBiome(biomeId) && season == Season.WINTER) {
            return new Pair<>(hasPrecipitation, 0.14f);
        }else if(temp <= -0.51) {
            //Permanently Frozen Biomes
            return switch (season) {
                case SPRING -> CONFIG.isFallAndSpringReversed() ? new Pair<>(hasPrecipitation, temp - 0.3f) : new Pair<>(hasPrecipitation, temp);
                case SUMMER -> new Pair<>(hasPrecipitation, temp + 0.84f);
                case WINTER -> new Pair<>(hasPrecipitation, temp - 0.7f);
                case FALL -> CONFIG.isFallAndSpringReversed() ? new Pair<>(hasPrecipitation, temp) : new Pair<>(hasPrecipitation, temp - 0.3f);
            };
        }else if(temp <= 0.15) {
            //Usually Frozen Biomes
            return switch (season) {
                case SPRING -> CONFIG.isFallAndSpringReversed() ? new Pair<>(hasPrecipitation, temp - 0.25f) : new Pair<>(hasPrecipitation, temp);
                case SUMMER -> new Pair<>(hasPrecipitation, temp + (CONFIG.shouldSnowyBiomesMeltInSummer() ? 0.66f : 0f));
                case WINTER -> new Pair<>(hasPrecipitation, temp - 0.75f);
                case FALL -> CONFIG.isFallAndSpringReversed() ? new Pair<>(hasPrecipitation, temp) : new Pair<>(hasPrecipitation, temp - 0.25f);
            };
        }else if(temp <= 0.49) {
            //Temparate Biomes
            return switch (season) {
                case SPRING -> CONFIG.isFallAndSpringReversed() ? new Pair<>(hasPrecipitation, temp - 0.16f) : new Pair<>(hasPrecipitation, temp);
                case SUMMER -> new Pair<>(hasPrecipitation, temp + 0.66f);
                case WINTER -> new Pair<>(hasPrecipitation, temp - 0.8f);
                case FALL  -> CONFIG.isFallAndSpringReversed() ? new Pair<>(hasPrecipitation, temp) : new Pair<>(hasPrecipitation, temp - 0.16f);
            };
        }else if(temp <= 0.79) {
            //Usually Ice Free Biomes
            return switch (season) {
                case SPRING -> CONFIG.isFallAndSpringReversed() ? new Pair<>(hasPrecipitation, temp - 0.34f) : new Pair<>(hasPrecipitation, temp);
                case SUMMER -> new Pair<>(hasPrecipitation, temp + 0.46f);
                case WINTER -> new Pair<>(hasPrecipitation, temp - 0.56f);
                case FALL -> CONFIG.isFallAndSpringReversed() ? new Pair<>(hasPrecipitation, temp) : new Pair<>(hasPrecipitation, temp - 0.34f);
            };
        }else{
            // Ice Free Biomes
            return switch (season) {
                case SPRING -> CONFIG.isFallAndSpringReversed() ? new Pair<>(hasPrecipitation, temp - 0.34f) : new Pair<>(hasPrecipitation, temp);
                case SUMMER -> new Pair<>(hasPrecipitation, temp + 0.4f);
                case WINTER -> new Pair<>(true, temp - 0.64f);
                case FALL -> CONFIG.isFallAndSpringReversed() ? new Pair<>(hasPrecipitation, temp) : new Pair<>(hasPrecipitation, temp - 0.34f);
            };
        }
    }

}
