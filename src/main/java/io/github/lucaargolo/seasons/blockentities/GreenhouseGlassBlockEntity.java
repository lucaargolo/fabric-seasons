package io.github.lucaargolo.seasons.blockentities;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.utils.GreenhouseCache;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GreenhouseGlassBlockEntity extends BlockEntity {

    public GreenhouseGlassBlockEntity(BlockPos pos, BlockState state) {
        super(FabricSeasons.GREENHOUSE_GLASS_TYPE, pos, state);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, GreenhouseGlassBlockEntity entity) {
        GreenhouseCache.add(world, pos);
    }

}
