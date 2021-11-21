package com.yurisuika.seasons.utils;

import com.mojang.datafixers.util.Pair;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.HashMap;

public class GreenhouseCache {

    private static HashMap<RegistryKey<World>, HashMap<Pair<Integer, Integer>, Integer>> lastCache = new HashMap<>();
    private static HashMap<RegistryKey<World>, HashMap<Pair<Integer, Integer>, Integer>> cache = new HashMap<>();

    public static void add(World world, BlockPos pos) {
        RegistryKey<World> worldKey = world.getRegistryKey();
        HashMap<Pair<Integer, Integer>, Integer> positionsForWorld = cache.computeIfAbsent(worldKey, k -> new HashMap<>());
        Pair<Integer, Integer> position = new Pair<>(pos.getX(), pos.getZ());
        int height = positionsForWorld.computeIfAbsent(position, k -> pos.getY());
        if(height < pos.getY()) {
            positionsForWorld.put(position, pos.getY());
        }
    }

    public static boolean test(World world, BlockPos pos) {
        RegistryKey<World> worldKey = world.getRegistryKey();
        HashMap<Pair<Integer, Integer>, Integer> positionsForWorld = lastCache.get(worldKey);
        if(positionsForWorld != null) {
            Pair<Integer, Integer> position = new Pair<>(pos.getX(), pos.getZ());
            Integer height = positionsForWorld.get(position);
            if(height != null) {
                return pos.getY() < height;
            }
        }
        return false;
    }

    public static void tick(MinecraftServer server) {
        lastCache = cache;
        cache = new HashMap<>();
    }
}
