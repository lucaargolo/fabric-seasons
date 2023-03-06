package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.mixed.BiomeMixed;
import io.github.lucaargolo.seasons.resources.FoliageSeasonColors;
import io.github.lucaargolo.seasons.utils.Season;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BiomeColors.class)
public class BiomeColorsMixin {

    @SuppressWarnings("ConstantValue")
    @Inject(at = @At("RETURN"), method = "method_23791", cancellable = true)
    private static void enhanceFallColors(Biome biome, double x, double z, CallbackInfoReturnable<Integer> cir) {
        Season season = FabricSeasons.getCurrentSeason();
        if(season == Season.FALL && ((Object) biome) instanceof BiomeMixed mixed) {
            double d = MathHelper.clamp(mixed.getOriginalWeather().temperature, 0.0F, 1.0F);
            double e = MathHelper.clamp(mixed.getOriginalWeather().downfall, 0.0F, 1.0F);
            int fallFoliageColor = FoliageSeasonColors.getColor(Season.FALL, d, e);
            if(cir.getReturnValue() == fallFoliageColor) {
                double sample = Biome.FOLIAGE_NOISE.sample(x * 0.0225, z * 0.0225, false);
                cir.setReturnValue(sample < 0.25 ? fallFoliageColor : FoliageSeasonColors.getColor(Season.FALL, 0.85, 0.9));
            }
        }
    }

}
