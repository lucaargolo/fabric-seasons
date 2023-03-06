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

    public static class HardcodedColors {
        private int springColor;
        private int summerColor;
        private int fallColor;
        private int winterColor;

        HardcodedColors(int springColor, int summerColor, int fallColor, int winterColor) {
            this.springColor = springColor;
            this.summerColor = summerColor;
            this.fallColor = fallColor;
            this.winterColor = winterColor;
        }

        public int getColor(Season season) {
            return switch (season) {
                case SPRING -> springColor;
                case SUMMER -> summerColor;
                case FALL -> fallColor;
                case WINTER -> winterColor;
            };
        }
    }

    private static class BiomeColors {

        private String biomeIdentifier;
        private HardcodedColors colors;

        BiomeColors() {
            this.biomeIdentifier = "";
            this.colors = new HardcodedColors(0, 0, 0,0);
        }

        BiomeColors(String biomeIdentifier, int springColor, int summerColor, int fallColor, int winterColor) {
            this.biomeIdentifier = biomeIdentifier;
            this.colors = new HardcodedColors(springColor, summerColor, fallColor, winterColor);
        }

    }

    
    private int seasonLength = 672000;

    private SeasonLock seasonLock = new SeasonLock();

    private List<String> dimensionWhitelist = List.of("minecraft:overworld");
    
    private boolean doTemperatureChanges = true;
    
    private boolean isSeasonTiedWithSystemTime = false;
    
    private boolean isInNorthHemisphere = true;

    private HardcodedColors minecraftDefaultFoliage = new HardcodedColors(0x48B518, 0x4CE00B, 0xD2CF1E, 0xC6DFB6);
    private HardcodedColors minecraftSpruceFoliage = new HardcodedColors(0x619961, 0x619961, 0x619961, 0x619961);
    private HardcodedColors minecraftBirchFoliage = new HardcodedColors(0x80A755, 0x81B844, 0xD66800, 0x665026);
    private HardcodedColors minecraftSwampGrass1 = new HardcodedColors(0x4C763C, 0x4C763C, 0x4C763C, 0x4C763C);
    private HardcodedColors minecraftSwampGrass2 = new HardcodedColors(0x6A7039, 0x6A7039, 0x6A7039, 0x6A7039);

    private boolean isDefaultHSBShiftEnabled = false;

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

    private final List<BiomeColors> foliageColorList = new ArrayList<>();
    private final List<BiomeColors> grassColorList = new ArrayList<>();

    private int getShiftedColor(Season season, int defaultColor) {
        Color initialColor = new Color(defaultColor);
        HSBShift hueShift = defaultHSBShift.getHSBShift(season);
        Color finalColor = ColorHelper.changeHueSatBri(initialColor, hueShift.hue, hueShift.saturation, hueShift.brightness);
        return finalColor.getRGB();
    }

    public Optional<Integer> getSeasonFoliageColor(Biome biome, Identifier biomeIdentifier, Season season) {
        Optional<BiomeColors> colors = foliageColorList.stream().filter(it -> it.biomeIdentifier.equals(biomeIdentifier.toString())).findFirst();
        Optional<Integer> color = colors.map(biomeColors -> biomeColors.colors.getColor(season));
        if(color.isEmpty() && isDefaultHSBShiftEnabled) {
            Optional<Integer> defaultColor = biome.getEffects().getFoliageColor();
            if(defaultColor.isPresent()) {
                return Optional.of(getShiftedColor(season, defaultColor.get()));
            }
        }
        return color;
    }


    public Optional<Integer> getSeasonGrassColor(Biome biome, Identifier biomeIdentifier, Season season) {
        Optional<BiomeColors> colors = grassColorList.stream().filter(it -> it.biomeIdentifier.equals(biomeIdentifier.toString())).findFirst();
        Optional<Integer> color = colors.map(biomeColors -> biomeColors.colors.getColor(season));
        if(color.isEmpty() && isDefaultHSBShiftEnabled) {
            Optional<Integer> defaultColor = biome.getEffects().getGrassColor();
            if(defaultColor.isPresent()) {
                return Optional.of(getShiftedColor(season, defaultColor.get()));
            }
        }
        return color;
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

    public HardcodedColors getMinecraftDefaultFoliage() {
        return minecraftDefaultFoliage;
    }

    public HardcodedColors getMinecraftSpruceFoliage() {
        return minecraftSpruceFoliage;
    }

    public HardcodedColors getMinecraftBirchFoliage() {
        return minecraftBirchFoliage;
    }

    public HardcodedColors getMinecraftSwampGrass1() {
        return minecraftSwampGrass1;
    }

    public HardcodedColors getMinecraftSwampGrass2() {
        return minecraftSwampGrass2;
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
