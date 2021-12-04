package com.yurisuika.seasons.mixin;

import com.yurisuika.seasons.Seasons;
import com.yurisuika.seasons.colors.SeasonFoliageColors;
import net.minecraft.client.color.world.FoliageColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = FoliageColors.class, priority = 2000)
public class FoliageColorsMixin {

    /**
     * @author
     * Seasons
     * @reason
     * Spruce Color OptiFine Compatibility
     */
    @Overwrite()
    public static int getSpruceColor() {
        return SeasonFoliageColors.getSpruceColor(Seasons.getCurrentSeason());
    }

    /**
     * @author
     * Seasons
     * @reason
     * Birch Color OptiFine Compatibility
     */
    @Overwrite()
    public static int getBirchColor() {
        return SeasonFoliageColors.getBirchColor(Seasons.getCurrentSeason());
    }

    /**
     * @author
     * Seasons
     * @reason
     * Default Color OptiFine Compatibility
     */
    @Overwrite()
    public static int getDefaultColor() {
        return SeasonFoliageColors.getDefaultColor(Seasons.getCurrentSeason());
    }

}
