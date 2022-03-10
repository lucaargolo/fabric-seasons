package io.github.lucaargolo.seasons.block;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.blockentities.GreenhouseGlassBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GreenhouseGlassBlock extends BlockWithEntity {

    public GreenhouseGlassBlock(Settings settings) {
        super(settings);
    }

    @Override
    public GreenhouseGlassBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GreenhouseGlassBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : checkType(type, FabricSeasons.GREENHOUSE_GLASS_TYPE, GreenhouseGlassBlockEntity::serverTick);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
