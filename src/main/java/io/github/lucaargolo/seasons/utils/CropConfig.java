package io.github.lucaargolo.seasons.utils;

import com.google.gson.JsonElement;
import net.minecraft.network.PacketByteBuf;

public class CropConfig {
    private final float springModifier;
    private final float summerModifier;
    private final float fallModifier;
    private final float winterModifier;

    public CropConfig(float springModifier, float summerModifier, float fallModifier, float winterModifier) {
        this.springModifier = springModifier;
        this.summerModifier = summerModifier;
        this.fallModifier = fallModifier;
        this.winterModifier = winterModifier;
    }

    public CropConfig(JsonElement json) {
        this.springModifier = getStringModifier(json.getAsJsonObject().get("spring").getAsString());
        this.summerModifier = getStringModifier(json.getAsJsonObject().get("summer").getAsString());
        this.fallModifier = getStringModifier(json.getAsJsonObject().get("fall").getAsString());
        this.winterModifier = getStringModifier(json.getAsJsonObject().get("winter").getAsString());
    }

    private float getStringModifier(String modifier) {
        return Float.parseFloat(modifier);
    }

    public float getModifier(Season season) {
        return switch (season) {
            case SPRING -> springModifier;
            case SUMMER -> summerModifier;
            case FALL -> fallModifier;
            case WINTER -> winterModifier;
        };
    }

    public void toBuf(PacketByteBuf buf) {
        buf.writeFloat(springModifier);
        buf.writeFloat(summerModifier);
        buf.writeFloat(fallModifier);
        buf.writeFloat(winterModifier);
    }

    public static CropConfig fromBuf(PacketByteBuf buf) {
        return new CropConfig(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

}