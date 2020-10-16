package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.colors.SeasonFoliageColors;
import io.github.lucaargolo.seasons.colors.SeasonGrassColors;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.world.BiomeColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BlockColors.class)
public class BlockColorsMixin {

    /**
     * @author D4rkness_King
     */
    @Overwrite()
    public static BlockColors create() {
        BlockColors blockColors = new BlockColors();

        blockColors.registerColorProvider((state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColors.getGrassColor(world, state.get(TallPlantBlock.HALF) == DoubleBlockHalf.UPPER ? pos.down() : pos) : -1, Blocks.LARGE_FERN, Blocks.TALL_GRASS);

        blockColors.registerColorProperty(TallPlantBlock.HALF, Blocks.LARGE_FERN, Blocks.TALL_GRASS);

        blockColors.registerColorProvider((state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColors.getGrassColor(world, pos) : SeasonGrassColors.getColor(FabricSeasons.getCurrentSeason(), 0.5D, 1.0D), Blocks.GRASS_BLOCK, Blocks.FERN, Blocks.GRASS, Blocks.POTTED_FERN);

        blockColors.registerColorProvider((state, world, pos, tintIndex) -> SeasonFoliageColors.getSpruceColor(FabricSeasons.getCurrentSeason()), Blocks.SPRUCE_LEAVES);

        blockColors.registerColorProvider((state, world, pos, tintIndex) -> SeasonFoliageColors.getBirchColor(FabricSeasons.getCurrentSeason()), Blocks.BIRCH_LEAVES);

        blockColors.registerColorProvider((state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColors.getFoliageColor(world, pos) : SeasonFoliageColors.getDefaultColor(FabricSeasons.getCurrentSeason()), Blocks.OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.VINE);

        blockColors.registerColorProvider((state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColors.getWaterColor(world, pos) : -1, Blocks.WATER, Blocks.BUBBLE_COLUMN, Blocks.CAULDRON);

        blockColors.registerColorProvider((state, world, pos, tintIndex) -> RedstoneWireBlock.getWireColor(state.get(RedstoneWireBlock.POWER)), Blocks.REDSTONE_WIRE);

        blockColors.registerColorProperty(RedstoneWireBlock.POWER, Blocks.REDSTONE_WIRE);

        blockColors.registerColorProvider((state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColors.getGrassColor(world, pos) : -1, Blocks.SUGAR_CANE);

        blockColors.registerColorProvider((state, world, pos, tintIndex) -> 14731036, Blocks.ATTACHED_MELON_STEM, Blocks.ATTACHED_PUMPKIN_STEM);

        blockColors.registerColorProvider((state, world, pos, tintIndex) -> {
            int i = state.get(StemBlock.AGE);
            int j = i * 32;
            int k = 255 - i * 8;
            int l = i * 4;
            return j << 16 | k << 8 | l;
        }, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);

        blockColors.registerColorProperty(StemBlock.AGE, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);

        blockColors.registerColorProvider((state, world, pos, tintIndex) -> world != null && pos != null ? 2129968 : 7455580, Blocks.LILY_PAD);

        return blockColors;
    }

}
