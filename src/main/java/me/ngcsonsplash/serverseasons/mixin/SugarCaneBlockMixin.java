package me.ngcsonsplash.serverseasons.mixin;

import me.ngcsonsplash.serverseasons.FabricSeasons;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(SugarCaneBlock.class)
public class SugarCaneBlockMixin {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldView;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;"), method = "canPlaceAt", locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    public void allowSugarCaneToGrowOnIce(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir, BlockState blockState, BlockPos blockPos, Iterator var6, Direction direction, BlockState blockState2) {
        if(!FabricSeasons.CONFIG.shouldIceBreakSugarCane() && blockState2.isOf(Blocks.ICE)) {
            cir.setReturnValue(true);
        }
    }

}
