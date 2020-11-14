package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin({CropBlock.class, CocoaBlock.class, StemBlock.class})
public abstract class FertilizeableMixin extends Block implements Fertilizable {

    public FertilizeableMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    public void randomTickInject(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if(FabricSeasons.MOD_CONFIG.isSeasonMessingCrops()) {
            Identifier cropIdentifier = Registry.BLOCK.getId(state.getBlock());
            float multiplier = FabricSeasons.MOD_CONFIG.getSeasonCropMultiplier(cropIdentifier, FabricSeasons.getCurrentSeason(world));
            float k = multiplier - 1f;
            if(k > 0 && random.nextFloat() <= k) {
                this.randomTick(state, world, pos, random);
            }
            if(k < 0 && random.nextFloat() <= (k+1f)) {
                ci.cancel();
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "grow", cancellable = true)
    public void growInject(ServerWorld world, Random random, BlockPos pos, BlockState state, CallbackInfo ci) {
        if(FabricSeasons.MOD_CONFIG.isSeasonMessingBonemeal()) {
            Identifier cropIdentifier = Registry.BLOCK.getId(state.getBlock());
            float multiplier = FabricSeasons.MOD_CONFIG.getSeasonCropMultiplier(cropIdentifier, FabricSeasons.getCurrentSeason(world));
            float k = multiplier - 1f;
            if(k > 0 && random.nextFloat() <= k) {
                this.grow(world, random, pos, state);
            }
            if(k < 0 && random.nextFloat() <= (k+1f)) {
                ci.cancel();
            }
        }
    }

}
