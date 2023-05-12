package io.github.lucaargolo.seasons.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.lucaargolo.seasons.FabricSeasons;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.nbt.*;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import static io.github.lucaargolo.seasons.FabricSeasons.MOD_NAME;

public class CompatWarnState {

    private static CompatWarnState instance = null;

    private final MinecraftClient client;

    private final HashSet<ModInfo> availableCompatPacks;

    private final HashSet<String> alreadyWarned;

    private final HashSet<ModInfo> toWarn;

    private boolean dirty = false;



    private CompatWarnState(MinecraftClient client, HashSet<String> alreadyWarned) {
        this.client = client;
        this.availableCompatPacks = new HashSet<>();
        try {
            try (CloseableHttpClient http = HttpClients.createDefault()) {
                HttpGet request = new HttpGet("https://gist.githubusercontent.com/lucaargolo/abfd0edbcf7340e6f8bf32698a8d2d57/raw/fabric-seasons-compat.json");
                try (CloseableHttpResponse response = http.execute(request)) {
                    JsonElement element = JsonParser.parseString(EntityUtils.toString(response.getEntity()));
                    for (JsonElement modInfoElement : element.getAsJsonArray()) {
                        availableCompatPacks.add(FabricSeasons.GSON.fromJson(modInfoElement, ModInfo.class));
                    }
                }
            }
        }catch (Exception e) {
            FabricSeasons.LOGGER.error("["+MOD_NAME+"] Failed to request compatibility mods list.", e);
        }
        this.alreadyWarned = alreadyWarned;
        this.toWarn = new HashSet<>();
        HashSet<String> availableNamespaces = new HashSet<>();
        ClientPlayNetworkHandler handler = client.getNetworkHandler();
        if(handler != null) {
            handler.getRegistryManager().get(Registry.BIOME_KEY).getIndexedEntries().forEach(entry -> {
                entry.getKey().ifPresent(key -> availableNamespaces.add(key.getValue().getNamespace()));
            });
            FabricSeasons.SEEDS_MAP.forEach((item, block) -> {
                availableNamespaces.add(Registry.BLOCK.getId(block).getNamespace());
            });
        }
        availableNamespaces.stream().filter(namespace -> !alreadyWarned.contains(namespace)).forEach((namespace) -> {
            availableCompatPacks.stream().filter(info -> info.mods.contains(namespace)).forEach(toWarn::add);
        });
    }
    
    private void saveState() {
        NbtCompound nbt = new NbtCompound();
        NbtList list = new NbtList();
        alreadyWarned.forEach(s -> list.add(NbtString.of(s)));
        nbt.put("list", list);
        File compatWarnFile = new File(MinecraftClient.getInstance().runDirectory, File.separator+"data"+File.separator+"seasons_compat_warn.nbt");
        try {
            Boolean ignored = compatWarnFile.getParentFile().mkdirs();
            Boolean ignored2 = compatWarnFile.createNewFile();
            NbtIo.writeCompressed(nbt, compatWarnFile);
        } catch (IOException e) {
            FabricSeasons.LOGGER.error("["+MOD_NAME+"] Failed to save season compat warn state.", e);
        }
    }

    public static CompatWarnState loadState(MinecraftClient client) {
        File compatWarnFile = new File(MinecraftClient.getInstance().runDirectory, File.separator+"data"+File.separator+"seasons_compat_warn.nbt");
        HashSet<String> alreadyWarned = new HashSet<>();
        NbtCompound nbt;
        try {
            nbt = NbtIo.readCompressed(compatWarnFile);
        } catch (Exception e) {
            nbt = new NbtCompound();
        }
        NbtElement element = nbt.get("list");
        if(element instanceof NbtList list) {
            list.forEach(listElement -> {
                if(listElement instanceof NbtString listString) {
                    alreadyWarned.add(listString.asString());
                }
            });
        }
        return new CompatWarnState(client, alreadyWarned);
    }

    public static CompatWarnState getInstance(MinecraftClient client) {
        if(instance == null) {
            instance = loadState(client);
        }
        return instance;
    }

    public static void join(MinecraftClient client) {
        getInstance(client).join();
    }

    public void join() {
        toWarn.forEach(info -> {
            if(!alreadyWarned.contains(info.id) && !FabricLoader.getInstance().isModLoaded(info.id)) {
                ClientPlayerEntity player = client.player;
                if(player != null) {
                    MutableText first, second;
                    if(!info.mods.contains("minecraft")) {
                        first = Text.literal("\n").append(Text.translatable("chat.seasons.mod_installed", Text.literal(info.name).formatted(Formatting.GREEN)).formatted(Formatting.YELLOW));
                        second = Text.literal(("\n§e"+Text.translatable("chat.seasons.compatibility").getString()).replace("Fabric Seasons", "§aFabric Seasons§e")+"\n");
                    }else{
                        first = Text.literal("\n").append(Text.translatable("chat.seasons.mod_not_installed", Text.literal(info.name).formatted(Formatting.RED)).formatted(Formatting.YELLOW));
                        second = Text.literal(("\n§e"+Text.translatable("chat.seasons.extras").getString())+"\n");
                    }
                    MutableText third = Text.literal("§e"+Text.translatable("chat.seasons.available_at").getString());
                    MutableText curse = Text.literal("§6§nCurseForge§r ").styled(s -> s.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.curseforge.com/minecraft/mc-mods/"+info.url)));
                    MutableText modrinth = Text.literal("§2§nModrinth§r ").styled(s -> s.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://modrinth.com/mod/"+info.url)));
                    MutableText github = Text.literal("§5§nGitHub§r\n").styled(s -> s.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/lucaargolo/"+info.url+"/releases")));
                    MutableText fourth = Text.literal("§e"+Text.translatable("chat.seasons.show_once").getString()+"\n");
                    player.sendMessage(first.append(second).append(third).append(curse).append(modrinth).append(github).append(fourth));
                }
                alreadyWarned.add(info.id);
                dirty = true;
            }
        });
        if(dirty) {
            dirty = false;
            saveState();
        }
    }

    @SuppressWarnings({"unused", "MismatchedQueryAndUpdateOfCollection"})
    public static class ModInfo {
        private List<String> mods;
        private String id;
        private String url;
        private String name;

    }

}
