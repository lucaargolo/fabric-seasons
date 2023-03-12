package io.github.lucaargolo.seasons.utils;

import io.github.lucaargolo.seasons.FabricSeasons;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;

public interface Meltable {

    default void onMeltableReplaced(ServerWorld world, BlockPos pos) {
        FabricSeasons.getPlacedMeltablesState(world).setManuallyPlaced(pos, false);
        FabricSeasons.getReplacedMeltablesState(world).setReplaced(pos, null);
    }

    default void onMeltableManuallyPlaced(ServerWorld world, BlockPos pos) {
        FabricSeasons.getPlacedMeltablesState(world).setManuallyPlaced(pos, true);
    }

    static void replaceBlockOnSnow(ServerWorld world, BlockPos blockPos, Biome biome) {
        BlockState plantState = world.getBlockState(blockPos);
        if(plantState.isIn(BlockTags.REPLACEABLE_PLANTS) || plantState.isIn(BlockTags.FLOWERS) || plantState.isIn(BlockTags.TALL_FLOWERS) || plantState.isIn(BlockTags.SAPLINGS) || plantState.isIn(TagKey.of(Registry.BLOCK_KEY, new Identifier("c:flowers")))) {
            if (!biome.doesNotSnow(blockPos) && blockPos.getY() >= world.getBottomY() && blockPos.getY() < world.getTopY() && world.getLightLevel(LightType.BLOCK, blockPos) < 10) {
                BlockState upperState = world.getBlockState(blockPos.up());
                if(plantState.getProperties().contains(TallPlantBlock.HALF) && upperState.getProperties().contains(TallPlantBlock.HALF)) {
                    if(upperState.get(TallPlantBlock.HALF) == DoubleBlockHalf.UPPER) {
                        FabricSeasons.setMeltable(blockPos);
                        FabricSeasons.getReplacedMeltablesState(world).setReplaced(blockPos, plantState);
                        world.setBlockState(blockPos, Blocks.SNOW.getDefaultState(), Block.FORCE_STATE);
                        world.setBlockState(blockPos.up(), Blocks.AIR.getDefaultState());
                        Blocks.SNOW.getDefaultState().updateNeighbors(world, blockPos, Block.NOTIFY_ALL);
                        world.updateListeners(blockPos, plantState, Blocks.SNOW.getDefaultState(), Block.NOTIFY_ALL);
                    }
                }else if(upperState.isAir()) {
                    FabricSeasons.setMeltable(blockPos);
                    FabricSeasons.getReplacedMeltablesState(world).setReplaced(blockPos, plantState);
                    world.setBlockState(blockPos, Blocks.SNOW.getDefaultState());
                }
            }
        }
    }

}
