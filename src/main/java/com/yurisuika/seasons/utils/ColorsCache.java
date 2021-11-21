package com.yurisuika.seasons.utils;

import net.minecraft.world.biome.Biome;

import java.util.HashMap;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class ColorsCache {

    private static final HashMap<Biome, Optional<Integer>> foliageColors = new HashMap<>();
    private static final HashMap<Biome, Optional<Integer>> grassColors = new HashMap<>();

    public static void createFoliageCache(Biome biome, Optional<Integer> integer) {
        foliageColors.put(biome, integer);
    }

    public static void createGrassCache(Biome biome, Optional<Integer> integer) {
        grassColors.put(biome, integer);
    }

    public static boolean hasFoliageCache(Biome biome) {
        return foliageColors.containsKey(biome);
    }

    public static boolean hasGrassCache(Biome biome) {
        return grassColors.containsKey(biome);
    }

    public static Optional<Integer> getFoliageCache(Biome biome) {
        return foliageColors.get(biome);
    }

    public static Optional<Integer> getGrassCache(Biome biome) {
        return grassColors.get(biome);
    }

    public static void clearCache() {
        foliageColors.clear();
        grassColors.clear();
    }

}
