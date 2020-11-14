package io.github.lucaargolo.seasons.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IceBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(IceBlock.class)
public abstract class IceBlockMixin extends Block {

    @Shadow protected abstract void melt(BlockState state, World world, BlockPos pos);

    private static final BooleanProperty NATURAL = BooleanProperty.of("natural");

    public IceBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    public void init(Settings settings, CallbackInfo info) {
        if(this == Blocks.ICE)
            this.setDefaultState((this.stateManager.getDefaultState()).with(NATURAL, true));
    }

    @Inject(at = @At("HEAD"), method = "randomTick")
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo info) {
        if (this == Blocks.ICE && state.get(NATURAL) && world.getBiome(pos).getTemperature(pos) >= 0.15F) {
            this.melt(state, world, pos);
        }
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NATURAL);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(NATURAL, false);
    }
}
