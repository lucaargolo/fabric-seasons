package io.github.lucaargolo.seasons.utils;

import io.github.lucaargolo.seasons.FabricSeasons;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
@Config(name = FabricSeasons.MOD_ID)
public class ModConfig implements ConfigData {

    private int seasonLength = 672000;
    private boolean isSeasonLocked = false;
    private Season lockedSeason = Season.SPRING;

    public int getSeasonLength() {
        return seasonLength;
    }

    public boolean isSeasonLocked() {
        return isSeasonLocked;
    }

    public Season getLockedSeason() {
        return lockedSeason;
    }
}
