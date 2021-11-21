package com.yurisuika.seasons.mixin;

import com.yurisuika.seasons.Seasons;
import com.yurisuika.seasons.colors.SeasonFoliageColors;
import com.yurisuika.seasons.colors.SeasonGrassColors;
import com.yurisuika.seasons.utils.ColorsCache;
import com.yurisuika.seasons.utils.Season;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(Biome.class)
public class BiomeMixin {

    @Shadow @Final private Biome.Category category;

    @SuppressWarnings("ConstantConditions")
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/BiomeEffects;getGrassColor()Ljava/util/Optional;"), method = "getGrassColorAt")
    public Optional<Integer> getSeasonGrassColor(BiomeEffects effects) {
        Biome biome = (Biome) ((Object) this);
        if(ColorsCache.hasGrassCache(biome)) {
            return ColorsCache.getGrassCache(biome);
        }
        else if(category == Biome.Category.MESA) {
            Optional<Integer> returnColor = effects.getGrassColor();
            World world = MinecraftClient.getInstance().world;
            if(world != null) {
                Optional<Integer> badlandsGrassColor = Optional.of(Seasons.CONFIG.getMinecraftBadlandsGrass().getColor(Seasons.getCurrentSeason()));
                if(badlandsGrassColor.isPresent()) {
                    returnColor = badlandsGrassColor;
                }
            }
            ColorsCache.createFoliageCache(biome, returnColor);
            return returnColor;
        }
        else {
            Optional<Integer> returnColor = effects.getGrassColor();
            World world = MinecraftClient.getInstance().world;
            if(world != null) {
                Identifier biomeIdentifier = world.getRegistryManager().get(Registry.BIOME_KEY).getId(biome);
                Optional<Integer> seasonGrassColor = Seasons.CONFIG.getSeasonGrassColor(biome, biomeIdentifier, Seasons.getCurrentSeason());
                if(seasonGrassColor.isPresent()) {
                    returnColor = seasonGrassColor;
                }
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
        }
        else if(category == Biome.Category.MESA) {
            Optional<Integer> returnColor = effects.getFoliageColor();
            World world = MinecraftClient.getInstance().world;
            if(world != null) {
                Optional<Integer> badlandsFoliageColor = Optional.of(Seasons.CONFIG.getMinecraftBadlandsFoliage().getColor(Seasons.getCurrentSeason()));
                if(badlandsFoliageColor.isPresent()) {
                    returnColor = badlandsFoliageColor;
                }
            }
            ColorsCache.createFoliageCache(biome, returnColor);
            return returnColor;
        }
        else{
            Optional<Integer> returnColor = effects.getFoliageColor();
            World world = MinecraftClient.getInstance().world;
            if(world != null) {
                Identifier biomeIdentifier = world.getRegistryManager().get(Registry.BIOME_KEY).getId(biome);
                Optional<Integer> seasonFoliageColor = Seasons.CONFIG.getSeasonFoliageColor(biome, biomeIdentifier, Seasons.getCurrentSeason());
                if(seasonFoliageColor.isPresent()) {
                    returnColor = seasonFoliageColor;
                }
            }
            ColorsCache.createFoliageCache(biome, returnColor);
            return returnColor;
        }

    }

    @SuppressWarnings("removal")
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/BiomeEffects$GrassColorModifier;getModifiedGrassColor(DDI)I"), method = "getGrassColorAt")
    public int getSeasonModifiedGrassColor(BiomeEffects.GrassColorModifier gcm, double x, double z, int color) {
        if(gcm == BiomeEffects.GrassColorModifier.SWAMP) {
            int swampColor1 = Seasons.CONFIG.getMinecraftSwampGrass1().getColor(Seasons.getCurrentSeason());
            int swampColor2 = Seasons.CONFIG.getMinecraftSwampGrass2().getColor(Seasons.getCurrentSeason());

            double d = Biome.FOLIAGE_NOISE.sample(x * 0.0225D, z * 0.0225D, false);
            return d < -0.1D ? swampColor1 : swampColor2;
        }else{
            return gcm.getModifiedGrassColor(x, z, color);
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/color/world/FoliageColors;getColor(DD)I"), method = "getDefaultFoliageColor")
    public int getSeasonDefaultFoliageColor(double d, double e) {
        return SeasonFoliageColors.getColor(Seasons.getCurrentSeason(), d, e);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/color/world/GrassColors;getColor(DD)I"), method = "getDefaultGrassColor")
    public int getSeasonDefaultGrassColor(double d, double e) {
        return SeasonGrassColors.getColor(Seasons.getCurrentSeason(), d, e);
    }

}
