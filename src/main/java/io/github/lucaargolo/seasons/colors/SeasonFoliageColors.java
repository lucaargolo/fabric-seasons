package io.github.lucaargolo.seasons.colors;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.utils.Season;

public class SeasonFoliageColors {

    private static int[] springColorMap = new int[65536];
    private static int[] summerColorMap = new int[65536];
    private static int[] fallColorMap = new int[65536];
    private static int[] winterColorMap = new int[65536];

    public static void setColorMap(Season season, int[] pixels) {
        switch (season) {
            case SPRING:
                springColorMap = pixels;
                break;
            case SUMMER:
                summerColorMap = pixels;
                break;
            case FALL:
                fallColorMap = pixels;
                break;
            case WINTER:
                winterColorMap = pixels;
                break;
        }
    }

    public static int getColor(Season season, double temperature, double humidity) {
        humidity *= temperature;
        int i = (int)((1.0D - temperature) * 255.0D);
        int j = (int)((1.0D - humidity) * 255.0D);
        switch (season) {
            case SPRING:
                return springColorMap[j << 8 | i];
            case SUMMER:
                return summerColorMap[j << 8 | i];
            case FALL:
                return fallColorMap[j << 8 | i];
            case WINTER:
                return winterColorMap[j << 8 | i];
        }
        return springColorMap[j << 8 | i];
    }

    public static int getSpruceColor(Season season) {
        return FabricSeasons.MOD_CONFIG.getMinecraftSpruceFoliage().getColor(season);
    }

    public static int getBirchColor(Season season) {
        return FabricSeasons.MOD_CONFIG.getMinecraftBirchFoliage().getColor(season);
    }

    public static int getDefaultColor(Season season) {
        return FabricSeasons.MOD_CONFIG.getMinecraftDefaultFoliage().getColor(season);
    }

}
