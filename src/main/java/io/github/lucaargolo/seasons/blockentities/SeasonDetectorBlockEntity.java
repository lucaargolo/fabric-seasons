package io.github.lucaargolo.seasons.blockentities;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.block.SeasonDetectorBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SeasonDetectorBlockEntity extends BlockEntity {

    public SeasonDetectorBlockEntity(BlockPos pos, BlockState state) {
        super(FabricSeasons.SEASON_DETECTOR_TYPE, pos, state);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, SeasonDetectorBlockEntity entity) {
        if (world.getTime() % 20L == 0L) {
            Block block = state.getBlock();
            if (block instanceof SeasonDetectorBlock) {
                SeasonDetectorBlock.updateState(state, world, pos);
            }
        }
    }
}
