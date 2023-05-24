package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.resources.CropConfigs;
import io.github.lucaargolo.seasons.utils.Season;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(at = @At("HEAD"), method = "appendTooltip")
    public void appendTooltipInject(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        if(FabricSeasons.CONFIG.isSeasonMessingCrops()) {
            Season season = FabricSeasons.getCurrentSeason();
            Item item = stack.getItem();
            Block block = FabricSeasons.SEEDS_MAP.getOrDefault(item, null);
            if (block != null) {
                Identifier cropIdentifier = Registries.BLOCK.getId(block);
                float multiplier = CropConfigs.getSeasonCropMultiplier(cropIdentifier, season);
                if (multiplier == 0f) {
                    tooltip.add(Text.translatable("tooltip.seasons.not_grow").formatted(Formatting.RED));
                } else if (multiplier < 1.0f) {
                    tooltip.add(Text.translatable("tooltip.seasons.slowed_grow").formatted(Formatting.GOLD));
                } else if (multiplier == 1.0f) {
                    tooltip.add(Text.translatable("tooltip.seasons.normal_grow").formatted(Formatting.GREEN));
                } else {
                    tooltip.add(Text.translatable("tooltip.seasons.faster_grow").formatted(Formatting.LIGHT_PURPLE));
                }
                MinecraftClient client = MinecraftClient.getInstance();
                long handle = client.getWindow().getHandle();
                KeyBinding sneakKey = client.options.sneakKey;
                InputUtil.Key boundKey = sneakKey.boundKey;
                boolean sneak = false;
                if(boundKey.getCategory() == InputUtil.Type.MOUSE) {
                    sneak = GLFW.glfwGetMouseButton(handle, boundKey.getCode()) == 1;
                }else if(boundKey.getCategory() == InputUtil.Type.KEYSYM) {
                    sneak = GLFW.glfwGetKey(handle, boundKey.getCode()) == 1;
                }
                if (sneak) {
                    for (Season s : Season.values()) {
                        if(season == s) {
                            MutableText text = Text.translatable(s.getTranslationKey()).formatted(s.getFormatting(), Formatting.UNDERLINE);
                            MutableText multiplierText = Text.literal(String.format("%.1f", (CropConfigs.getSeasonCropMultiplier(cropIdentifier, s) * 100)) + "% speed");
                            tooltip.add(text.append(Text.literal(": ").append(multiplierText.formatted(s.getFormatting()))));
                        }else{
                            MutableText text = Text.translatable(s.getTranslationKey()).formatted(s.getFormatting());
                            MutableText multiplierText = Text.literal(String.format("%.1f", (CropConfigs.getSeasonCropMultiplier(cropIdentifier, s) * 100)) + "% speed");
                            tooltip.add(text.append(Text.literal(": ").append(multiplierText.formatted(Formatting.WHITE))));
                        }
                    }
                }else {
                    tooltip.add(Text.translatable("tooltip.seasons.show_more", sneakKey.getBoundKeyLocalizedText().copy().formatted(Formatting.BLUE)).formatted(Formatting.GRAY));
                }
            }
        }
    }

}
