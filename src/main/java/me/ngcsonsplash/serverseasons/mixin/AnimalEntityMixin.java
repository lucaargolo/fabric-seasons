package me.ngcsonsplash.serverseasons.mixin;

import me.ngcsonsplash.serverseasons.FabricSeasons;
import me.ngcsonsplash.serverseasons.utils.Season;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimalEntity.class)
public class AnimalEntityMixin {

    @Inject(at = @At("HEAD"), method = "breed", cancellable = true)
    public void breedInject(ServerWorld serverWorld, AnimalEntity animalEntity, CallbackInfo info) {
        if(FabricSeasons.getCurrentSeason(serverWorld) == Season.WINTER && !FabricSeasons.CONFIG.doAnimalsBreedInWinter()) {
            info.cancel();
        }
    }

}
