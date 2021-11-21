package com.yurisuika.seasons.mixin;

import com.yurisuika.seasons.Seasons;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {

    @Shadow public abstract Block getBlock();

    @Inject(at = @At("HEAD"), method = "isOf", cancellable = true)
    public void is(Block block, CallbackInfoReturnable<Boolean> infoReturnable) {
        if(getBlock() == Seasons.ORIGINAL_ICE && block == Blocks.ICE) {
            infoReturnable.setReturnValue(true);
        }
        if(getBlock() == Seasons.ORIGINAL_SNOW && block == Blocks.SNOW) {
            infoReturnable.setReturnValue(true);
        }
    }

}
