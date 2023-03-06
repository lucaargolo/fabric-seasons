package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.colors.SeasonFoliageColors;
import io.github.lucaargolo.seasons.colors.SeasonGrassColors;
import io.github.lucaargolo.seasons.mixed.BiomeMixed;
import io.github.lucaargolo.seasons.utils.ColorsCache;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Biome.class)
public class BiomeMixin implements BiomeMixed {

    private Biome.Weather originalWeather;

    @SuppressWarnings("ConstantConditions")
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/BiomeEffects;getGrassColor()Ljava/util/Optional;"), method = "getGrassColorAt")
    public Optional<Integer> getSeasonGrassColor(BiomeEffects effects) {
        Biome biome = (Biome) ((Object) this);
        if(ColorsCache.hasGrassCache(biome)) {
            return ColorsCache.getGrassCache(biome);
        }else {
            Optional<Integer> returnColor = effects.getGrassColor();
            World world = MinecraftClient.getInstance().world;
            if(world != null) {
                Identifier biomeIdentifier = world.getRegistryManager().get(Registry.BIOME_KEY).getId(biome);
                Optional<Integer> seasonGrassColor = FabricSeasons.CONFIG.getSeasonGrassColor(biome, biomeIdentifier, FabricSeasons.getCurrentSeason());
                if(seasonGrassColor.isPresent()) {
                    returnColor = seasonGrassColor;
                }
            }
            if(returnColor == null) {
                System.out.println("huh");
            }
            ColorsCache.createGrassCache(biome, returnColor);
            return returnColor;
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/BiomeEffects;getFoliageColor()Ljava/util/Optional;"), method = "getFoliageColor")
    public Optional<Integer> getSeasonFoliageColor(BiomeEffects effects) {
        Biome biome = (Biome) ((Object) this);
        if(ColorsCache.hasFoliageCache(biome)) {
            return ColorsCache.getFoliageCache(biome);
        }else{
            Optional<Integer> returnColor = effects.getFoliageColor();
            World world = MinecraftClient.getInstance().world;
            if(world != null) {
                Identifier biomeIdentifier = world.getRegistryManager().get(Registry.BIOME_KEY).getId(biome);
                Optional<Integer> seasonFoliageColor = FabricSeasons.CONFIG.getSeasonFoliageColor(biome, biomeIdentifier, FabricSeasons.getCurrentSeason());
                if(seasonFoliageColor.isPresent()) {
                    returnColor = seasonFoliageColor;
                }
            }
            ColorsCache.createFoliageCache(biome, returnColor);
            return returnColor;
        }

    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/BiomeEffects$GrassColorModifier;getModifiedGrassColor(DDI)I"), method = "getGrassColorAt")
    public int getSeasonModifiedGrassColor(BiomeEffects.GrassColorModifier gcm, double x, double z, int color) {
        if(gcm == BiomeEffects.GrassColorModifier.SWAMP) {
            int swampColor1 = FabricSeasons.CONFIG.getMinecraftSwampGrass1().getColor(FabricSeasons.getCurrentSeason());
            int swampColor2 = FabricSeasons.CONFIG.getMinecraftSwampGrass2().getColor(FabricSeasons.getCurrentSeason());

            double d = Biome.FOLIAGE_NOISE.sample(x * 0.0225D, z * 0.0225D, false);
            return d < -0.1D ? swampColor1 : swampColor2;
        }else{
            return gcm.getModifiedGrassColor(x, z, color);
        }
    }

    @Inject(at = @At("HEAD"), method = "getDefaultFoliageColor", cancellable = true)
    public void getSeasonDefaultFolliageColor(CallbackInfoReturnable<Integer> cir) {
        if(this.originalWeather != null) {
            double originalTemperature = MathHelper.clamp(this.originalWeather.temperature, 0.0F, 1.0F);
            double originalDownfall = MathHelper.clamp(this.originalWeather.downfall, 0.0F, 1.0F);
            cir.setReturnValue(SeasonFoliageColors.getColor(FabricSeasons.getCurrentSeason(), originalTemperature, originalDownfall));
        }
    }

    @Inject(at = @At("HEAD"), method = "getDefaultGrassColor", cancellable = true)
    public void getSeasonDefaultGrassColor(CallbackInfoReturnable<Integer> cir) {
        if(this.originalWeather != null) {
            double d = MathHelper.clamp(this.originalWeather.temperature, 0.0F, 1.0F);
            double e = MathHelper.clamp(this.originalWeather.downfall, 0.0F, 1.0F);
            cir.setReturnValue(SeasonGrassColors.getColor(FabricSeasons.getCurrentSeason(), d, e));
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
