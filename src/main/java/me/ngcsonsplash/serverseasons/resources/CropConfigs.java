package me.ngcsonsplash.serverseasons.resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import me.ngcsonsplash.serverseasons.FabricSeasons;
import me.ngcsonsplash.serverseasons.utils.CropConfig;
import me.ngcsonsplash.serverseasons.utils.Season;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static me.ngcsonsplash.serverseasons.FabricSeasons.MOD_NAME;

public class CropConfigs implements SimpleSynchronousResourceReloadListener {

    private static final CropConfig DEFAULT = new CropConfig(1.0f, 0.8f, 0.6f, 0f);
    private static CropConfig defaultCropConfig = DEFAULT;
    private static HashMap<Identifier, CropConfig> cropConfigMap = new HashMap<>();

    public static float getSeasonCropMultiplier(Identifier cropIdentifier, Season season) {
        return cropConfigMap.getOrDefault(cropIdentifier, defaultCropConfig).getModifier(season);
    }

    public static void receiveConfig(CropConfig defaultConfig, HashMap<Identifier, CropConfig> configMap) {
        defaultCropConfig = defaultConfig;
        cropConfigMap = configMap;
    }

    public static void clear() {
        defaultCropConfig = DEFAULT;
        cropConfigMap.clear();
    }

    public static CropConfig getDefaultCropConfig() {
        return defaultCropConfig;
    }

    public static void toBuf(RegistryByteBuf buf) {
        buf.writeInt(cropConfigMap.size());
        cropConfigMap.forEach((id, config) -> {
            buf.writeIdentifier(id);
            config.toBuf(buf);
        });
    }
    
    public static HashMap<Identifier, CropConfig> fromBuf(RegistryByteBuf buf) {
        HashMap<Identifier, CropConfig> cropConfigMap = new HashMap<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            cropConfigMap.put(buf.readIdentifier(), CropConfig.fromBuf(buf));
        }
        return cropConfigMap;
    }
    
    public static HashMap<Identifier, CropConfig> getCropConfigMap() {
        return cropConfigMap;
    }
    
    @Override
    public Identifier getFabricId() {
        return FabricSeasons.identifier("crop_configs");
    }

    @Override
    public void reload(ResourceManager manager) {
        try {
            Resource resource = manager.getResource(FabricSeasons.identifier("hardcoded/crop/default.json")).orElseThrow();
            JsonElement input = JsonParser.parseReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            defaultCropConfig = CropConfig.CODEC.parse(JsonOps.INSTANCE, input.getAsJsonObject()).getOrThrow();
        } catch (Exception e) {
            FabricSeasons.LOGGER.error("[" + MOD_NAME + "] Failed to load hardcoded grass colors", e);
        }
        
        cropConfigMap.clear();
        manager.findResources("seasons/crop", id -> id.getPath().endsWith(".json")).forEach((id, resource) -> {
            String[] split = id.getPath().split("/");
            Identifier cropIdentifier = Identifier.of(id.getNamespace(), split[split.length - 1].replace(".json", ""));
            try {
                JsonElement input = JsonParser.parseReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
                CropConfig config = CropConfig.CODEC.parse(JsonOps.INSTANCE, input.getAsJsonObject()).getOrThrow();
                cropConfigMap.put(cropIdentifier, config);
            } catch (Exception e) {
                FabricSeasons.LOGGER.error("[" + MOD_NAME + "] Failed to load crop config for: " + cropIdentifier, e);
            }
        });
        
        if (!cropConfigMap.isEmpty()) {
            FabricSeasons.LOGGER.info("[" + MOD_NAME + "] Successfully loaded {} custom crop configs.", cropConfigMap.size());
        }
    }
}
