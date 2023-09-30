package io.github.lucaargolo.seasons.utils;

import io.github.lucaargolo.seasons.FabricSeasons;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentState;

import static io.github.lucaargolo.seasons.FabricSeasons.MOD_NAME;

public class PlacedMeltablesState extends PersistentState {

    Long2ObjectArrayMap<LongArraySet> chunkToPlaced = new Long2ObjectArrayMap<>();

    public boolean isManuallyPlaced(BlockPos blockPos) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        LongArraySet longArray = chunkToPlaced.get(chunkPos.toLong());
        return longArray != null && longArray.contains(blockPos.asLong());
    }

    public void setManuallyPlaced(BlockPos blockPos, Boolean manuallyPlaced) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        LongArraySet longArray = chunkToPlaced.get(chunkPos.toLong());
        if(longArray != null) {
            if(manuallyPlaced) {
                longArray.add(blockPos.asLong());
            }else{
                longArray.remove(blockPos.asLong());
                if(longArray.isEmpty()) {
                    chunkToPlaced.remove(chunkPos.toLong());
                }
            }
        }else if(manuallyPlaced) {
            longArray = new LongArraySet();
            longArray.add(blockPos.asLong());
            chunkToPlaced.put(chunkPos.toLong(), longArray);
        }
        markDirty();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        chunkToPlaced.long2ObjectEntrySet().fastForEach(entry -> {
            if(!entry.getValue().isEmpty()) {
                nbt.put(entry.getLongKey() + "", new NbtLongArray(entry.getValue()));
            }
        });
        return nbt;
    }
    public static PlacedMeltablesState createFromNbt(NbtCompound nbt) {
        PlacedMeltablesState state = new PlacedMeltablesState();
        nbt.getKeys().forEach(key -> {
            try {
                long longKey = Long.parseLong(key);
                long[] longArray = nbt.getLongArray(key);
                state.chunkToPlaced.put(longKey, new LongArraySet(longArray));
            }catch (NumberFormatException exception) {
                FabricSeasons.LOGGER.error("["+MOD_NAME+"] Error reading manually placed meltable blocks at "+key, exception);
            }
        });
        return state;
    }

    public static Type<PlacedMeltablesState> type = new Type<>(
        PlacedMeltablesState::new,
        PlacedMeltablesState::createFromNbt,
        null
            );
}
