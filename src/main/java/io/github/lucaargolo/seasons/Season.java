package io.github.lucaargolo.seasons;

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
}
