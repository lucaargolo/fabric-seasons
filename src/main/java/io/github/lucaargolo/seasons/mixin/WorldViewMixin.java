package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
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
     */
    @Overwrite
    default Biome getBiome(BlockPos pos) {
        Biome biome = this.getBiomeAccess().getBiome(pos);
        if (this instanceof World) {
            FabricSeasons.injectBiomeSeason(biome, (World) this);
        }
        return biome;
    }

}
