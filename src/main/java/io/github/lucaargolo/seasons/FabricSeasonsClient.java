package io.github.lucaargolo.seasons;

import io.github.lucaargolo.seasons.colors.SeasonFoliageColormapResourceSupplier;
import io.github.lucaargolo.seasons.colors.SeasonGrassColormapResourceSupplier;
import io.github.lucaargolo.seasons.utils.Season;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.resource.ResourceType;
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
                    if(block instanceof CropBlock || block instanceof StemBlock || block instanceof CocoaBlock) {
                        FabricSeasons.SEEDS_MAP.put(item, ((BlockItem) item).getBlock());
                    }
                }
            });
        });

        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            if(FabricSeasons.getCurrentSeason() != lastRenderedSeason) {
                lastRenderedSeason = FabricSeasons.getCurrentSeason();
                client.worldRenderer.reload();
            }
        });
    }
}
