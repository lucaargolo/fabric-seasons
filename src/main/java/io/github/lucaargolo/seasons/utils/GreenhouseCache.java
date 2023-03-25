package io.github.lucaargolo.seasons.utils;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.*;

public class GreenhouseCache {

    private static final HashMap<RegistryKey<World>, HashMap<ChunkPos, ArrayList<GreenHouseTicket>>> CACHE = new HashMap<>();

    public static final int EXPIRATION_TIME = 40;
    private static int AGE;

    public static void add(World world, ChunkPos chunkPos, GreenHouseTicket ticket) {
        RegistryKey<World> worldKey = world.getRegistryKey();
        HashMap<ChunkPos, ArrayList<GreenHouseTicket>> chunkTickets = CACHE.computeIfAbsent(worldKey, k -> new HashMap<>());
        chunkTickets.computeIfAbsent(chunkPos, p -> new ArrayList<>()).add(ticket);
    }

    public static Set<Season> test(World world, BlockPos pos) {
        HashSet<Season> seasons = new HashSet<>();
        RegistryKey<World> worldKey = world.getRegistryKey();
        HashMap<ChunkPos, ArrayList<GreenHouseTicket>> chunkTickets = CACHE.get(worldKey);
        if(chunkTickets != null) {
            ArrayList<GreenHouseTicket> tickets = chunkTickets.get(new ChunkPos(pos));
            if(tickets != null) {
                Iterator<GreenHouseTicket> iterator = tickets.iterator();
                while (iterator.hasNext()) {
                    GreenHouseTicket ticket = iterator.next();
                    if(AGE > ticket.age + EXPIRATION_TIME) {
                        iterator.remove();
                    }else{
                        seasons.addAll(ticket.test(pos));
                    }
                }
            }
        }
        return seasons;
    }

    public static void tick(MinecraftServer server) {
        AGE++;
    }

    public static class GreenHouseTicket {

        private final Set<Season> seasons;
        private final int x;
        private final int z;
        private final int minY;
        private final int maxY;
        private final int age;

        public GreenHouseTicket(int x, int z, int minY, int maxY, Season... season) {
            this.seasons = Set.of(season);
            this.x = x;
            this.z = z;
            this.minY = minY;
            this.maxY = maxY;
            this.age = AGE;
        }

        public Set<Season> test(BlockPos pos) {
            if(pos.getX() == x && (pos.getY() >= minY && pos.getY() <= maxY) && pos.getZ() == z) {
                return this.seasons;
            }else{
                return Set.of();
            }
        }
    }

}
