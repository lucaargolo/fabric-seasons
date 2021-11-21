package com.yurisuika.seasons.colors;

import com.yurisuika.seasons.utils.Season;

public class SeasonGrassColors {

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
        int k = j << 8 | i;
        switch (season) {
            case SPRING:
                return k > springColorMap.length ? -65281 : springColorMap[k];
            case SUMMER:
                return k > summerColorMap.length ? -65281 : summerColorMap[k];
            case FALL:
                return k > fallColorMap.length ? -65281 : fallColorMap[k];
            case WINTER:
                return k > winterColorMap.length ? -65281 : winterColorMap[k];
        }
        return k > springColorMap.length ? -65281 : springColorMap[k];
    }
}
