package com.yurisuika.seasons.colors;

import com.yurisuika.seasons.utils.Season;

public class SeasonGrassColors {

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
        int k = j << 8 | i;
        switch (season) {
            case EARLY_SPRING:
                return k > earlySpringColorMap.length ? -65281 : earlySpringColorMap[k];
            case MID_SPRING:
                return k > midSpringColorMap.length ? -65281 : midSpringColorMap[k];
            case LATE_SPRING:
                return k > lateSpringColorMap.length ? -65281 : lateSpringColorMap[k];
            case EARLY_SUMMER:
                return k > earlySummerColorMap.length ? -65281 : earlySummerColorMap[k];
            case MID_SUMMER:
                return k > midSummerColorMap.length ? -65281 : midSummerColorMap[k];
            case LATE_SUMMER:
                return k > lateSummerColorMap.length ? -65281 : lateSummerColorMap[k];
            case EARLY_AUTUMN:
                return k > earlyAutumnColorMap.length ? -65281 : earlyAutumnColorMap[k];
            case MID_AUTUMN:
                return k > midAutumnColorMap.length ? -65281 : midAutumnColorMap[k];
            case LATE_AUTUMN:
                return k > lateAutumnColorMap.length ? -65281 : lateAutumnColorMap[k];
            case EARLY_WINTER:
                return k > earlyWinterColorMap.length ? -65281 : earlyWinterColorMap[k];
            case MID_WINTER:
                return k > midWinterColorMap.length ? -65281 : midWinterColorMap[k];
            case LATE_WINTER:
                return k > lateWinterColorMap.length ? -65281 : lateWinterColorMap[k];
        }
        return k > earlySpringColorMap.length ? -65281 : earlySpringColorMap[k];
    }
}
