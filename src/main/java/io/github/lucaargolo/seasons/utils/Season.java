package io.github.lucaargolo.seasons.utils;

import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;

import java.util.Locale;

public enum Season implements StringIdentifiable {
    SPRING(Formatting.GREEN),
    SUMMER(Formatting.GOLD),
    FALL(Formatting.RED),
    WINTER(Formatting.AQUA);

    private final Formatting formatting;

    Season(Formatting formatting) {
        this.formatting = formatting;
    }

    public Formatting getFormatting() {
        return formatting;
    }

    public String getTranslationKey() {
        return "tooltip.seasons."+name().toLowerCase(Locale.ROOT);
    }

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
