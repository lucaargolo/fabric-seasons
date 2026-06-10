package me.ngcsonsplash.serverseasons.mixin;

import me.ngcsonsplash.serverseasons.ServerSeasons;
import me.ngcsonsplash.serverseasons.ServerSeasonsClient;
import me.ngcsonsplash.serverseasons.utils.Season;
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
        Season season = ServerSeasons.getCurrentSeason();
        if(ServerSeasonsClient.originalToSeasonModelMap.containsKey(originalModel) && ServerSeasonsClient.originalToSeasonModelMap.get(originalModel).containsKey(season)) {
            cir.setReturnValue(ServerSeasonsClient.originalToSeasonModelMap.get(originalModel).get(season));
        }
    }

}
