package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.utils.Meltable;
import io.github.lucaargolo.seasons.utils.ReplacedMeltablesState;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnowBlock.class)
public abstract class SnowBlockMixin extends Block implements Meltable {

    public SnowBlockMixin(Settings settings) {
        super(settings);
    }


    @Inject(at = @At("HEAD"), method = "randomTick")
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (world.getLightLevel(LightType.SKY, pos) > 0 && world.getBiome(pos).value().getTemperature(pos) >= 0.15F && !FabricSeasons.getPlacedMeltablesState(world).isManuallyPlaced(pos)) {
            Block.dropStacks(state, world, pos);
            BlockState replacedState = FabricSeasons.getReplacedMeltablesState(world).getReplaced(pos);
            if(replacedState != null && world.canPlace(replacedState, pos, ShapeContext.absent())) {
                world.setBlockState(pos, replacedState);
            }else{
                world.removeBlock(pos, false);
            }
        }
    }

}
