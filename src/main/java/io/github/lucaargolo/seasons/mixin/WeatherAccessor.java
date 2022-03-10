package io.github.lucaargolo.seasons.mixin;

import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Biome.Weather.class)
public interface WeatherAccessor {

    @Accessor @Final void setPrecipitation(Biome.Precipitation precipitation);

    @Accessor @Final void setTemperature(float temperature);

    @Accessor @Final void setTemperatureModifier(Biome.TemperatureModifier temperatureModifier);

    @Accessor @Final void setDownfall(float downfall);

}
