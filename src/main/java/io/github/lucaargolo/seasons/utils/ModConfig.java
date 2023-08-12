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

    private static class SeasonLength {
        private int springLength = 672000;
        private int summerLength = 672000;
        private int fallLength = 672000;
        private int winterLength = 672000;

    }

    private SeasonLength seasonLength = new SeasonLength();

    private SeasonLock seasonLock = new SeasonLock();


    private List<String> dimensionAllowlist = List.of(
            "minecraft:overworld"
    );
    
    private boolean doTemperatureChanges = true;

    private boolean shouldSnowyBiomesMeltInSummer = true;

    private boolean shouldIceNearWaterMelt = false;

    private List<String> biomeDenylist = List.of(
            "terralith:glacial_chasm"
    );

    private boolean isSeasonTiedWithSystemTime = false;
    
    private boolean isInNorthHemisphere = true;

    private boolean isSeasonMessingCrops = true;
    private boolean isSeasonMessingBonemeal = false;
    private boolean doCropsGrowsNormallyUnderground = false;

    private boolean doAnimalsBreedInWinter = true;

    private boolean notifyCompat = true;

    private boolean debugCommandEnabled = false;

    public boolean shouldNotifyCompat() {
        return notifyCompat;
    }

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

    public boolean shouldSnowyBiomesMeltInSummer() {
        return shouldSnowyBiomesMeltInSummer;
    }

    public boolean shouldIceNearWaterMelt() {
        return shouldIceNearWaterMelt;
    }

    public int getSpringLength() {
        return seasonLength.springLength;
    }

    public int getSummerLength() {
        return seasonLength.summerLength;
    }

    public int getFallLength() {
        return seasonLength.fallLength;
    }

    public int getWinterLength() {
        return seasonLength.winterLength;
    }

    public int getYearLength() {
        return seasonLength.springLength + seasonLength.summerLength + seasonLength.fallLength + seasonLength.winterLength;
    }

    @Deprecated
    public int getSeasonLength() {
        return getSpringLength();
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

    public boolean isDebugCommandEnabled() { return debugCommandEnabled; }

}
