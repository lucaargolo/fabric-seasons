package com.yurisuika.seasons.colors;

import com.yurisuika.seasons.Seasons;
import com.yurisuika.seasons.utils.Season;

public class SeasonFoliageColors {

    private static int[] earlySpringColorMap = new int[65536];
    private static int[] midSpringColorMap = new int[65536];
    private static int[] lateSpringColorMap = new int[65536];
    private static int[] earlySummerColorMap = new int[65536];
    private static int[] midSummerColorMap = new int[65536];
    private static int[] lateSummerColorMap = new int[65536];
    private static int[] earlyAutumnColorMap = new int[65536];
    private static int[] midAutumnColorMap = new int[65536];
    private static int[] lateAutumnColorMap = new int[65536];
    private static int[] earlyWinterColorMap = new int[65536];
    private static int[] midWinterColorMap = new int[65536];
    private static int[] lateWinterColorMap = new int[65536];

    public static void setColorMap(Season season, int[] pixels) {
        switch (season) {
            case EARLY_SPRING:
                earlySpringColorMap = pixels;
                break;
            case MID_SPRING:
                midSpringColorMap = pixels;
                break;
            case LATE_SPRING:
                lateSpringColorMap = pixels;
                break;
            case EARLY_SUMMER:
                earlySummerColorMap = pixels;
                break;
            case MID_SUMMER:
                midSummerColorMap = pixels;
                break;
            case LATE_SUMMER:
                lateSummerColorMap = pixels;
                break;
            case EARLY_AUTUMN:
                earlyAutumnColorMap = pixels;
                break;
            case MID_AUTUMN:
                midAutumnColorMap = pixels;
                break;
            case LATE_AUTUMN:
                lateAutumnColorMap = pixels;
                break;
            case EARLY_WINTER:
                earlyWinterColorMap = pixels;
                break;
            case MID_WINTER:
                midWinterColorMap = pixels;
                break;
            case LATE_WINTER:
                lateWinterColorMap = pixels;
                break;
        }
    }

    public static int getColor(Season season, double temperature, double humidity) {
        humidity *= temperature;
        int i = (int)((1.0D - temperature) * 255.0D);
        int j = (int)((1.0D - humidity) * 255.0D);
        switch (season) {
            case EARLY_SPRING:
                return earlySpringColorMap[j << 8 | i];
            case MID_SPRING:
                return midSpringColorMap[j << 8 | i];
            case LATE_SPRING:
                return lateSpringColorMap[j << 8 | i];
            case EARLY_SUMMER:
                return earlySummerColorMap[j << 8 | i];
            case MID_SUMMER:
                return midSummerColorMap[j << 8 | i];
            case LATE_SUMMER:
                return lateSummerColorMap[j << 8 | i];
            case EARLY_AUTUMN:
                return earlyAutumnColorMap[j << 8 | i];
            case MID_AUTUMN:
                return midAutumnColorMap[j << 8 | i];
            case LATE_AUTUMN:
                return lateAutumnColorMap[j << 8 | i];
            case EARLY_WINTER:
                return earlyWinterColorMap[j << 8 | i];
            case MID_WINTER:
                return midWinterColorMap[j << 8 | i];
            case LATE_WINTER:
                return lateWinterColorMap[j << 8 | i];
        }
        return earlySpringColorMap[j << 8 | i];
    }

    public static int getSpruceColor(Season season) {
        return Seasons.CONFIG.getMinecraftSpruceFoliage().getColor(season);
    }

    public static int getBirchColor(Season season) {
        return Seasons.CONFIG.getMinecraftBirchFoliage().getColor(season);
    }

    public static int getDefaultColor(Season season) {
        return Seasons.CONFIG.getMinecraftDefaultFoliage().getColor(season);
    }

}
