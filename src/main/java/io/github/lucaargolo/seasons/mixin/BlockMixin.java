package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockMixin {

    @SuppressWarnings("ConstantConditions")
    @Inject(at = @At("HEAD"), method = "is", cancellable = true)
    public void is(Block block, CallbackInfoReturnable<Boolean> infoReturnable) {
        Block b = (Block)((Object) this);
        if(b == FabricSeasons.ORIGINAL_ICE && block == Blocks.ICE) {
            infoReturnable.setReturnValue(true);
        }
        if(b == FabricSeasons.ORIGINAL_SNOW && block == Blocks.SNOW) {
            infoReturnable.setReturnValue(true);
        }
    }

}
