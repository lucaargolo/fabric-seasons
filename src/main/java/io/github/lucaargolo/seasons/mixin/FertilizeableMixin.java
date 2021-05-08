package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin({CropBlock.class, CocoaBlock.class, StemBlock.class})
public abstract class FertilizeableMixin extends Block implements Fertilizable {

    private boolean seasons$shouldInject = true;

    public FertilizeableMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    public void randomTickInject(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if(FabricSeasons.CONFIG.isSeasonMessingCrops() && seasons$shouldInject) {
            Identifier cropIdentifier = Registry.BLOCK.getId(state.getBlock());
            float multiplier = FabricSeasons.CONFIG.getSeasonCropMultiplier(cropIdentifier, FabricSeasons.getCurrentSeason(world));
            while(multiplier > 0f) {
                float rand = random.nextFloat();
                if(multiplier >= rand) {
                    seasons$shouldInject = false;
                    this.randomTick(state, world, pos, random);
                    multiplier -= 1f;
                }
            }
            seasons$shouldInject = true;
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "grow", cancellable = true)
    public void growInject(ServerWorld world, Random random, BlockPos pos, BlockState state, CallbackInfo ci) {
        if(FabricSeasons.CONFIG.isSeasonMessingBonemeal() && seasons$shouldInject) {
            Identifier cropIdentifier = Registry.BLOCK.getId(state.getBlock());
            float multiplier = FabricSeasons.CONFIG.getSeasonCropMultiplier(cropIdentifier, FabricSeasons.getCurrentSeason(world));
            while(multiplier > 0f) {
                float rand = random.nextFloat();
                if(multiplier >= rand) {
                    seasons$shouldInject = false;
                    this.grow(world, random, pos, state);
                    multiplier -= 1f;
                }
            }
            seasons$shouldInject = true;
            ci.cancel();
        }
    }

}
