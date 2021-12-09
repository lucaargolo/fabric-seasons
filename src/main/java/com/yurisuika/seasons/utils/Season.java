package com.yurisuika.seasons.utils;

import net.minecraft.util.StringIdentifiable;

import java.util.Locale;

public enum Season implements StringIdentifiable {
    EARLY_SPRING,
    MID_SPRING,
    LATE_SPRING,
    EARLY_SUMMER,
    MID_SUMMER,
    LATE_SUMMER,
    EARLY_AUTUMN,
    MID_AUTUMN,
    LATE_AUTUMN,
    EARLY_WINTER,
    MID_WINTER,
    LATE_WINTER;

    @Override
    public String asString() {
        return name().toLowerCase(Locale.ROOT);
    }

    public Season getNext() {
        return switch (this) {
            case MID_SPRING -> Season.LATE_SPRING;
            case LATE_SPRING -> Season.EARLY_SUMMER;
            case EARLY_SUMMER -> Season.MID_SUMMER;
            case MID_SUMMER -> Season.LATE_SUMMER;
            case LATE_SUMMER -> Season.EARLY_AUTUMN;
            case EARLY_AUTUMN -> Season.MID_AUTUMN;
            case MID_AUTUMN -> Season.LATE_AUTUMN;
            case LATE_AUTUMN -> Season.EARLY_WINTER;
            case EARLY_WINTER -> Season.MID_WINTER;
            case MID_WINTER -> Season.LATE_WINTER;
            case LATE_WINTER -> Season.EARLY_SPRING;
            default -> Season.MID_SPRING;
        };
    }
}
