package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WorldView.class)
public interface WorldViewMixin {

    @Shadow BiomeAccess getBiomeAccess();

    /**
     * @author D4rkness_King
     * @reason I need to change how all the biomes are perceived by Minecraft,
     *   without actually changing the original values so that i dont screw up
     *   world generation. Upon extensive testing, i found out that this works.
     */
    @Overwrite
    default RegistryEntry<Biome> getBiome(BlockPos pos) {
        RegistryEntry<Biome> biomeEntry = this.getBiomeAccess().getBiome(pos);
        if (this instanceof World) {
            FabricSeasons.injectBiomeTemperature(biomeEntry, (World) this);
        }
        return biomeEntry;
    }

}
