package io.github.lucaargolo.seasons.utils;

import com.google.gson.JsonElement;

public class SeasonColor {
    private final int springColor;
    private final int summerColor;
    private final int fallColor;
    private final int winterColor;

    public SeasonColor(int springColor, int summerColor, int fallColor, int winterColor) {
        this.springColor = springColor;
        this.summerColor = summerColor;
        this.fallColor = fallColor;
        this.winterColor = winterColor;
    }

    public SeasonColor(JsonElement json) {
        this.springColor = getStringColor(json.getAsJsonObject().get("summer").getAsString());
        this.summerColor = getStringColor(json.getAsJsonObject().get("summer").getAsString());
        this.fallColor = getStringColor(json.getAsJsonObject().get("fall").getAsString());
        this.winterColor = getStringColor(json.getAsJsonObject().get("winter").getAsString());
    }

    private int getStringColor(String color) {
        if(color.startsWith("0x")) {
            return Integer.parseInt(color.replace("0x", ""), 16);
        }else if(color.startsWith("#")) {
            return Integer.parseInt(color.replace("#", ""), 16);
        }else{
            return Integer.parseInt(color);
        }
    }

    public int getColor(Season season) {
        return switch (season) {
            case SPRING -> springColor;
            case SUMMER -> summerColor;
            case FALL -> fallColor;
            case WINTER -> winterColor;
        };
    }
}
