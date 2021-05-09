package io.github.lucaargolo.seasons.block;

import io.github.lucaargolo.seasons.blockentities.GreenhouseGlassBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class GreenhouseGlassBlock extends Block implements BlockEntityProvider {

    public GreenhouseGlassBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new GreenhouseGlassBlockEntity();
    }

}
