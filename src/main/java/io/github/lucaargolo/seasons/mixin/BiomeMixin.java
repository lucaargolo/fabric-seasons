package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.mixed.BiomeMixed;
import io.github.lucaargolo.seasons.resources.FoliageSeasonColors;
import io.github.lucaargolo.seasons.resources.GrassSeasonColors;
import io.github.lucaargolo.seasons.utils.ColorsCache;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Biome.class)
public abstract class BiomeMixin implements BiomeMixed {

    @Shadow @Final public Biome.Weather weather;
    @Shadow @Final private BiomeEffects effects;

    @Shadow protected abstract int getDefaultGrassColor();

    @Shadow protected abstract int getDefaultFoliageColor();

    private Biome.Weather originalWeather;

    @SuppressWarnings({"ConstantConditions", "removal", "OptionalAssignedToNull"})
    @Environment(EnvType.CLIENT)
    @Inject(at = @At("TAIL"), method = "getGrassColorAt", cancellable = true)
    public void getSeasonGrassColor(double x, double z, CallbackInfoReturnable<Integer> cir) {
        Biome biome = (Biome) ((Object) this);
        Optional<Integer> overridedColor;
        if(ColorsCache.hasGrassCache(biome)) {
            overridedColor = ColorsCache.getGrassCache(biome);
        }else {
            overridedColor = effects.getGrassColor();
            World world = MinecraftClient.getInstance().world;
            if(world != null) {
                Identifier biomeIdentifier = world.getRegistryManager().get(RegistryKeys.BIOME).getId(biome);
                Optional<Integer> seasonGrassColor = GrassSeasonColors.getSeasonGrassColor(biome, biomeIdentifier, FabricSeasons.getCurrentSeason());
                if(seasonGrassColor.isPresent()) {
                    overridedColor = seasonGrassColor;
                }
            }
            ColorsCache.createGrassCache(biome, overridedColor);
        }
        if(effects.getGrassColorModifier() == BiomeEffects.GrassColorModifier.SWAMP) {
            int swampColor1 = GrassSeasonColors.getSwampColor1(FabricSeasons.getCurrentSeason());
            int swampColor2 = GrassSeasonColors.getSwampColor2(FabricSeasons.getCurrentSeason());

            double d = Biome.FOLIAGE_NOISE.sample(x * 0.0225D, z * 0.0225D, false);
            cir.setReturnValue(d < -0.1D ? swampColor1 : swampColor2);
        }else if(overridedColor != null){
            Integer integer = overridedColor.orElseGet(this::getDefaultGrassColor);
            cir.setReturnValue(effects.getGrassColorModifier().getModifiedGrassColor(x, z, integer));
        }
    }

    @SuppressWarnings({"ConstantConditions", "OptionalAssignedToNull"})
    @Environment(EnvType.CLIENT)
    @Inject(at = @At("TAIL"), method = "getFoliageColor", cancellable = true)
    public void getSeasonFoliageColor(CallbackInfoReturnable<Integer> cir) {
        Biome biome = (Biome) ((Object) this);
        Optional<Integer> overridedColor;
        if(ColorsCache.hasFoliageCache(biome)) {
            overridedColor = ColorsCache.getFoliageCache(biome);
        }else{
            overridedColor = effects.getFoliageColor();
            World world = MinecraftClient.getInstance().world;
            if(world != null) {
                Identifier biomeIdentifier = world.getRegistryManager().get(RegistryKeys.BIOME).getId(biome);
                Optional<Integer> seasonFoliageColor = FoliageSeasonColors.getSeasonFoliageColor(biome, biomeIdentifier, FabricSeasons.getCurrentSeason());
                if(seasonFoliageColor.isPresent()) {
                    overridedColor = seasonFoliageColor;
                }
            }
            ColorsCache.createFoliageCache(biome, overridedColor);
        }
        if(overridedColor != null) {
            Integer integer = overridedColor.orElseGet(this::getDefaultFoliageColor);
            cir.setReturnValue(integer);
        }
    }

    @Environment(EnvType.CLIENT)
    @Inject(at = @At("HEAD"), method = "getDefaultFoliageColor", cancellable = true)
    public void getSeasonDefaultFolliageColor(CallbackInfoReturnable<Integer> cir) {
        if(this.originalWeather != null) {
            double originalTemperature = MathHelper.clamp(this.originalWeather.temperature(), 0.0F, 1.0F);
            double originalDownfall = MathHelper.clamp(this.originalWeather.downfall(), 0.0F, 1.0F);
            cir.setReturnValue(FoliageSeasonColors.getColor(FabricSeasons.getCurrentSeason(), originalTemperature, originalDownfall));
        }else{
            double temperature = MathHelper.clamp(this.weather.temperature(), 0.0F, 1.0F);
            double downfall = MathHelper.clamp(this.weather.downfall(), 0.0F, 1.0F);
            cir.setReturnValue(FoliageSeasonColors.getColor(FabricSeasons.getCurrentSeason(), temperature, downfall));
        }
    }

    @Environment(EnvType.CLIENT)
    @Inject(at = @At("HEAD"), method = "getDefaultGrassColor", cancellable = true)
    public void getSeasonDefaultGrassColor(CallbackInfoReturnable<Integer> cir) {
        if(this.originalWeather != null) {
            double d = MathHelper.clamp(this.originalWeather.temperature(), 0.0F, 1.0F);
            double e = MathHelper.clamp(this.originalWeather.downfall(), 0.0F, 1.0F);
            cir.setReturnValue(GrassSeasonColors.getColor(FabricSeasons.getCurrentSeason(), d, e));
        }else{
            double d = MathHelper.clamp(this.weather.temperature(), 0.0F, 1.0F);
            double e = MathHelper.clamp(this.weather.downfall(), 0.0F, 1.0F);
            cir.setReturnValue(GrassSeasonColors.getColor(FabricSeasons.getCurrentSeason(), d, e));
        }
    }

    @Override
    public Biome.Weather getOriginalWeather() {
        return this.originalWeather;
    }

    @Override
    public void setOriginalWeather(Biome.Weather originalWeather) {
        this.originalWeather = originalWeather;
    }
}
