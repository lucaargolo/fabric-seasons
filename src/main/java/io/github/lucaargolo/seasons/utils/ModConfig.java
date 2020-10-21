package io.github.lucaargolo.seasons.utils;

import io.github.lucaargolo.seasons.FabricSeasons;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Optional;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
@Config(name = FabricSeasons.MOD_ID)
public class ModConfig implements ConfigData {

    @ConfigEntry.Category("vanillaColors")
    @ConfigEntry.Gui.Excluded private static HashMap<Identifier, Field> foliageColors = new HashMap<>();
    @ConfigEntry.Category("vanillaColors")
    @ConfigEntry.Gui.Excluded private static HashMap<Identifier, Field> grassColors = new HashMap<>();

    private HardcodedColors registerBiomeColors(Identifier biomeIdentifier, boolean isFoliage, int springColor, int summerColor, int fallColor, int winterColor) {
        String type = "Grass";
        if(isFoliage) type = "Foliage";
        String camelPath = biomeIdentifier.getPath();
        camelPath = camelPath.substring(0,1).toUpperCase()+camelPath.substring(1);
        String fieldName = biomeIdentifier.getNamespace()+camelPath+type;
        HardcodedColors biomeColors = new HardcodedColors(springColor, summerColor, fallColor, winterColor);
        try {
            if (isFoliage) {
                foliageColors.put(biomeIdentifier, this.getClass().getDeclaredField(fieldName));
            } else {
                grassColors.put(biomeIdentifier, this.getClass().getDeclaredField(fieldName));
            }
        }catch (NoSuchFieldException ignored) {}
        return biomeColors;
    }

    public Optional<Integer> getSeasonFoliageColor(Identifier biomeIdentifier, Season season) {
        Field colorField = foliageColors.get(biomeIdentifier);
        if(colorField != null) {
            try {
                HardcodedColors colors = (HardcodedColors) colorField.get(this);
                return Optional.of(colors.getColor(season));
            }catch (Exception ignored) {}
        }
        return Optional.empty();
    }

    public Optional<Integer> getSeasonGrassColor(Identifier biomeIdentifier, Season season) {
        Field colorField = grassColors.get(biomeIdentifier);
        if(colorField != null) {
            try {
                HardcodedColors colors = (HardcodedColors) colorField.get(this);
                return Optional.of(colors.getColor(season));
            }catch (Exception ignored) {}
        }
        return Optional.empty();
    }

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

        private HardcodedColors(int springColor, int summerColor, int fallColor, int winterColor) {
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

    @ConfigEntry.Category("seasonBehaviour")
    @ConfigEntry.Gui.Tooltip(count = 3) private int seasonLength = 672000;
    @ConfigEntry.Category("seasonBehaviour")
    @ConfigEntry.Gui.CollapsibleObject private SeasonLock seasonLock = new SeasonLock();
    @ConfigEntry.Category("seasonBehaviour")
    @ConfigEntry.Gui.Tooltip(count = 2) private boolean doTemperatureChanges = true;

    @ConfigEntry.Category("itemsAndBlocks")
    private boolean isSeasonCalendarEnabled = true;
    @ConfigEntry.Category("itemsAndBlocks")
    private boolean isSeasonDetectorEnabled = true;

    @ConfigEntry.Category("vanillaColors")
    @ConfigEntry.Gui.CollapsibleObject private HardcodedColors defaultFoliage = new HardcodedColors(0x48B518, 0x4CE00B, 0xE0990B, 0x755514);
    @ConfigEntry.Category("vanillaColors")
    @ConfigEntry.Gui.CollapsibleObject private HardcodedColors spruceFoliage = new HardcodedColors(0x619961, 0x619961, 0x619961, 0x619961);
    @ConfigEntry.Category("vanillaColors")
    @ConfigEntry.Gui.CollapsibleObject private HardcodedColors birchFoliage = new HardcodedColors(0x80A755, 0x81B844, 0xD66800, 0x665026);
    @ConfigEntry.Category("vanillaColors")
    @ConfigEntry.Gui.CollapsibleObject private HardcodedColors swampGrass1 = new HardcodedColors(0x4C763C, 0x4C763C, 0x4C763C, 0x4C763C);
    @ConfigEntry.Category("vanillaColors")
    @ConfigEntry.Gui.CollapsibleObject private HardcodedColors swampGrass2 = new HardcodedColors(0x6A7039, 0x6A7039, 0x6A7039, 0x6A7039);

    @ConfigEntry.Category("vanillaColors")
    @ConfigEntry.Gui.CollapsibleObject private HardcodedColors minecraftSwampFoliage = registerBiomeColors(new Identifier("swamp"), true, 0x6A7039, 0x6A7039, 0x6A7039, 0x6A7039);

    public HardcodedColors getDefaultFoliage() {
        return defaultFoliage;
    }

    public HardcodedColors getSpruceFoliage() {
        return spruceFoliage;
    }

    public HardcodedColors getBirchFoliage() {
        return birchFoliage;
    }

    public HardcodedColors getSwampGrass1() {
        return swampGrass1;
    }

    public HardcodedColors getSwampGrass2() {
        return swampGrass2;
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
