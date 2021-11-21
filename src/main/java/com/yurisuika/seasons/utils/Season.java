package com.yurisuika.seasons.utils;

import net.minecraft.util.StringIdentifiable;

import java.util.Locale;

public enum Season implements StringIdentifiable {
    SPRING,
    SUMMER,
    FALL,
    WINTER;

    @Override
    public String asString() {
        return name().toLowerCase(Locale.ROOT);
    }

    public Season getNext() {
        return switch (this) {
            case SUMMER -> Season.FALL;
            case FALL -> Season.WINTER;
            case WINTER -> Season.SPRING;
            default -> Season.SUMMER;
        };
    }
}
