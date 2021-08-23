package io.github.lucaargolo.seasons.utils;

import io.github.lucaargolo.seasons.FabricSeasons;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class ConfigSync implements ClientModInitializer, DedicatedServerModInitializer {

    private static boolean DebugClient = true;
    private static boolean InitClientOnConnected = false;

    private static final Identifier ConfigSyncAskPacketID = new ModIdentifier("config_sync_ssk");
    private static final Identifier ConfigSyncResponsePacketID = new ModIdentifier("config_sync_response");

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null){
                if (client.player.age == 1){
                    String playerName = client.player.getEntityName();
                    if (!InitClientOnConnected){
                        //Connected
                        if(DebugClient) client.player.sendMessage(new LiteralText("[Seasons-Client] Asking Server For Config"), false);
                        ClientPlayNetworking.send(ConfigSyncAskPacketID, PacketByteBufs.empty());
                        InitClientOnConnected = true;
                    }
                }
            }
            else{
                if (client.world == null) {
                    InitClientOnConnected = false;
                }
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncResponsePacketID, (client, handler, buf, responseSender) -> {
            String json = buf.readString();

            client.execute(() -> {
                FabricSeasons.CONFIG = FabricSeasons.ConfigFromJson(json);
                if (client.player != null){
                    if(DebugClient) client.player.sendMessage(new LiteralText("[Seasons-Client] Received Config From Server"), false);
                }
            });
        });
    }

    @Override
    public void onInitializeServer() {
        ServerPlayNetworking.registerGlobalReceiver(ConfigSyncAskPacketID, (server,player,serverPlayNetworkHandler,byteBuf,packetSender) -> {
            String json = FabricSeasons.ConfigToJson(FabricSeasons.CONFIG);
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeString(json);
            ServerPlayNetworking.send(player, ConfigSyncResponsePacketID, buf);
            server.sendSystemMessage(new LiteralText("[Seasons-Server] Sent Config To " + player.getEntityName()), player.getUuid());
        });
    }
}
