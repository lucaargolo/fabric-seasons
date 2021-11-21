package com.yurisuika.seasons.mixin;

import com.yurisuika.seasons.utils.ColorsCache;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(at = @At("HEAD"), method = "reload()V")
    public void reload(CallbackInfo info) {
        ColorsCache.clearCache();
    }

}
