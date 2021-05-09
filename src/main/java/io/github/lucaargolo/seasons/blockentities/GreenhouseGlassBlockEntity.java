package io.github.lucaargolo.seasons.blockentities;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.utils.GreenhouseCache;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Tickable;

public class GreenhouseGlassBlockEntity extends BlockEntity implements Tickable {

    public GreenhouseGlassBlockEntity() {
        super(FabricSeasons.GREENHOUSE_GLASS_TYPE);
    }

    @Override
    public void tick() {
        if(world != null && !world.isClient) {
            GreenhouseCache.add(world, pos);
        }
    }

}
