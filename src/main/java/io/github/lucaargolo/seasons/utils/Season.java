package io.github.lucaargolo.seasons.utils;

import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;

import java.util.Locale;

public enum Season implements StringIdentifiable {
    SPRING(Formatting.GREEN, Formatting.DARK_GREEN),
    SUMMER(Formatting.GOLD, Formatting.GOLD),
    FALL(Formatting.RED, Formatting.RED),
    WINTER(Formatting.AQUA, Formatting.DARK_AQUA);

    private final Formatting formatting;
    private final Formatting darkFormatting;

    Season(Formatting formatting, Formatting darkFormatting) {
        this.formatting = formatting;
        this.darkFormatting = darkFormatting;
    }

    public Formatting getFormatting() {
        return formatting;
    }

    public Formatting getDarkFormatting() {
        return darkFormatting;
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
