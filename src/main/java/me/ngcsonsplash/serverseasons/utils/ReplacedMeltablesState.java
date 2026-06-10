package me.ngcsonsplash.serverseasons.utils;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentState;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import me.ngcsonsplash.serverseasons.ServerSeasons;

import static me.ngcsonsplash.serverseasons.ServerSeasons.MOD_NAME;

public class ReplacedMeltablesState extends PersistentState {

    Long2ObjectArrayMap<Long2ObjectArrayMap<BlockState>> chunkToReplaced = new Long2ObjectArrayMap<>();

    public BlockState getReplaced(BlockPos blockPos) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        Long2ObjectArrayMap<BlockState> posToReplaced = chunkToReplaced.get(chunkPos.toLong());
        if(posToReplaced != null) {
            return posToReplaced.get(blockPos.asLong());
        }else{
            return null;
        }
    }

    public void setReplaced(BlockPos blockPos, BlockState replacedState) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        Long2ObjectArrayMap<BlockState> posToReplaced = chunkToReplaced.get(chunkPos.toLong());
        if(posToReplaced != null) {
            if(replacedState != null) {
                posToReplaced.put(blockPos.asLong(), replacedState);
            }else{
                posToReplaced.remove(blockPos.asLong());
                if(posToReplaced.isEmpty()) {
                    chunkToReplaced.remove(chunkPos.toLong());
                }
            }
        }else if(replacedState != null) {
            posToReplaced = new Long2ObjectArrayMap<>();
            posToReplaced.put(blockPos.asLong(), replacedState);
            chunkToReplaced.put(chunkPos.toLong(), posToReplaced);
        }
        markDirty();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        chunkToReplaced.long2ObjectEntrySet().fastForEach(entry -> {
            if(!entry.getValue().isEmpty()) {
                NbtCompound innerNbt = new NbtCompound();
                entry.getValue().long2ObjectEntrySet().fastForEach(innerEntry -> {
                    BlockState.CODEC.encode(innerEntry.getValue(), NbtOps.INSTANCE, NbtOps.INSTANCE.empty()).ifSuccess((element) -> {
                        innerNbt.put(innerEntry.getLongKey() + "", element);
                    });
                });
                nbt.put(entry.getLongKey() + "", innerNbt);
            }
        });
        return nbt;
    }
    
    public static ReplacedMeltablesState createFromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        ReplacedMeltablesState state = new ReplacedMeltablesState();
        nbt.getKeys().forEach(key -> {
            try {
                long longKey = Long.parseLong(key);
                Long2ObjectArrayMap<BlockState> posToReplaced = new Long2ObjectArrayMap<>();
                NbtCompound innerNbt = nbt.getCompound(key);
                innerNbt.getKeys().forEach(innerKey -> {
                    long innerLongKey = Long.parseLong(innerKey);
                    BlockState.CODEC.decode(NbtOps.INSTANCE, innerNbt.get(innerKey)).ifSuccess((pair) -> {
                        BlockState replacedState = pair.getFirst();
                        posToReplaced.put(innerLongKey, replacedState);
                    });
                });
                state.chunkToReplaced.put(longKey, posToReplaced);
            }catch (NumberFormatException exception) {
                ServerSeasons.LOGGER.error("["+MOD_NAME+"] Error reading replaced meltable blocks at "+key, exception);
            }
        });
        return state;
    }
    
    public static PersistentState.Type<ReplacedMeltablesState> getPersistentStateType() {
        return new PersistentState.Type<>(ReplacedMeltablesState::new, ReplacedMeltablesState::createFromNbt, null);
    }
}
