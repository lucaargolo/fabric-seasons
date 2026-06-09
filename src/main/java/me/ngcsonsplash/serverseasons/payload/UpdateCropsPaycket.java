package me.ngcsonsplash.serverseasons.payload;

import me.ngcsonsplash.serverseasons.FabricSeasons;
import me.ngcsonsplash.serverseasons.utils.CropConfig;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public record UpdateCropsPaycket(CropConfig cropConfig, HashMap<Identifier, CropConfig> cropConfigMap) implements CustomPayload {
    public static final CustomPayload.Id<UpdateCropsPaycket> ID = new CustomPayload.Id<>(FabricSeasons.identifier("update_crops"));
    
    public static final PacketCodec<RegistryByteBuf, UpdateCropsPaycket> CODEC = PacketCodec.tuple(
        CropConfig.PACKET_CODEC, UpdateCropsPaycket::cropConfig,
        PacketCodecs.map(HashMap::new, Identifier.PACKET_CODEC, CropConfig.PACKET_CODEC), UpdateCropsPaycket::cropConfigMap,
        UpdateCropsPaycket::new
    );
    
    public static UpdateCropsPaycket fromConfig(CropConfig config, HashMap<Identifier, CropConfig> configMap) {
        return new UpdateCropsPaycket(config, configMap);
    }
    
    @Override
    public CustomPayload.Id<UpdateCropsPaycket> getId() {
        return ID;
    }
}
