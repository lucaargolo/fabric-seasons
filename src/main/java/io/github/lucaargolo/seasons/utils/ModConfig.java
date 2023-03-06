package io.github.lucaargolo.seasons.utils;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal", "MismatchedQueryAndUpdateOfCollection", "unused"})
public class ModConfig {

    private static class SeasonLock {
        private boolean isSeasonLocked = false;
        private Season lockedSeason = Season.SPRING;
    }
    private int seasonLength = 672000;

    private SeasonLock seasonLock = new SeasonLock();

    private List<String> dimensionWhitelist = List.of("minecraft:overworld");
    
    private boolean doTemperatureChanges = true;
    
    private boolean isSeasonTiedWithSystemTime = false;
    
    private boolean isInNorthHemisphere = true;


    private boolean isDefaultHSBShiftEnabled = false;

    public boolean isDefaultHSBShiftEnabled() {
        return isDefaultHSBShiftEnabled;
    }

    private static class HSBShift {
        private float hue;
        private float saturation;
        private float brightness;

        public HSBShift(float hue, float saturation, float brightness) {
            this.hue = hue;
            this.saturation = saturation;
            this.brightness = brightness;
        }
    }

    private static class DefaultHSBShift {
        private HSBShift springHSBShift = new HSBShift(0f, 100f, 0f);
        private HSBShift summerHSBShift = new HSBShift(0f, 150f, -10f);
        private HSBShift fallHSBShift = new HSBShift(-65f, 125f, -15f);
        private HSBShift winterHSBShift = new HSBShift(-65f, 80f, -40f);

        public HSBShift getHSBShift(Season season) {
            return switch (season) {
                case SPRING -> springHSBShift;
                case SUMMER -> summerHSBShift;
                case FALL -> fallHSBShift;
                case WINTER -> winterHSBShift;
            };
        }

    }

    
    private DefaultHSBShift defaultHSBShift = new DefaultHSBShift();

    public int getShiftedColor(Season season, int defaultColor) {
        Color initialColor = new Color(defaultColor);
        HSBShift hueShift = defaultHSBShift.getHSBShift(season);
        Color finalColor = ColorHelper.changeHueSatBri(initialColor, hueShift.hue, hueShift.saturation, hueShift.brightness);
        return finalColor.getRGB();
    }

    private static class DefaultCropConfig {

        private float springModifier, summerModifier, fallModifier, winterModifier;

        public DefaultCropConfig(float springModifier, float summerModifier, float fallModifier, float winterModifier) {
            this.springModifier = springModifier;
            this.summerModifier = summerModifier;
            this.fallModifier = fallModifier;
            this.winterModifier = winterModifier;
        }

        public float getModifier(Season season) {
            return switch (season) {
                case SPRING -> springModifier;
                case SUMMER -> summerModifier;
                case FALL -> fallModifier;
                case WINTER -> winterModifier;
            };
        }
    }

    private static class CropConfig {

        private String cropIdentifier;
        private float springModifier, summerModifier, fallModifier, winterModifier;

        public CropConfig() {
            this.cropIdentifier = "";
            this.springModifier = 1.0f;
            this.summerModifier = 0.8f;
            this.fallModifier = 0.6f;
            this.winterModifier = 0f;
        }

        public CropConfig(String cropIdentifier, float springModifier, float summerModifier, float fallModifier, float winterModifier) {
            this.cropIdentifier = cropIdentifier;
            this.springModifier = springModifier;
            this.summerModifier = summerModifier;
            this.fallModifier = fallModifier;
            this.winterModifier = winterModifier;
        }

        public float getModifier(Season season) {
            return switch (season) {
                case SPRING -> springModifier;
                case SUMMER -> summerModifier;
                case FALL -> fallModifier;
                case WINTER -> winterModifier;
            };
        }

    }

    private boolean isSeasonMessingCrops = true;
    private boolean isSeasonMessingBonemeal = false;
    private boolean doCropsGrowsNormallyUnderground = true;

    private DefaultCropConfig defaultCropConfig = new DefaultCropConfig(1.0f, 0.8f, 0.6f, 0f);

    private final List<CropConfig> cropConfigs = new ArrayList<>();

    public float getSeasonCropMultiplier(Identifier cropIdentifier, Season season) {
        Optional<CropConfig> config = cropConfigs.stream().filter(it -> it.cropIdentifier.equals(cropIdentifier.toString())).findFirst();
        return config.map(cropConfig -> cropConfig.getModifier(season)).orElse(defaultCropConfig.getModifier(season));
    }

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

    public boolean doTemperatureChanges() {
        return doTemperatureChanges;
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
        return dimensionWhitelist.contains(dimension.getValue().toString());
    }

    public boolean isSeasonTiedWithSystemTime() {
        return isSeasonTiedWithSystemTime;
    }

    public boolean isInNorthHemisphere() {
        return isInNorthHemisphere;
    }

}
