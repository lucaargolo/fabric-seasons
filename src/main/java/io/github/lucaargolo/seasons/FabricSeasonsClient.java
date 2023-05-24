package io.github.lucaargolo.seasons;

import io.github.lucaargolo.seasons.commands.SeasonDebugCommand;
import io.github.lucaargolo.seasons.resources.CropConfigs;
import io.github.lucaargolo.seasons.resources.FoliageSeasonColors;
import io.github.lucaargolo.seasons.resources.GrassSeasonColors;
import io.github.lucaargolo.seasons.utils.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

import static io.github.lucaargolo.seasons.FabricSeasons.*;

public class FabricSeasonsClient implements ClientModInitializer {

    private static boolean isServerConfig = false;
    private static ModConfig clientConfig = null;
    private static final Map<RegistryKey<World>, Season> lastRenderedSeasonMap = new HashMap<>();

    public static final Map<BakedModel, Map<Season, BakedModel>> originalToSeasonModelMap = new HashMap<>();

    @Override
    public void onInitializeClient() {
        clientConfig = FabricSeasons.CONFIG;
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new GrassSeasonColors());
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new FoliageSeasonColors());

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            FabricSeasons.SEEDS_MAP.clear();
            Registries.ITEM.forEach(item -> {
                if(item instanceof BlockItem) {
                    Block block = ((BlockItem) item).getBlock();
                    if(block instanceof SeasonalFertilizable) {
                        FabricSeasons.SEEDS_MAP.put(item, ((BlockItem) item).getBlock());
                    }
                }
            });
        });

        ClientTickEvents.END_WORLD_TICK.register((clientWorld) -> {
            if(FabricSeasons.getCurrentSeason(clientWorld) != lastRenderedSeasonMap.get(clientWorld.getRegistryKey())) {
                lastRenderedSeasonMap.put(clientWorld.getRegistryKey(), FabricSeasons.getCurrentSeason(clientWorld));
                MinecraftClient.getInstance().worldRenderer.reload();
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(FabricSeasons.ANSWER_CONFIG, (client, handler, buf, responseSender) -> {
            String configJson = buf.readString();
            client.execute(() -> {
                FabricSeasons.CONFIG = FabricSeasons.GSON.fromJson(configJson, ModConfig.class);
                isServerConfig = true;
                FabricSeasons.LOGGER.info("["+MOD_NAME+"] Received dedicated server config.");
            });

        });

        ClientPlayNetworking.registerGlobalReceiver(FabricSeasons.UPDATE_CROPS, (client, handler, buf, responseSender) -> {
            CropConfig receivedConfig = CropConfig.fromBuf(buf);
            HashMap<Identifier, CropConfig> receivedMap = CropConfigs.fromBuf(buf);
            client.execute(() -> {
                CropConfigs.receiveConfig(receivedConfig, receivedMap);
                FabricSeasons.LOGGER.info("["+MOD_NAME+"] Received dedicated server crops.");
            });

        });


        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if(CONFIG.shouldNotifyCompat()) {
                CompatWarnState.join(client);
            }
            if(!client.isIntegratedServerRunning()) {
                FabricSeasons.LOGGER.info("["+MOD_NAME+"] Joined dedicated server, asking for config.");
                ClientPlayNetworking.send(ASK_FOR_CONFIG, PacketByteBufs.empty());
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> {
            CropConfigs.clear();
            if(isServerConfig && clientConfig != null) {
                FabricSeasons.LOGGER.info("["+MOD_NAME+"] Left dedicated server, restoring config.");
                FabricSeasons.CONFIG = clientConfig;
                isServerConfig = false;
            }
        }));

        if(FabricLoader.getInstance().isDevelopmentEnvironment() || CONFIG.isDebugCommandEnabled()) {
            ClientCommandRegistrationCallback.EVENT.register((SeasonDebugCommand::register));
        }

        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent((container) -> {
            ResourceManagerHelper.registerBuiltinResourcePack(new ModIdentifier("seasonal_lush_caves"), container, Text.literal("Seasonal Lush Caves"), ResourcePackActivationType.DEFAULT_ENABLED);
        });
    }
}
