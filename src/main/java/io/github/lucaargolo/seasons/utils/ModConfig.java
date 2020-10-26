package io.github.lucaargolo.seasons.utils;

import io.github.lucaargolo.seasons.FabricSeasons;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal", "MismatchedQueryAndUpdateOfCollection"})
@Config(name = FabricSeasons.MOD_ID)
public class ModConfig implements ConfigData {

    @SuppressWarnings("FieldMayBeFinal")
    private static class SeasonLock {
        @ConfigEntry.Gui.Tooltip private boolean isSeasonLocked = false;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        private Season lockedSeason = Season.SPRING;
    }

    @SuppressWarnings("FieldMayBeFinal")
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

    @SuppressWarnings("FieldMayBeFinal")
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
    private final List<BiomeColors> foliageColorList = new ArrayList<>();
    @ConfigEntry.Category("hardcodedColors")
    private final List<BiomeColors> grassColorList = new ArrayList<>();

    public Optional<Integer> getSeasonFoliageColor(Identifier biomeIdentifier, Season season) {
        Optional<BiomeColors> colors = foliageColorList.stream().filter(it -> it.biomeIdentifier.equals(biomeIdentifier.toString())).findFirst();
        return colors.map(biomeColors -> biomeColors.colors.getColor(season));
    }

    public Optional<Integer> getSeasonGrassColor(Identifier biomeIdentifier, Season season) {
        Optional<BiomeColors> colors = grassColorList.stream().filter(it -> it.biomeIdentifier.equals(biomeIdentifier.toString())).findFirst();
        return colors.map(biomeColors -> biomeColors.colors.getColor(season));
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
