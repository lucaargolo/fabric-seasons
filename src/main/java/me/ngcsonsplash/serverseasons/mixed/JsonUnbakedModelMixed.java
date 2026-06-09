package me.ngcsonsplash.serverseasons.mixed;

import com.mojang.datafixers.util.Either;
import me.ngcsonsplash.serverseasons.utils.Season;
import net.minecraft.client.util.SpriteIdentifier;

import java.util.Map;

public interface JsonUnbakedModelMixed {

    Map<Season, Map<String, Either<SpriteIdentifier, String>>> getSeasonalTextureMap();
    void setSeasonalTextureMap(Map<Season, Map<String, Either<SpriteIdentifier, String>>> seasonalTextureMap);
}
