package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.LightType;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FreezeTopLayerFeature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements StructureWorldAccess {

    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> dimension, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, dimension, profiler, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z", ordinal = 0), method = "tickChunk", locals = LocalCapture.CAPTURE_FAILSOFT)
    public void setMeltableIce(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci, ChunkPos chunkPos, boolean bl, int i, int j, Profiler profiler, BlockPos blockPos, BlockPos blockPos2, Biome biome) {
        FabricSeasons.setMeltable(blockPos2);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z", ordinal = 1), method = "tickChunk", locals = LocalCapture.CAPTURE_FAILSOFT)
    public void setMeltableSnow(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci, ChunkPos chunkPos, boolean bl, int i, int j, Profiler profiler, BlockPos blockPos, BlockPos blockPos2, Biome biome) {
        FabricSeasons.setMeltable(blockPos);
    }


    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;precipitationTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/biome/Biome$Precipitation;)V"), method = "tickChunk", locals = LocalCapture.CAPTURE_FAILSOFT)
    public void setReplacedMeltable(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci, ChunkPos chunkPos, boolean bl, int i, int j, Profiler profiler, BlockPos blockPos, BlockPos blockPos2, Biome biome, BlockState blockState, Biome.Precipitation precipitation) {
        BlockState plantState = this.getBlockState(blockPos);
        if(plantState.isIn(BlockTags.REPLACEABLE_PLANTS) || plantState.isIn(BlockTags.FLOWERS) || plantState.isIn(BlockTags.SAPLINGS) ) {
            if (!biome.doesNotSnow(blockPos) && blockPos.getY() >= this.getBottomY() && blockPos.getY() < this.getTopY() && this.getLightLevel(LightType.BLOCK, blockPos) < 10) {
                BlockState upperState = this.getBlockState(blockPos.up());
                if(upperState.isAir()) {
                    FabricSeasons.setMeltable(blockPos);
                    FabricSeasons.getReplacedMeltablesState((ServerWorld) (Object) this).setReplaced(blockPos, plantState);
                    this.setBlockState(blockPos, Blocks.SNOW.getDefaultState());
                }
            }
        }
    }

}
