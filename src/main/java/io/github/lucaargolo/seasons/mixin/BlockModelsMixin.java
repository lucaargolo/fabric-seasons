package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.FabricSeasonsClient;
import io.github.lucaargolo.seasons.utils.Season;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockModels.class)
public class BlockModelsMixin {

    @Inject(at = @At("RETURN"), method = "getModel", cancellable = true)
    public void injectSeasonalModel(BlockState state, CallbackInfoReturnable<BakedModel> cir) {
        BakedModel originalModel = cir.getReturnValue();
        Season season = FabricSeasons.getCurrentSeason();
        if(FabricSeasonsClient.originalToSeasonModelMap.containsKey(originalModel) && FabricSeasonsClient.originalToSeasonModelMap.get(originalModel).containsKey(season)) {
            cir.setReturnValue(FabricSeasonsClient.originalToSeasonModelMap.get(originalModel).get(season));
        }
    }

}
