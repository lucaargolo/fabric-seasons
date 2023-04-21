package io.github.lucaargolo.seasons.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.resources.CropConfigs;
import io.github.lucaargolo.seasons.utils.Season;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class SeasonDebugCommand {

    @SuppressWarnings("deprecation")
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistry)  {
        dispatcher.register(ClientCommandManager.literal("season_debug")
            .then(ClientCommandManager.literal("create_all_crops").executes(context -> {
                AtomicInteger index = new AtomicInteger();
                FabricSeasons.SEEDS_MAP.values().forEach(block -> {
                    Identifier cropId = Registries.BLOCK.getId(block);
                    index.getAndIncrement();
                    createCrop(cropId);
                });
                context.getSource().sendFeedback(Text.literal("Successfully created "+index.get()+" crop entries."));
                return 1;
            }))
            .then(ClientCommandManager.literal("create_biome_translations").executes(context -> {
                AtomicInteger index = new AtomicInteger();
                ClientPlayerEntity player = context.getSource().getPlayer();
                player.getWorld().getRegistryManager().get(RegistryKeys.BIOME).getIndexedEntries().forEach(biomeEntry -> {
                    biomeEntry.getKey().ifPresent(biomeKey -> {
                        if(createTranslation(biomeKey.getValue())) {
                            index.getAndIncrement();
                        }
                    });
                });
                context.getSource().sendFeedback(Text.literal("Successfully created "+index.get()+" biome translations."));
                return 1;
            }))
            .then(ClientCommandManager.literal("create_block_variants")
                .then(ClientCommandManager.argument("block", BlockStateArgumentType.blockState(commandRegistry)).executes(context -> {
                    BlockState state = context.getArgument("block", BlockStateArgument.class).getBlockState();
                    Optional<RegistryKey<Block>> optional = state.getRegistryEntry().getKey();
                    if(optional.isPresent()) {
                        MinecraftClient client = MinecraftClient.getInstance();
                        Identifier blockId = optional.get().getValue();
                        Optional<Resource> optional2 = client.getResourceManager().getResource(new Identifier(blockId.getNamespace(), "blockstates/"+blockId.getPath()+".json"));
                        if(optional2.isPresent()) {
                            Resource blockState = optional2.get();
                            try {
                                HashSet<Identifier> models = collectModels(new HashSet<>(), JsonParser.parseReader(new InputStreamReader(blockState.getInputStream(), StandardCharsets.UTF_8)));
                                models.forEach((model) -> {
                                    Identifier modelId = new Identifier(model.getNamespace(), "models/"+model.getPath()+".json");
                                    Optional<Resource> optional3 = client.getResourceManager().getResource(modelId);
                                    if(optional3.isPresent()) {
                                        Resource blockModel = optional3.get();
                                        try {
                                            HashMap<String, Identifier> textures = collectTextures(new HashMap<>(), client.getResourceManager(), JsonParser.parseReader(new InputStreamReader(blockModel.getInputStream(), StandardCharsets.UTF_8)));
                                            textures.forEach((name, id) -> {
                                                Optional<Resource> optional4 = client.getResourceManager().getResource(new Identifier(id.getNamespace(), "textures/"+id.getPath()+".png"));
                                                if(optional4.isPresent()) {
                                                    Resource texture = optional4.get();
                                                    try {
                                                        copyTexture(id, texture.getInputStream());
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                            createTextureIndex(modelId, textures);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                    return 1;
                })
            ))
            .then(ClientCommandManager.literal("create_item_variants")
                .then(ClientCommandManager.argument("item", ItemStackArgumentType.itemStack(commandRegistry)).executes(context -> {
                    Item item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
                    Optional<RegistryKey<Item>> optional = item.getRegistryEntry().getKey();
                    if(optional.isPresent()) {
                        MinecraftClient client = MinecraftClient.getInstance();
                        Identifier itemId = optional.get().getValue();
                        Identifier modelId = new Identifier(itemId.getNamespace(), "models/item/"+itemId.getPath()+".json");
                        Optional<Resource> optional2 = client.getResourceManager().getResource(modelId);
                        if(optional2.isPresent()) {
                            Resource itemModel = optional2.get();
                            try {
                                HashMap<String, Identifier> textures = collectTextures(new HashMap<>(), client.getResourceManager(), JsonParser.parseReader(new InputStreamReader(itemModel.getInputStream(), StandardCharsets.UTF_8)));
                                textures.forEach((name, id) -> {
                                    Optional<Resource> optional4 = client.getResourceManager().getResource(new Identifier(id.getNamespace(), "textures/"+id.getPath()+".png"));
                                    if(optional4.isPresent()) {
                                        Resource texture = optional4.get();
                                        try {
                                            copyTexture(id, texture.getInputStream());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                createTextureIndex(modelId, textures);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return 1;
                })
            ))
            .then(ClientCommandManager.literal("print_biomes").executes(context -> {
                ClientPlayerEntity player = context.getSource().getPlayer();
                List<RegistryEntry<Biome>> entries = new ArrayList<>();
                player.getWorld().getRegistryManager().get(RegistryKeys.BIOME).getIndexedEntries().forEach(entries::add);
                entries.sort(Comparator.comparing(entry -> entry.getKey().orElse(RegistryKey.of(RegistryKeys.BIOME, new Identifier("missingno"))).getValue().toString()));
                AtomicReference<String> str = new AtomicReference<>("\n");
                entries.forEach((entry) -> str.updateAndGet(s -> s + entry.getKey().orElse(RegistryKey.of(RegistryKeys.BIOME, new Identifier("missingno"))).getValue().toString() + "\n"));
                System.out.println(str.get());
                return 1;
            }))
            .then(ClientCommandManager.literal("create_foliage_color")
                .then(ClientCommandManager.argument("color", IntegerArgumentType.integer()).executes(context -> {
                    int color = IntegerArgumentType.getInteger(context, "color");
                    ClientPlayerEntity player = context.getSource().getPlayer();;
                    Season season = FabricSeasons.getCurrentSeason(player.getWorld());
                    RegistryEntry<Biome> entry = player.getWorld().getBiome(player.getBlockPos());
                    Identifier id = entry.getKey().orElseThrow().getValue();
                    context.getSource().sendFeedback(setBiomeColor(id, season, color, "foliage"));
                    return 1;
                }))
                .then(ClientCommandManager.argument("hex_color", StringArgumentType.string()).executes(context -> {
                    String hexColor = StringArgumentType.getString(context, "hex_color");
                    int color = Integer.parseInt(hexColor, 16);
                    ClientPlayerEntity player = context.getSource().getPlayer();
                    Season season = FabricSeasons.getCurrentSeason(player.getWorld());
                    RegistryEntry<Biome> entry = player.getWorld().getBiome(player.getBlockPos());
                    Identifier id = entry.getKey().orElseThrow().getValue();
                    context.getSource().sendFeedback(setBiomeColor(id, season, color, "foliage"));
                    return 1;
                }))
                .executes(context -> {
                    ClientPlayerEntity player = context.getSource().getPlayer();
                    Season season = FabricSeasons.getCurrentSeason(player.getWorld());
                    RegistryEntry<Biome> entry = player.getWorld().getBiome(player.getBlockPos());
                    Identifier id = entry.getKey().orElseThrow().getValue();
                    Biome biome = entry.value();
                    context.getSource().sendFeedback(setBiomeColor(id, season, biome.getFoliageColor(), "foliage"));
                    return 1;
                })
            )
            .then(ClientCommandManager.literal("create_grass_color")
                .then(ClientCommandManager.argument("color", IntegerArgumentType.integer()).executes(context -> {
                    int color = IntegerArgumentType.getInteger(context, "color");
                    ClientPlayerEntity player = context.getSource().getPlayer();
                    Season season = FabricSeasons.getCurrentSeason(player.getWorld());
                    RegistryEntry<Biome> entry = player.getWorld().getBiome(player.getBlockPos());
                    Identifier id = entry.getKey().orElseThrow().getValue();
                    context.getSource().sendFeedback(setBiomeColor(id, season, color, "grass"));
                    return 1;
                }))
                .then(ClientCommandManager.argument("hex_color", StringArgumentType.string()).executes(context -> {
                    String hexColor = StringArgumentType.getString(context, "hex_color");
                    int color = Integer.parseInt(hexColor, 16);
                    ClientPlayerEntity player = context.getSource().getPlayer();
                    Season season = FabricSeasons.getCurrentSeason(player.getWorld());
                    RegistryEntry<Biome> entry = player.getWorld().getBiome(player.getBlockPos());
                    Identifier id = entry.getKey().orElseThrow().getValue();
                    context.getSource().sendFeedback(setBiomeColor(id, season, color, "grass"));
                    return 1;
                }))
                .executes(context -> {
                    ClientPlayerEntity player = context.getSource().getPlayer();
                    Season season = FabricSeasons.getCurrentSeason(player.getWorld());
                    RegistryEntry<Biome> entry = player.getWorld().getBiome(player.getBlockPos());
                    Identifier id = entry.getKey().orElseThrow().getValue();
                    Biome biome = entry.value();
                    context.getSource().sendFeedback(setBiomeColor(id, season, biome.getGrassColorAt(player.getX(), player.getZ()), "grass"));
                    return 1;
                })
            )
        );
    }

    private static HashMap<String, Identifier> collectTextures(HashMap<String, Identifier> map, ResourceManager manager, JsonElement element) throws IOException {
        JsonObject model = element.getAsJsonObject();
        if(model.has("parent")) {
            Identifier parent = new Identifier(model.get("parent").getAsString());
            Optional<Resource> optional = manager.getResource(new Identifier(parent.getNamespace(), "models/"+parent.getPath()+".json"));
            if(optional.isPresent()) {
                Resource parentModel = optional.get();
                collectTextures(map, manager, JsonParser.parseReader(new InputStreamReader(parentModel.getInputStream(), StandardCharsets.UTF_8)));
            }
        }
        if(model.has("textures")) {
            JsonObject textures = model.getAsJsonObject("textures");
            textures.entrySet().forEach((entry) -> {
                if(Identifier.isValid(entry.getValue().getAsString())) {
                    map.put(entry.getKey(), new Identifier(entry.getValue().getAsString()));
                }
            });
        }
        return map;
    }

    private static HashSet<Identifier> collectModels(HashSet<Identifier> set, JsonElement element) {
        if(element.isJsonObject()) {
            element.getAsJsonObject().entrySet().forEach((entry) -> {
                if(Objects.equals(entry.getKey(), "model") && entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isString()) {
                    set.add(new Identifier(entry.getValue().getAsString()));
                }else{
                    collectModels(set, entry.getValue());
                }
            });
        }else if(element.isJsonArray()) {
            element.getAsJsonArray().forEach((innerElement) -> {
                collectModels(set, innerElement);
            });
        }
        return set;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void createTextureIndex(Identifier id, HashMap<String, Identifier> textureMap)  {
        Path gamePath = FabricLoader.getInstance().getGameDir();
        String[] split = id.getPath().split("/");
        String last = split[split.length-1];
        split[split.length-1] = "";
        String join = String.join(File.separator, split);
        File resourcePath = new File(gamePath + File.separator + "resourcepacks" + File.separator + "seasons_debug" + File.separator + "assets" + File.separator + id.getNamespace() + File.separator + "seasons" + File.separator + join.substring(0, join.length()-1));
        resourcePath.mkdirs();
        File textureFile = new File(resourcePath.getPath() + File.separator + last);
        JsonObject textureJson = new JsonObject();
        try {
            textureFile.createNewFile();
            for(Season s : Season.values()) {
                if(s != Season.SPRING) {
                    JsonObject textureMapJson = new JsonObject();

                    textureMap.forEach((key, value) -> {
                        String[] innerSplit = value.getPath().split("/");
                        String innerLast = innerSplit[innerSplit.length-1];
                        innerSplit[innerSplit.length-1] = "";
                        String innerJoin = String.join("/", innerSplit);
                        textureMapJson.add(key, new JsonPrimitive(new Identifier(value.getNamespace(),innerJoin + s.name().toLowerCase(Locale.ROOT) + "_" + innerLast).toString()));
                    });
                    textureJson.add(s.name().toLowerCase(Locale.ROOT), textureMapJson);
                }
            }
            JsonObject f = new JsonObject();
            f.add("textures", textureJson);
            String json = FabricSeasons.GSON.toJson(f);
            try (PrintWriter out = new PrintWriter(textureFile)) {
                out.println(json);
            }
        }catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void copyTexture(Identifier id, InputStream inputStream) {
        List<File> files = new ArrayList<>();
        for(Season s : Season.values()) {
            if(s != Season.SPRING) {
                Path gamePath = FabricLoader.getInstance().getGameDir();
                String[] split = id.getPath().split("/");
                String last = split[split.length-1];
                split[split.length-1] = "";
                String join = String.join(File.separator, split);
                File resourcePath = new File(gamePath + File.separator + "resourcepacks" + File.separator + "seasons_debug" + File.separator + "assets" + File.separator + id.getNamespace() + File.separator + "textures" + File.separator + join.substring(0, join.length()-1));
                resourcePath.mkdirs();
                File textureFile = new File(resourcePath.getPath() + File.separator + s.name().toLowerCase(Locale.ROOT) + "_" + last + ".png");
                files.add(textureFile);
            }
        }
        try {
            List<FileOutputStream> streams = new ArrayList<>();
            for(File f : files) {
                if(f.createNewFile()) {
                    streams.add(new FileOutputStream(f));
                }
            }
            int read;
            byte[] bytes = new byte[8192];
            while ((read = inputStream.read(bytes)) != -1) {
                for(FileOutputStream s : streams) {
                    s.write(bytes, 0, read);
                }
            }
            for(FileOutputStream s : streams) {
                s.close();
            }
        }catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static Text setBiomeColor(Identifier id, Season season, int color, String type)  {
        Path gamePath = FabricLoader.getInstance().getGameDir();
        File resourcePath = new File(gamePath + File.separator + "resourcepacks" + File.separator + "seasons_debug" + File.separator + "assets" + File.separator + id.getNamespace() + File.separator + "seasons" + File.separator + type);
        resourcePath.mkdirs();
        File biomeFile = new File(resourcePath.getPath() + File.separator + id.getPath() + ".json");
        JsonObject biomeJson;
        try {
            if (biomeFile.createNewFile()) {
                biomeJson = new JsonObject();
                biomeJson.add(Season.SPRING.name().toLowerCase(Locale.ROOT), new JsonPrimitive(color));
                biomeJson.add(Season.SUMMER.name().toLowerCase(Locale.ROOT), new JsonPrimitive(color));
                biomeJson.add(Season.FALL.name().toLowerCase(Locale.ROOT), new JsonPrimitive(color));
                biomeJson.add(Season.WINTER.name().toLowerCase(Locale.ROOT), new JsonPrimitive(color));
            } else {
                biomeJson = JsonParser.parseString(new String(Files.readAllBytes(biomeFile.toPath()))).getAsJsonObject();
            }
            biomeJson.add(season.name().toLowerCase(Locale.ROOT), new JsonPrimitive(color));
            String json = FabricSeasons.GSON.toJson(biomeJson);
            try (PrintWriter out = new PrintWriter(biomeFile)) {
                out.println(json);
            }
        }catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        return Text.literal("Successfully set "+id+" "+type+" color to "+color+" at "+biomeFile.getPath()).styled(style -> style.withColor(color).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, Integer.toHexString(color))));
    }

    private static void createCrop(Identifier id)  {
        Path gamePath = FabricLoader.getInstance().getGameDir();
        File dataPath = new File(gamePath + File.separator + "datapacks" + File.separator + "seasons_debug" + File.separator + "data" + File.separator + id.getNamespace() + File.separator + "seasons" + File.separator + "crop");
        dataPath.mkdirs();
        File cropFile = new File(dataPath.getPath() + File.separator + id.getPath() + ".json");
        JsonObject cropJson;
        try {
            if (cropFile.createNewFile()) {
                cropJson = new JsonObject();
                cropJson.add(Season.SPRING.name().toLowerCase(Locale.ROOT), new JsonPrimitive(CropConfigs.getSeasonCropMultiplier(id, Season.SPRING)));
                cropJson.add(Season.SUMMER.name().toLowerCase(Locale.ROOT), new JsonPrimitive(CropConfigs.getSeasonCropMultiplier(id, Season.SUMMER)));
                cropJson.add(Season.FALL.name().toLowerCase(Locale.ROOT), new JsonPrimitive(CropConfigs.getSeasonCropMultiplier(id, Season.FALL)));
                cropJson.add(Season.WINTER.name().toLowerCase(Locale.ROOT), new JsonPrimitive(CropConfigs.getSeasonCropMultiplier(id, Season.WINTER)));
            } else {
                cropJson = JsonParser.parseString(new String(Files.readAllBytes(cropFile.toPath()))).getAsJsonObject();
            }
            String json = FabricSeasons.GSON.toJson(cropJson);
            try (PrintWriter out = new PrintWriter(cropFile)) {
                out.println(json);
            }
        }catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private static boolean createTranslation(Identifier id)  {
        Path gamePath = FabricLoader.getInstance().getGameDir();
        String translationKey = "biome."+id.getNamespace()+"."+id.getPath();
        String[] split = id.getPath().split("_");
        for(int i = 0; i < split.length; i++) {
            String s = split[i];
            String[] innerSplit = s.split("/");
            s = innerSplit[0];
            char c = s.charAt(0);
            s = s.replaceFirst(String.valueOf(c), String.valueOf(Character.toUpperCase(c)));
            split[i] = s;
        }
        String translation = String.join(" ", split);
        if(I18n.hasTranslation(translationKey)) {
            return false;
        }
        File dataPath = new File(gamePath + File.separator + "resourcepacks" + File.separator + "seasons_debug" + File.separator + "assets" + File.separator + id.getNamespace());
        dataPath.mkdirs();
        File langFile = new File(dataPath.getPath() + File.separator + "en_us.json");
        JsonObject langJson;
        try {
            if (langFile.createNewFile()) {
                langJson = new JsonObject();
            } else {
                langJson = JsonParser.parseString(new String(Files.readAllBytes(langFile.toPath()))).getAsJsonObject();
            }
            if(!langJson.has(translationKey)) {
                langJson.add(translationKey, new JsonPrimitive(translation));
                String json = FabricSeasons.GSON.toJson(langJson);
                try (PrintWriter out = new PrintWriter(langFile)) {
                    out.println(json);
                }
            }
        }catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        return true;
    }

}
