package io.github.lucaargolo.seasons.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class SeasonalSnowBlock extends SnowBlock {

	public SeasonalSnowBlock(Settings settings) {
		super(settings);
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		world.setBlockState(pos, Blocks.SNOW.getStateWithProperties(state));
	}

	@Override
	public String getTranslationKey() {
		return Blocks.SNOW.getTranslationKey();
	}

	@Override
	protected Block asBlock() {
		return Blocks.SNOW;
	}

	@Override
	public Item asItem() {
		return Blocks.SNOW.asItem();
	}
}