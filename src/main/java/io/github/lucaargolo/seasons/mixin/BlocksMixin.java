package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.block.SeasonalIceBlock;
import io.github.lucaargolo.seasons.block.SeasonalSnowBlock;
import io.github.lucaargolo.seasons.utils.ModIdentifier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Blocks.class)
public abstract class BlocksMixin {

    @Mutable
    @Shadow @Final public static Block ICE;
    @Mutable
    @Shadow @Final public static Block SNOW;

    @Inject(at = @At("TAIL"), method = "register", cancellable = true)
    private static void onRegisterBlock(String id, Block block, CallbackInfoReturnable<Block> infoReturnable) {
        if(id.equals("ice")) {
            FabricSeasons.ORIGINAL_ICE = infoReturnable.getReturnValue();
            ICE = Registry.register(Registry.BLOCK, new ModIdentifier("seasonal_ice"), new SeasonalIceBlock(AbstractBlock.Settings.of(Material.ICE).slipperiness(0.98F).ticksRandomly().strength(0.5F).sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning((state, world, pos, entityType) -> entityType == EntityType.POLAR_BEAR)));
            infoReturnable.setReturnValue(ICE);
        }else if(id.equals("snow")) {
            FabricSeasons.ORIGINAL_SNOW = infoReturnable.getReturnValue();
            SNOW = Registry.register(Registry.BLOCK, new ModIdentifier("seasonal_snow"), new SeasonalSnowBlock(AbstractBlock.Settings.of(Material.SNOW_LAYER).ticksRandomly().strength(0.1F).requiresTool().sounds(BlockSoundGroup.SNOW).nonOpaque()));
            infoReturnable.setReturnValue(SNOW);
        }
    }


}
