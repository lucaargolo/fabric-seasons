package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.resources.FoliageSeasonColors;
import io.github.lucaargolo.seasons.resources.GrassSeasonColors;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockColors.class)
public class BlockColorsMixin {

    @Inject(method = "method_1693", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/color/world/GrassColors;getColor(DD)I"), cancellable = true)
    private static void injectGrassColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex, CallbackInfoReturnable<Integer> info) {
        info.setReturnValue(GrassSeasonColors.getColor(FabricSeasons.getCurrentSeason(), 0.5D, 1.0D));
    }

    @Inject(method = "method_1695", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/color/world/FoliageColors;getSpruceColor()I"), cancellable = true)
    private static void injectSpruceColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex, CallbackInfoReturnable<Integer> info) {
        info.setReturnValue(FoliageSeasonColors.getSpruceColor(FabricSeasons.getCurrentSeason()));
    }

    @Inject(method = "method_1687", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/color/world/FoliageColors;getBirchColor()I"), cancellable = true)
    private static void injectBirchColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex, CallbackInfoReturnable<Integer> info) {
        info.setReturnValue(FoliageSeasonColors.getBirchColor(FabricSeasons.getCurrentSeason()));
    }

    @Inject(method = "method_1692", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/color/world/FoliageColors;getDefaultColor()I"), cancellable = true)
    private static void injectFoliageColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex, CallbackInfoReturnable<Integer> info) {
        info.setReturnValue(FoliageSeasonColors.getDefaultColor(FabricSeasons.getCurrentSeason()));
    }

}
