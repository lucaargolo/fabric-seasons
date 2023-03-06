package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.resources.CropConfigs;
import io.github.lucaargolo.seasons.utils.GreenhouseCache;
import io.github.lucaargolo.seasons.utils.SeasonalFertilizable;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LightType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({CropBlock.class, CocoaBlock.class, StemBlock.class, SaplingBlock.class, SweetBerryBushBlock.class})
public abstract class FertilizableMixin extends Block implements Fertilizable, SeasonalFertilizable {

    private boolean seasons$shouldInject = true;

    public FertilizableMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    public void randomTickInject(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        boolean shouldGrowNormally = GreenhouseCache.test(world, pos) || (world.getLightLevel(LightType.SKY, pos) == 0 && FabricSeasons.CONFIG.doCropsGrowsNormallyUnderground());
        if(!shouldGrowNormally && FabricSeasons.CONFIG.isSeasonMessingCrops() && seasons$shouldInject) {
            Identifier cropIdentifier = Registry.BLOCK.getId(state.getBlock());
            float multiplier = CropConfigs.getSeasonCropMultiplier(cropIdentifier, FabricSeasons.getCurrentSeason(world));
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
        boolean shouldGrowNormally = GreenhouseCache.test(world, pos) || (world.getLightLevel(LightType.SKY, pos) == 0 && FabricSeasons.CONFIG.doCropsGrowsNormallyUnderground());
        if(!shouldGrowNormally && FabricSeasons.CONFIG.isSeasonMessingBonemeal() && seasons$shouldInject) {
            Identifier cropIdentifier = Registry.BLOCK.getId(state.getBlock());
            float multiplier = CropConfigs.getSeasonCropMultiplier(cropIdentifier, FabricSeasons.getCurrentSeason(world));
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
