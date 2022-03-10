package io.github.lucaargolo.seasons.utils;

import io.github.lucaargolo.seasons.FabricSeasons;
import net.minecraft.util.Identifier;

public class ModIdentifier extends Identifier {

    public ModIdentifier(String path) {
        super(FabricSeasons.MOD_ID, path);
    }

}
