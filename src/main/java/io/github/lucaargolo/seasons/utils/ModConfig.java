package io.github.lucaargolo.seasons.utils;

import io.github.lucaargolo.seasons.FabricSeasons;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
@Config(name = FabricSeasons.MOD_ID)
public class ModConfig implements ConfigData {

    private static class SeasonLock {
        @ConfigEntry.Gui.Tooltip private boolean isSeasonLocked = false;
        private Season lockedSeason = Season.SPRING;
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
