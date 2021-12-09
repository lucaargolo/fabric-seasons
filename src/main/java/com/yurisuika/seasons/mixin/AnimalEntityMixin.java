package com.yurisuika.seasons.mixin;

import com.yurisuika.seasons.Seasons;
import com.yurisuika.seasons.utils.Season;
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
        if(Seasons.getCurrentSeason(serverWorld) == Season.EARLY_WINTER && !Seasons.CONFIG.doAnimalsBreedInWinter()) {
            info.cancel();
        }
        else if(Seasons.getCurrentSeason(serverWorld) == Season.MID_WINTER && !Seasons.CONFIG.doAnimalsBreedInWinter()) {
            info.cancel();
        }
        else if(Seasons.getCurrentSeason(serverWorld) == Season.LATE_WINTER && !Seasons.CONFIG.doAnimalsBreedInWinter()) {
            info.cancel();
        }
    }

}
