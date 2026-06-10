package me.ngcsonsplash.serverseasons.mixin;

import me.ngcsonsplash.serverseasons.ServerSeasons;
import me.ngcsonsplash.serverseasons.utils.Meltable;
import net.minecraft.block.BlockState;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements StructureWorldAccess {


    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z", ordinal = 0), method = "tickIceAndSnow(Lnet/minecraft/util/math/BlockPos;)V", locals = LocalCapture.CAPTURE_FAILSOFT)
    public void setMeltableIce(BlockPos pos, CallbackInfo ci, BlockPos blockPos, BlockPos blockPos2, Biome biome) {
        ServerSeasons.setMeltable(blockPos2);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z", ordinal = 1), method = "tickIceAndSnow(Lnet/minecraft/util/math/BlockPos;)V", locals = LocalCapture.CAPTURE_FAILSOFT)
    public void setMeltableLayeredSnow(BlockPos pos, CallbackInfo ci, BlockPos blockPos, BlockPos blockPos2, Biome biome, int i, BlockState blockState, int j, BlockState blockState2) {
        ServerSeasons.setMeltable(blockPos);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z", ordinal = 2), method = "tickIceAndSnow(Lnet/minecraft/util/math/BlockPos;)V", locals = LocalCapture.CAPTURE_FAILSOFT)
    public void setMeltableSnow(BlockPos pos, CallbackInfo ci, BlockPos blockPos, BlockPos blockPos2, Biome biome) {
        ServerSeasons.setMeltable(blockPos);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;precipitationTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/biome/Biome$Precipitation;)V"), method = "tickIceAndSnow(Lnet/minecraft/util/math/BlockPos;)V", locals = LocalCapture.CAPTURE_FAILSOFT)
    public void setReplacedMeltable(BlockPos pos, CallbackInfo ci, BlockPos blockPos, BlockPos blockPos2, Biome biome, int i, Biome.Precipitation precipitation, BlockState blockState3) {
        if (ServerSeasons.CONFIG.shouldSnowReplaceVegetation())
            Meltable.replaceBlockOnSnow((ServerWorld) (Object) this, blockPos, biome);
    }

}
