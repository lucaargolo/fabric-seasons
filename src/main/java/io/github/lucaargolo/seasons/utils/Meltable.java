package io.github.lucaargolo.seasons.utils;

import io.github.lucaargolo.seasons.FabricSeasons;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public interface Meltable {

    default void onMeltableReplaced(ServerWorld world, BlockPos pos) {
        FabricSeasons.getMeltablesState(world).setManuallyPlaced(pos, false);
    }

    default void onMeltableManuallyPlaced(ServerWorld world, BlockPos pos) {
        FabricSeasons.getMeltablesState(world).setManuallyPlaced(pos, true);
    }

}
