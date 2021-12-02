package com.yurisuika.seasons.utils;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
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
            switch (season) {
                case SPRING:
                    return springColor;
                case SUMMER:
                    return summerColor;
                case FALL:
                    return fallColor;
                case WINTER:
                    return winterColor;
            }
            return springColor;
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


    private int seasonLength = 768000;

    private SeasonLock seasonLock = new SeasonLock();

    private List<String> dimensionWhitelist = List.of("minecraft:overworld");

    private boolean doTemperatureChanges = true;

    private boolean isSeasonTiedWithSystemTime = false;

    private boolean isInNorthHemisphere = true;

    private HardcodedColors minecraftDefaultFoliage = new HardcodedColors(0x668450, 0x757F55, 0x845C50, 0x766B5D);
    private HardcodedColors minecraftSpruceFoliage = new HardcodedColors(0x5D7B53, 0x647855, 0x6A7757, 0x647855);
    private HardcodedColors minecraftBirchFoliage = new HardcodedColors(0x7A9D69, 0x8A986E, 0xA58260, 0x8F8776);
    private HardcodedColors minecraftSwampGrass1 = new HardcodedColors(0x72774C, 0x75734E, 0x736B50, 0x75734E);
    private HardcodedColors minecraftSwampGrass2 = new HardcodedColors(0x7C8054, 0x7E7B56, 0x7C7358, 0x7E7B56);
    private HardcodedColors minecraftBadlandsGrass = new HardcodedColors(0xB1B37D, 0xA7B781, 0x8E8D64, 0xA7B781);
    private HardcodedColors minecraftBadlandsFoliage = new HardcodedColors(0x888E64, 0x8B8E64, 0x8E8264, 0x8B8E64);

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
            switch (season) {
                case SPRING:
                    return springHSBShift;
                case SUMMER:
                    return summerHSBShift;
                case FALL:
                    return fallHSBShift;
                case WINTER:
                    return winterHSBShift;
            }
            return springHSBShift;
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
        if(!color.isPresent() && isDefaultHSBShiftEnabled) {
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
        if(!color.isPresent() && isDefaultHSBShiftEnabled) {
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
            switch (season) {
                case SPRING:
                    return springModifier;
                case SUMMER:
                    return summerModifier;
                case FALL:
                    return fallModifier;
                case WINTER:
                    return winterModifier;
            }
            return springModifier;
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
            switch (season) {
                case SPRING:
                    return springModifier;
                case SUMMER:
                    return summerModifier;
                case FALL:
                    return fallModifier;
                case WINTER:
                    return winterModifier;
            }
            return springModifier;
        }

    }

    private boolean isSeasonMessingCrops = true;
    private boolean isSeasonMessingBonemeal = false;
    private boolean doCropsGrowsNormallyUnderground = false;

    private DefaultCropConfig defaultCropConfig = new DefaultCropConfig(1.0f, 0.8f, 0.6f, 0f);

    private final List<CropConfig> cropConfigs = new ArrayList<>();

    public float getSeasonCropMultiplier(Identifier cropIdentifier, Season season) {
        Optional<CropConfig> config = cropConfigs.stream().filter(it -> it.cropIdentifier.equals(cropIdentifier.toString())).findFirst();
        return config.map(cropConfig -> cropConfig.getModifier(season)).orElse(defaultCropConfig.getModifier(season));
    }

    private boolean doAnimalsBreedInWinter = false;

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

    public HardcodedColors getMinecraftBadlandsGrass() {
        return minecraftBadlandsGrass;
    }

    public HardcodedColors getMinecraftBadlandsFoliage() {
        return minecraftBadlandsFoliage;
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
