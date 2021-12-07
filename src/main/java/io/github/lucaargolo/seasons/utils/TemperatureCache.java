package io.github.lucaargolo.seasons.utils;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Cache vanilla / calculated temperature
 * The validity period after calculation is one game day
 *
 * @author 墨涤千尘
 */
public class TemperatureCache {

    private static final Map<String, Float> vanilla = new HashMap<>();

    private static final int day = 1200 * 20;

    private record Cached(float temp, long worldTime) {
        private Cached(float temp, long worldTime) {
            this.temp = temp;
            this.worldTime = worldTime / day;
        }

        public float getTemp() {
            return temp;
        }

        public long getWorldTime() {
            return worldTime;
        }
    }

    private static final Map<String, Cached> calculated = new HashMap<>();

    public static void cacheVanilla(Identifier id, float temp) {
        cacheVanilla(id, temp, false);
    }

    public static void cacheVanilla(Identifier id, float temp, boolean overwrite) {
        if (overwrite || !vanilla.containsKey(id.toString()))
            vanilla.put(id.toString(), temp);
    }

    public static void cacheCalculated(Identifier id, float temp, long worldTime) {
        calculated.put(id.toString(), new Cached(temp, worldTime));
    }

    public static Float getVanilla(Identifier id) {
        if (vanilla.containsKey(id.toString()))
            return vanilla.get(id.toString());
        return null;
    }

    public static Float getCalculated(Identifier id, long worldTime) {
        if (calculated.containsKey(id.toString())) {
            Cached cached = calculated.get(id.toString());
            if (cached.getWorldTime() == worldTime / day)
                return cached.getTemp();
        }
        return null;
    }
}
