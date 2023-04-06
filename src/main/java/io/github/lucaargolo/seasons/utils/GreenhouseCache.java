package io.github.lucaargolo.seasons.utils;

import io.github.lucaargolo.seasons.FabricSeasons;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.*;

public class GreenhouseCache {

    private static final HashMap<RegistryKey<World>, HashMap<ChunkPos, ArrayList<GreenHouseTicket>>> CACHE = new HashMap<>();

    public static final int EXPIRATION_TIME = 5;
    private static int AGE;

    public static void add(World world, ChunkPos chunkPos, GreenHouseTicket ticket) {
        RegistryKey<World> worldKey = world.getRegistryKey();
        HashMap<ChunkPos, ArrayList<GreenHouseTicket>> chunkTickets = CACHE.computeIfAbsent(worldKey, k -> new HashMap<>());
        chunkTickets.computeIfAbsent(chunkPos, p -> new ArrayList<>()).add(ticket);
    }

    public static Season test(World world, BlockPos pos) {
        Season currentSeason = FabricSeasons.getCurrentSeason(world);
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
                        ticket.expired = true;
                        iterator.remove();
                    }else{
                        seasons.addAll(ticket.test(pos));
                    }
                }
            }
        }
        return seasons.stream().max(Comparator.comparingInt(Season::getTemperature)).orElse(currentSeason);
    }

    public static void tick(MinecraftServer server) {
        AGE++;
    }

    public static class GreenHouseTicket {

        private final BlockBox box;

        public final Set<Season> seasons;

        public int age;
        public boolean expired;

        public GreenHouseTicket(BlockBox box, Season... season) {
            this.box = box;
            this.seasons = new HashSet<>(List.of(season));
            this.age = AGE;
            this.expired = false;
        }

        public Set<Season> test(BlockPos pos) {
            if(box.contains(pos)) {
                return this.seasons;
            }else{
                return Set.of();
            }
        }

    }

}
