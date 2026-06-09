package me.ngcsonsplash.serverseasons.payload;

import me.ngcsonsplash.serverseasons.FabricSeasons;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record ConfigSyncPacket(String config) implements CustomPayload {
    public static final CustomPayload.Id<ConfigSyncPacket> ID = new CustomPayload.Id<>(FabricSeasons.identifier("config_sync"));
    public static final PacketCodec<RegistryByteBuf, ConfigSyncPacket> CODEC = PacketCodec.tuple(
        PacketCodecs.STRING, ConfigSyncPacket::config,
        ConfigSyncPacket::new
    );
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
