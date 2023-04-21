package io.github.lucaargolo.seasons.utils;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal", "MismatchedQueryAndUpdateOfCollection", "unused"})
public class ModConfig {

    private static class SeasonLock {
        private boolean isSeasonLocked = false;
        private Season lockedSeason = Season.SPRING;
    }
    private int seasonLength = 672000;

    private SeasonLock seasonLock = new SeasonLock();

    private List<String> dimensionAllowlist = List.of(
            "minecraft:overworld"
    );
    
    private boolean doTemperatureChanges = true;

    private List<String> biomeDenylist = List.of(
            "terralith:glacial_chasm"
    );

    private boolean isSeasonTiedWithSystemTime = false;
    
    private boolean isInNorthHemisphere = true;

    private boolean isSeasonMessingCrops = true;
    private boolean isSeasonMessingBonemeal = false;
    private boolean doCropsGrowsNormallyUnderground = false;

    private boolean doAnimalsBreedInWinter = true;

    public boolean doAnimalsBreedInWinter() {
        return doAnimalsBreedInWinter;
    }

    public boolean isSeasonMessingCrops() {
        return isSeasonMessingCrops;
    }

    public boolean isSeasonMessingBonemeal() {
        return isSeasonMessingBonemeal;
    }

    public boolean doCropsGrowsNormallyUnderground() {
        return doCropsGrowsNormallyUnderground;
    }

    public boolean doTemperatureChanges(Identifier biomeId) {
        return doTemperatureChanges && !biomeDenylist.contains(biomeId.toString());
    }

    public int getSeasonLength() {
        return seasonLength;
    }

    public boolean isSeasonLocked() {
        return seasonLock.isSeasonLocked;
    }

    public Season getLockedSeason() {
        return seasonLock.lockedSeason;
    }

    public boolean isValidInDimension(RegistryKey<World> dimension) {
        return dimensionAllowlist.contains(dimension.getValue().toString());
    }

    public boolean isSeasonTiedWithSystemTime() {
        return isSeasonTiedWithSystemTime;
    }

    public boolean isInNorthHemisphere() {
        return isInNorthHemisphere;
    }

}
