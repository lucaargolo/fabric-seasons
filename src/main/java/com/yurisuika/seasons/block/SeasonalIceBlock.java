package com.yurisuika.seasons.block;

import com.yurisuika.seasons.Seasons;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IceBlock;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;

import java.util.Random;

public class SeasonalIceBlock extends IceBlock {

    public SeasonalIceBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getLightLevel(LightType.SKY, pos) > 0 && world.getBiome(pos).getTemperature(pos) >= 0.15F) {
            this.melt(state, world, pos);
        }
        super.randomTick(state, world, pos, random);
    }

    @Override
    public String getTranslationKey() {
        return Seasons.ORIGINAL_ICE.getTranslationKey();
    }

    @Override
    protected Block asBlock() {
        return Seasons.ORIGINAL_ICE;
    }

    @Override
    public Item asItem() {
        return Seasons.ORIGINAL_ICE.asItem();
    }
}
