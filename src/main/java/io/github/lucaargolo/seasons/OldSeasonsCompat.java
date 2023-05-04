package io.github.lucaargolo.seasons;

import io.github.lucaargolo.seasons.block.SeasonalIceBlock;
import io.github.lucaargolo.seasons.block.SeasonalSnowBlock;
import io.github.lucaargolo.seasons.utils.ModIdentifier;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;

public class OldSeasonsCompat implements ModInitializer {
	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, new ModIdentifier("ice"), new SeasonalIceBlock(FabricBlockSettings.copyOf(Blocks.ICE)));
		Registry.register(Registry.BLOCK, new ModIdentifier("snow"), new SeasonalSnowBlock(FabricBlockSettings.copyOf(Blocks.SNOW)));
	}
}
