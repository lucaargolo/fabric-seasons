package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.FabricSeasonsClient;
import io.github.lucaargolo.seasons.utils.Season;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemModels.class)
public class ItemModelsMixin {

    @Inject(at = @At("RETURN"), method = "getModel(Lnet/minecraft/item/Item;)Lnet/minecraft/client/render/model/BakedModel;", cancellable = true)
    public void injectSeasonalModel(Item item, CallbackInfoReturnable<BakedModel> cir) {
        BakedModel originalModel = cir.getReturnValue();
        Season season = FabricSeasons.getCurrentSeason();
        if(FabricSeasonsClient.originalToSeasonModelMap.containsKey(originalModel) && FabricSeasonsClient.originalToSeasonModelMap.get(originalModel).containsKey(season)) {
            cir.setReturnValue(FabricSeasonsClient.originalToSeasonModelMap.get(originalModel).get(season));
        }
    }

}
