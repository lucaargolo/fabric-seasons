package com.yurisuika.seasons.block;

import com.yurisuika.seasons.Seasons;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SnowBlock;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;

import java.util.Random;

public class SeasonalSnowBlock extends SnowBlock {

    public SeasonalSnowBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getLightLevel(LightType.SKY, pos) > 0 && world.getBiome(pos).getTemperature(pos) >= 0.15F) {
            Block.dropStacks(state, world, pos);
            world.removeBlock(pos, false);
        }
        super.randomTick(state, world, pos, random);
    }

    @Override
    public String getTranslationKey() {
        return Seasons.ORIGINAL_SNOW.getTranslationKey();
    }

    @Override
    protected Block asBlock() {
        return Seasons.ORIGINAL_SNOW;
    }

    @Override
    public Item asItem() {
        return Seasons.ORIGINAL_SNOW.asItem();
    }
}