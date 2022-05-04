package io.github.lucaargolo.seasons.models;

import com.mojang.datafixers.util.Pair;
import io.github.lucaargolo.seasons.mixin.ChunkRendererRegionAccessor;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.LightType;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class SeasonalSnowBakedModel implements FabricBakedModel, BakedModel, UnbakedModel {

    private final Lazy<BakedModel> originalSnowModel;

    public SeasonalSnowBakedModel(String variant) {
        ModelIdentifier originalSnowIdentifier = new ModelIdentifier(new Identifier("snow"), variant);
        this.originalSnowModel = new Lazy<>(() -> MinecraftClient.getInstance().getBakedModelManager().getModel(originalSnowIdentifier));
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        if (blockView.getLightLevel(LightType.SKY, pos) <= 0 || !(blockView instanceof ChunkRendererRegionAccessor accessor) || !(accessor.getWorld().getBiome(pos).getTemperature(pos) >= 0.15F)) {
            context.fallbackConsumer().accept(originalSnowModel.get());
        }
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        context.fallbackConsumer().accept(originalSnowModel.get());
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        return originalSnowModel.get().getQuads(state, face, random);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return originalSnowModel.get().useAmbientOcclusion();
    }

    @Override
    public boolean hasDepth() {
        return originalSnowModel.get().hasDepth();
    }

    @Override
    public boolean isSideLit() {
        return originalSnowModel.get().isSideLit();
    }

    @Override
    public boolean isBuiltin() {
        return originalSnowModel.get().isBuiltin();
    }

    @Override
    public Sprite getParticleSprite() {
        return originalSnowModel.get().getParticleSprite();
    }

    @Override
    public ModelTransformation getTransformation() {
        return originalSnowModel.get().getTransformation();
    }

    @Override
    public ModelOverrideList getOverrides() {
        return originalSnowModel.get().getOverrides();
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        return new ArrayList<>();
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
        return new ArrayList<>();
    }

    @Nullable
    @Override
    public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
        return this;
    }

    public static class Provider implements ModelVariantProvider {

        ResourceManager resourceManager;

        public Provider(ResourceManager resourceManager) {
            this.resourceManager = resourceManager;
        }

        @Override
        public @Nullable UnbakedModel loadModelVariant(ModelIdentifier modelId, ModelProviderContext context) throws ModelProviderException {
            if(modelId.getNamespace().equals("seasons") && modelId.getPath().equals("seasonal_snow")) {
                return new SeasonalSnowBakedModel(modelId.getVariant());
            }
            return null;
        }
    }
}
