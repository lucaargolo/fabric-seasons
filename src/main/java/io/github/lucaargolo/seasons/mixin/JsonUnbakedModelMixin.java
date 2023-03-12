package io.github.lucaargolo.seasons.mixin;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import io.github.lucaargolo.seasons.FabricSeasonsClient;
import io.github.lucaargolo.seasons.mixed.JsonUnbakedModelMixed;
import io.github.lucaargolo.seasons.utils.Season;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.function.Function;

@Mixin(JsonUnbakedModel.class)
public abstract class JsonUnbakedModelMixin implements JsonUnbakedModelMixed {


    @Shadow protected Map<String, Either<SpriteIdentifier, String>> textureMap;

    @Shadow public abstract BakedModel bake(ModelLoader loader, JsonUnbakedModel parent, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Identifier id, boolean hasDepth);

    @Shadow public abstract Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences);

    private Map<Season, Map<String, Either<SpriteIdentifier, String>>> seasonalTextureMap;
    private Map<String, Either<SpriteIdentifier, String>> originalTextureMap;

    @Inject(at = @At("TAIL"), method = "<init>")
    public void collectOriginalTextureMap(@Nullable Identifier parentId, List<ModelElement> elements, Map<String, Either<SpriteIdentifier, String>> textureMap, boolean ambientOcclusion, @Nullable JsonUnbakedModel.GuiLight guiLight, ModelTransformation transformations, List<ModelOverride> overrides, CallbackInfo ci) {
        this.originalTextureMap = textureMap;
    }

    @Override
    public Map<Season, Map<String, Either<SpriteIdentifier, String>>> getSeasonalTextureMap() {
        return this.seasonalTextureMap;
    }

    @Override
    public void setSeasonalTextureMap(Map<Season, Map<String, Either<SpriteIdentifier, String>>> seasonalTextureMap) {
        this.seasonalTextureMap = seasonalTextureMap;
    }

    @Inject(at = @At("RETURN"), method = "bake(Lnet/minecraft/client/render/model/ModelLoader;Lnet/minecraft/client/render/model/json/JsonUnbakedModel;Ljava/util/function/Function;Lnet/minecraft/client/render/model/ModelBakeSettings;Lnet/minecraft/util/Identifier;Z)Lnet/minecraft/client/render/model/BakedModel;")
    public void collectSeasonalModels(ModelLoader loader, JsonUnbakedModel parent, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Identifier id, boolean hasDepth, CallbackInfoReturnable<BakedModel> cir) {
        BakedModel originalModel = cir.getReturnValue();
        if(seasonalTextureMap != null && this.textureMap == this.originalTextureMap) {
            seasonalTextureMap.forEach((season, textureMap) -> {
                this.textureMap = textureMap;
                if(!FabricSeasonsClient.originalToSeasonModelMap.containsKey(originalModel)) {
                    FabricSeasonsClient.originalToSeasonModelMap.put(originalModel, Maps.newHashMap());
                }
                FabricSeasonsClient.originalToSeasonModelMap.get(originalModel).put(season, bake(loader, parent, textureGetter, settings, id, hasDepth));
            });
            this.textureMap = originalTextureMap;
        }
    }

    @Inject(at = @At("RETURN"), method = "getTextureDependencies")
    public void collectSeasonalDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences, CallbackInfoReturnable<Collection<SpriteIdentifier>> cir) {
        Collection<SpriteIdentifier> originalDependencies = cir.getReturnValue();
        if(originalDependencies instanceof HashSet<SpriteIdentifier> hashSet && seasonalTextureMap != null && this.textureMap == this.originalTextureMap) {
            seasonalTextureMap.forEach((season, textureMap) -> {
                this.textureMap = textureMap;
                originalDependencies.addAll(getTextureDependencies(unbakedModelGetter, unresolvedTextureReferences));
            });
            this.textureMap = originalTextureMap;
        }
    }





}
