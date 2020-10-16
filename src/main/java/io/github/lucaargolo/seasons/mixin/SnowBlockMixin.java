package io.github.lucaargolo.seasons.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SnowBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(SnowBlock.class)
public class SnowBlockMixin extends Block {

    @Shadow @Final public static IntProperty LAYERS;
    private static final BooleanProperty NATURAL = BooleanProperty.of("natural");

    public SnowBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    public void init(Settings settings, CallbackInfo info) {
        this.setDefaultState((this.stateManager.getDefaultState()).with(LAYERS, 1).with(NATURAL, true));
    }

    @Inject(at = @At("HEAD"), method = "appendProperties")
    private void appendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo info) {
        builder.add(NATURAL);
    }

    @Inject(at = @At("HEAD"), method = "randomTick")
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo info) {
        if (state.get(NATURAL) && world.getBiome(pos).weather.precipitation != Biome.Precipitation.SNOW) {
            Block.dropStacks(state, world, pos);
            world.removeBlock(pos, false);
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getPlacementState(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/block/BlockState;"), method = "getPlacementState")
    public BlockState getPlacementState(Block block, ItemPlacementContext ctx) {
        return block.getDefaultState().with(NATURAL, false);
    }
}
