package io.github.lucaargolo.seasons;

import io.github.lucaargolo.seasons.colors.SeasonFoliageColormapResourceSupplier;
import io.github.lucaargolo.seasons.colors.SeasonGrassColormapResourceSupplier;
import io.github.lucaargolo.seasons.utils.ModIdentifier;
import io.github.lucaargolo.seasons.utils.Season;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FabricSeasonsClient implements ClientModInitializer {

    private static Season lastRenderedSeason = Season.SPRING;

    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SeasonGrassColormapResourceSupplier());
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SeasonFoliageColormapResourceSupplier());

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
            if(FabricSeasons.getCurrentSeason() != lastRenderedSeason) {
                lastRenderedSeason = FabricSeasons.getCurrentSeason();
                MinecraftClient.getInstance().worldRenderer.reload();
            }
        });

        BlockRenderLayerMap.INSTANCE.putBlock(Registry.BLOCK.get(new ModIdentifier("greenhouse_glass")), RenderLayer.getTranslucent());
        //Since we're replacing the Blocks.ICE entry we have to manually add the default ice block to the translucent render layer
        BlockRenderLayerMap.INSTANCE.putBlock(Registry.BLOCK.get(new Identifier("ice")), RenderLayer.getTranslucent());
    }
}
