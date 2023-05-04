package io.github.lucaargolo.seasons.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IceBlock;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class SeasonalIceBlock extends IceBlock {

	public SeasonalIceBlock(Settings settings) {
		super(settings);
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		world.setBlockState(pos, Blocks.ICE.getStateWithProperties(state));
	}

	@Override
	public String getTranslationKey() {
		return Blocks.ICE.getTranslationKey();
	}

	@Override
	protected Block asBlock() {
		return Blocks.ICE;
	}

	@Override
	public Item asItem() {
		return Blocks.ICE.asItem();
	}
}