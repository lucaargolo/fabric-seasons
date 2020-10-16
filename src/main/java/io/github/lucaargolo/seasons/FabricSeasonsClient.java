package io.github.lucaargolo.seasons;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class FabricSeasonsClient implements ClientModInitializer {

    private static Season lastRenderedSeason = Season.SPRING;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            if(FabricSeasons.getCurrentSeason() != lastRenderedSeason) {
                lastRenderedSeason = FabricSeasons.getCurrentSeason();
                client.worldRenderer.reload();
            }
        });
    }
}
