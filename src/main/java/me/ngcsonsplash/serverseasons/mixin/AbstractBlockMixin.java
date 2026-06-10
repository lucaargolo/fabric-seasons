package me.ngcsonsplash.serverseasons.mixin;

import me.ngcsonsplash.serverseasons.ServerSeasons;
import me.ngcsonsplash.serverseasons.utils.Meltable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin {
    
    @Inject(at = @At("HEAD"), method = "onStateReplaced")
    public void checkIfMeltableReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved, CallbackInfo ci) {
        if(world instanceof ServerWorld serverWorld && state.getBlock() instanceof Meltable meltableBlock) {
            meltableBlock.onMeltableReplaced(serverWorld, pos);
        }
    }

    @Inject(at = @At("HEAD"), method = "onBlockAdded")
    public void checkIfMeltableAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci) {
        if(!ServerSeasons.isMeltable(pos) && world instanceof ServerWorld serverWorld && state.getBlock() instanceof Meltable meltableBlock) {
            meltableBlock.onMeltableManuallyPlaced(serverWorld, pos);
        }
    }

}
