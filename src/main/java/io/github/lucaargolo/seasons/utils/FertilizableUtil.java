package io.github.lucaargolo.seasons.utils;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.resources.CropConfigs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class FertilizableUtil {

    private static boolean seasons$shouldInject = true;

    @SuppressWarnings("deprecation")
    public static <F extends Block & Fertilizable> void randomTickInject(F fertilizable, BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if(FabricSeasons.CONFIG.isSeasonMessingCrops() && seasons$shouldInject) {
            float multiplier = 1f + getMultiplier(world, pos, state);
            while(multiplier > 0f) {
                multiplier -= 1f;
                float rand = random.nextFloat();
                if(multiplier >= rand) {
                    seasons$shouldInject = false;
                    fertilizable.randomTick(state, world, pos, random);
                    multiplier -= 1f;
                }
            }
            seasons$shouldInject = true;
            ci.cancel();
        }
    }

    public static <F extends Block & Fertilizable> void growInject(F fertilizable, ServerWorld world, Random random, BlockPos pos, BlockState state, CallbackInfo ci) {
        if(FabricSeasons.CONFIG.isSeasonMessingBonemeal() && seasons$shouldInject) {
            float multiplier = 1f + getMultiplier(world, pos, state);
            while(multiplier > 0f) {
                multiplier -= 1f;
                float rand = random.nextFloat();
                if(multiplier >= rand) {
                    seasons$shouldInject = false;
                    fertilizable.grow(world, random, pos, state);
                }
            }
            seasons$shouldInject = true;
            ci.cancel();
        }
    }
    public static float getMultiplier(ServerWorld world, BlockPos pos, BlockState state) {
        float multiplier;
        if(FabricSeasons.CONFIG.doCropsGrowsNormallyUnderground() && world.getLightLevel(LightType.SKY, pos) == 0) {
            //Plant is not being affected by seasons
            multiplier = 1f;
        }else{
            //Plant is being affected by warmest greenhouse season or the current one
            Identifier cropIdentifier = Registries.BLOCK.getId(state.getBlock());
            Season warmestSeason = GreenhouseCache.test(world, pos);
            multiplier = CropConfigs.getSeasonCropMultiplier(cropIdentifier, warmestSeason);
        }
        return multiplier;
    }

}
