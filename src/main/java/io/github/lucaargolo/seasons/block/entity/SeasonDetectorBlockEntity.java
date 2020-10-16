package io.github.lucaargolo.seasons.block.entity;

import io.github.lucaargolo.seasons.block.SeasonDetectorBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Tickable;

public class SeasonDetectorBlockEntity extends BlockEntity implements Tickable {

    public SeasonDetectorBlockEntity(BlockEntityType<?> type) {
        super(type);
    }

    @Override
    public void tick() {
        if (this.world != null && !this.world.isClient && this.world.getTime() % 20L == 0L) {
            BlockState blockState = this.getCachedState();
            Block block = blockState.getBlock();
            if (block instanceof SeasonDetectorBlock) {
                SeasonDetectorBlock.updateState(blockState, this.world, this.pos);
            }
        }
    }
}
