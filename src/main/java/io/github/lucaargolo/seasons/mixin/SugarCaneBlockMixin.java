package io.github.lucaargolo.seasons.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SugarCaneBlock;

@Mixin(SugarCaneBlock.class)
public abstract class SugarCaneBlockMixin 
{
	@Redirect( method="canPlaceAt", at=@At(value="INVOKE", ordinal=3, target="net/minecraft/block/BlockState.isOf (Lnet/minecraft/block/Block;)Z") )
	boolean allowIce(BlockState blockState, Block blockType){
		return blockState.isOf(Blocks.ICE) || blockState.isOf(blockType);
	}
}
