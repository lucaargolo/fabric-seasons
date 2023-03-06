package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.resources.CropConfigs;
import io.github.lucaargolo.seasons.utils.Season;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
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
                Identifier cropIdentifier = Registry.BLOCK.getId(block);
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
                boolean sneak = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), MinecraftClient.getInstance().options.sneakKey.boundKey.getCode());
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
                    tooltip.add(Text.literal(("§7"+Text.translatable("tooltip.seasons.show_more").getString()).replace("{KEY}", "§9"+Text.translatable(MinecraftClient.getInstance().options.sneakKey.getBoundKeyTranslationKey()).getString()+"§7")));
                }
            }
        }
    }

}
