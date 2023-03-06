package io.github.lucaargolo.seasons;

import io.github.lucaargolo.seasons.resources.SeasonFoliageColors;
import io.github.lucaargolo.seasons.resources.SeasonGrassColors;
import io.github.lucaargolo.seasons.utils.ModConfig;
import io.github.lucaargolo.seasons.utils.ModIdentifier;
import io.github.lucaargolo.seasons.utils.Season;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

import static io.github.lucaargolo.seasons.FabricSeasons.ASK_FOR_CONFIG;

public class FabricSeasonsClient implements ClientModInitializer {

    private static boolean isServerConfig = false;
    private static ModConfig clientConfig = null;
    private static final Map<RegistryKey<World>, Season> lastRenderedSeasonMap = new HashMap<>();

    @Override
    public void onInitializeClient() {
        clientConfig = FabricSeasons.CONFIG;
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SeasonGrassColors());
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SeasonFoliageColors());

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            FabricSeasons.SEEDS_MAP.clear();
            Registry.ITEM.forEach(item -> {
                if(item instanceof BlockItem) {
                    Block block = ((BlockItem) item).getBlock();
                    if(block instanceof CropBlock || block instanceof StemBlock || block instanceof CocoaBlock || block instanceof SaplingBlock) {
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
            FabricSeasons.CONFIG = FabricSeasons.GSON.fromJson(configJson, ModConfig.class);
            isServerConfig = true;
            FabricSeasons.LOGGER.info("Received dedicated server config.");
        });


        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if(!client.isIntegratedServerRunning()) {
                FabricSeasons.LOGGER.info("Joined dedicated server, asking for config.");
                ClientPlayNetworking.send(ASK_FOR_CONFIG, PacketByteBufs.empty());
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> {
            if(isServerConfig && clientConfig != null) {
                FabricSeasons.LOGGER.info("Left dedicated server, restoring config.");
                FabricSeasons.CONFIG = clientConfig;
                isServerConfig = false;
            }
        }));

        BlockRenderLayerMap.INSTANCE.putBlock(Registry.BLOCK.get(new ModIdentifier("greenhouse_glass")), RenderLayer.getTranslucent());
        //Since we're replacing the Blocks.ICE entry we have to manually add the default ice block to the translucent render layer
        BlockRenderLayerMap.INSTANCE.putBlock(Registry.BLOCK.get(new Identifier("ice")), RenderLayer.getTranslucent());
    }
}
