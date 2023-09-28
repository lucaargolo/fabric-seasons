package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.WorldRendererInterface;
import io.github.lucaargolo.seasons.utils.ColorsCache;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkRendererRegionBuilder;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin implements WorldRendererInterface {

    @Shadow @Nullable private ClientWorld world;
    @Shadow @Nullable private ChunkBuilder chunkBuilder;

    @Shadow @Final private ObjectArrayList<WorldRenderer.ChunkInfo> chunkInfos;

    @Inject(at = @At("HEAD"), method = "reload()V")
    public void reload(CallbackInfo info) {
        ColorsCache.clearCache();
    }

    @Override
    public void reloadOnlyColors() {
        if (this.world == null || this.chunkBuilder == null) {
            return;
        }
        ChunkRendererRegionBuilder chunkRendererRegionBuilder = new ChunkRendererRegionBuilder();
        this.world.reloadColor();

        for (WorldRenderer.ChunkInfo chunkInfo : this.chunkInfos) {
            ChunkBuilder.BuiltChunk builtChunk = chunkInfo.chunk;
            this.chunkBuilder.rebuild(builtChunk, chunkRendererRegionBuilder);
            builtChunk.cancelRebuild();
        }
    }
}
