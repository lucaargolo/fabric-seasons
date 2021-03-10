package io.github.lucaargolo.seasons.utils;

import io.github.lucaargolo.seasons.FabricSeasons;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal", "MismatchedQueryAndUpdateOfCollection"})
@Config(name = FabricSeasons.MOD_ID)
public class ModConfig implements ConfigData {

    private static class SeasonLock {
        @ConfigEntry.Gui.Tooltip private boolean isSeasonLocked = false;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        private Season lockedSeason = Season.SPRING;
    }

    public static class HardcodedColors {
        @ConfigEntry.ColorPicker private int springColor;
        @ConfigEntry.ColorPicker private int summerColor;
        @ConfigEntry.ColorPicker private int fallColor;
        @ConfigEntry.ColorPicker private int winterColor;

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
        @ConfigEntry.Gui.TransitiveObject
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

    @ConfigEntry.Category("seasonBehaviour")
    @ConfigEntry.Gui.Tooltip(count = 3) private int seasonLength = 672000;
    @ConfigEntry.Category("seasonBehaviour")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    private SeasonLock seasonLock = new SeasonLock();
    @ConfigEntry.Category("seasonBehaviour")
    @ConfigEntry.Gui.Tooltip(count = 2) private boolean doTemperatureChanges = true;

    @ConfigEntry.Category("itemsAndBlocks")
    private boolean isSeasonCalendarEnabled = true;
    @ConfigEntry.Category("itemsAndBlocks")
    private boolean isSeasonDetectorEnabled = true;

    @ConfigEntry.Category("hardcodedColors")
    @ConfigEntry.Gui.CollapsibleObject private HardcodedColors minecraftDefaultFoliage = new HardcodedColors(0x48B518, 0x4CE00B, 0xE0990B, 0x755514);
    @ConfigEntry.Category("hardcodedColors")
    @ConfigEntry.Gui.CollapsibleObject private HardcodedColors minecraftSpruceFoliage = new HardcodedColors(0x619961, 0x619961, 0x619961, 0x619961);
    @ConfigEntry.Category("hardcodedColors")
    @ConfigEntry.Gui.CollapsibleObject private HardcodedColors minecraftBirchFoliage = new HardcodedColors(0x80A755, 0x81B844, 0xD66800, 0x665026);
    @ConfigEntry.Category("hardcodedColors")
    @ConfigEntry.Gui.CollapsibleObject private HardcodedColors minecraftSwampGrass1 = new HardcodedColors(0x4C763C, 0x4C763C, 0x4C763C, 0x4C763C);
    @ConfigEntry.Category("hardcodedColors")
    @ConfigEntry.Gui.CollapsibleObject private HardcodedColors minecraftSwampGrass2 = new HardcodedColors(0x6A7039, 0x6A7039, 0x6A7039, 0x6A7039);

    @ConfigEntry.Category("hardcodedColors")
    @ConfigEntry.Gui.Tooltip(count = 2)
    private boolean isDefaultHSBShiftEnabled = true;

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
        @ConfigEntry.Gui.CollapsibleObject()
        private HSBShift springHSBShift = new HSBShift(0f, 100f, 0f);
        @ConfigEntry.Gui.CollapsibleObject()
        private HSBShift summerHSBShift = new HSBShift(0f, 150f, -10f);
        @ConfigEntry.Gui.CollapsibleObject()
        private HSBShift fallHSBShift = new HSBShift(-65f, 125f, -15f);
        @ConfigEntry.Gui.CollapsibleObject()
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

    @ConfigEntry.Category("hardcodedColors")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    private DefaultHSBShift defaultHSBShift = new DefaultHSBShift();

    @ConfigEntry.Category("hardcodedColors")
    private final List<BiomeColors> foliageColorList = new ArrayList<>();
    @ConfigEntry.Category("hardcodedColors")
    private final List<BiomeColors> grassColorList = new ArrayList<>();

    public Optional<Integer> getSeasonFoliageColor(Biome biome, Identifier biomeIdentifier, Season season) {
        Optional<BiomeColors> colors = foliageColorList.stream().filter(it -> it.biomeIdentifier.equals(biomeIdentifier.toString())).findFirst();
        Optional<Integer> color = colors.map(biomeColors -> biomeColors.colors.getColor(season));
        if(!color.isPresent() && isDefaultHSBShiftEnabled) {
            Optional<Integer> defaultColor = biome.getEffects().getFoliageColor();
            if(defaultColor.isPresent()) {
                Color initialColor = new Color(defaultColor.get());
                HSBShift hueShift = defaultHSBShift.getHSBShift(season);
                Color finalColor = ColorHelper.changeHueSatBri(initialColor, hueShift.hue, hueShift.saturation, hueShift.brightness);
                return Optional.of(finalColor.getRGB());
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
                Color initialColor = new Color(defaultColor.get());
                HSBShift hueShift = defaultHSBShift.getHSBShift(season);
                Color finalColor = ColorHelper.changeHueSatBri(initialColor, hueShift.hue, hueShift.saturation, hueShift.brightness);
                return Optional.of(finalColor.getRGB());
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

    @ConfigEntry.Category("crops")
    private boolean isSeasonMessingCrops = true;
    @ConfigEntry.Category("crops")
    private boolean isSeasonMessingBonemeal = false;

    @ConfigEntry.Category("crops")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    private DefaultCropConfig defaultCropConfig = new DefaultCropConfig(1.0f, 0.8f, 0.6f, 0f);

    @ConfigEntry.Category("crops")
    private final List<CropConfig> cropConfigs = new ArrayList<>();

    public float getSeasonCropMultiplier(Identifier cropIdentifier, Season season) {
        Optional<CropConfig> config = cropConfigs.stream().filter(it -> it.cropIdentifier.equals(cropIdentifier.toString())).findFirst();
        return config.map(cropConfig -> cropConfig.getModifier(season)).orElse(defaultCropConfig.getModifier(season));
    }

    @ConfigEntry.Category("animals")
    private boolean doAnimalsBreedsInWinter = true;

    public boolean doAnimalsBreedsInWinter() {
        return doAnimalsBreedsInWinter;
    }

    public boolean isSeasonMessingCrops() {
        return isSeasonMessingCrops;
    }

    public boolean isSeasonMessingBonemeal() {
        return isSeasonMessingBonemeal;
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

    public boolean isSeasonCalendarEnabled() {
        return isSeasonCalendarEnabled;
    }

    public boolean isSeasonDetectorEnabled() {
        return isSeasonDetectorEnabled;
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
}
