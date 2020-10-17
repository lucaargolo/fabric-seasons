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
        switch (this) {
            case SUMMER:
                return Season.FALL;
            case FALL:
                return Season.WINTER;
            case WINTER:
                return Season.SPRING;
            default:
                return Season.SUMMER;
        }
    }
}
