package com.yurisuika.seasons.utils;

import com.yurisuika.seasons.Seasons;
import net.minecraft.util.Identifier;

public class ModIdentifier extends Identifier {

    public ModIdentifier(String path) {
        super(Seasons.MOD_ID, path);
    }

}
