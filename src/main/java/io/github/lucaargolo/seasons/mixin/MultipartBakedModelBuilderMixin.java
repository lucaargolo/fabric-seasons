package io.github.lucaargolo.seasons.mixin;

import com.google.common.collect.Lists;
import io.github.lucaargolo.seasons.FabricSeasonsClient;
import io.github.lucaargolo.seasons.utils.Season;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.MultipartBakedModel;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Mixin(MultipartBakedModel.Builder.class)
public class MultipartBakedModelBuilderMixin {

    @Unique
    private final HashMap<Season, List<Pair<Predicate<BlockState>, BakedModel>>> seasonalComponentsMap = new HashMap<>();
    @Unique
    private final HashSet<Season> validSeasonalComponents = new HashSet<>();

    @Inject(at = @At("TAIL"), method = "<init>")
    public void createSeasonalComponentsMap(CallbackInfo ci) {
        for(Season season : Season.values()) {
            seasonalComponentsMap.put(season, Lists.newArrayList());
        }
    }

    @Inject(at = @At("HEAD"), method = "addComponent")
    public void addSeasonalComponent(Predicate<BlockState> predicate, BakedModel model, CallbackInfo ci) {
        Map<Season, BakedModel> seasonModelMap = FabricSeasonsClient.originalToSeasonModelMap.get(model);
        if(seasonModelMap != null) {
            seasonModelMap.forEach((season, seasonalModel) -> {
                validSeasonalComponents.add(season);
                seasonalComponentsMap.get(season).add(Pair.of(predicate, seasonalModel));
            });
        }else {
            seasonalComponentsMap.forEach((season, list) -> {
                list.add(Pair.of(predicate, model));
            });
        }
    }

    @Inject(at = @At("RETURN"), method = "build")
    public void addSeasonalMultipart(CallbackInfoReturnable<BakedModel> cir) {
        BakedModel bakedModel = cir.getReturnValue();
        Map<Season, BakedModel> seasonModelMap = new HashMap<>();
        seasonalComponentsMap.forEach((season, seasonalComponents) -> {
            if (validSeasonalComponents.contains(season)) {
                seasonModelMap.put(season, new MultipartBakedModel(seasonalComponents));
            }
        });
        if(!seasonModelMap.isEmpty()) {
            FabricSeasonsClient.originalToSeasonModelMap.put(bakedModel, seasonModelMap);
        }
    }


}
