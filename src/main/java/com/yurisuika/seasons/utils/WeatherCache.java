package com.yurisuika.seasons.utils;

import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

import java.util.HashMap;

public class WeatherCache {

    private static final HashMap<Identifier, Biome.Weather> cacheMap = new HashMap<>();

    public static boolean hasCache(Identifier identifier) {
        return cacheMap.containsKey(identifier);
    }

    public static Biome.Weather getCache(Identifier identifier) {
        return cacheMap.get(identifier);
    }

    public static void setCache(Identifier identifier, Biome.Weather weather) {
        cacheMap.put(identifier, weather);
    }

}
