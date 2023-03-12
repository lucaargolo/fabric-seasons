package io.github.lucaargolo.seasons.mixed;

import com.mojang.datafixers.util.Either;
import io.github.lucaargolo.seasons.utils.Season;
import net.minecraft.client.util.SpriteIdentifier;

import java.util.Map;

public interface JsonUnbakedModelMixed {

    Map<Season, Map<String, Either<SpriteIdentifier, String>>> getSeasonalTextureMap();
    void setSeasonalTextureMap(Map<Season, Map<String, Either<SpriteIdentifier, String>>> seasonalTextureMap);
}
