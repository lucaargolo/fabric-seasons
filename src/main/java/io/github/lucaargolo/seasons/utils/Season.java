package io.github.lucaargolo.seasons.utils;

import net.minecraft.util.StringIdentifiable;

public enum Season implements StringIdentifiable {
    SPRING,
    SUMMER,
    FALL,
    WINTER;

    @Override
    public String asString() {
        return name().toLowerCase();
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
