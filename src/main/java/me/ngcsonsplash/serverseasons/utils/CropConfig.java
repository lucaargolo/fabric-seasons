package me.ngcsonsplash.serverseasons.utils;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public class CropConfig {
    public static final Codec<CropConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.FLOAT.fieldOf("spring").forGetter(CropConfig::springModifier),
        Codec.FLOAT.fieldOf("summer").forGetter(CropConfig::summerModifier),
        Codec.FLOAT.fieldOf("fall").forGetter(CropConfig::fallModifier),
        Codec.FLOAT.fieldOf("winter").forGetter(CropConfig::winterModifier)
    ).apply(instance, CropConfig::new));
    public static final PacketCodec<ByteBuf, CropConfig> PACKET_CODEC = PacketCodec.of(CropConfig::toBuf, CropConfig::fromBuf);
    
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

    public void toBuf(ByteBuf buf) {
        buf.writeFloat(springModifier);
        buf.writeFloat(summerModifier);
        buf.writeFloat(fallModifier);
        buf.writeFloat(winterModifier);
    }

    public static CropConfig fromBuf(ByteBuf buf) {
        return new CropConfig(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
    }
    
    public float springModifier() {
        return springModifier;
    }
    
    public float summerModifier() {
        return summerModifier;
    }
    
    public float fallModifier() {
        return fallModifier;
    }
    
    public float winterModifier() {
        return winterModifier;
    }
}