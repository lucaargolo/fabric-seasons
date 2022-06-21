package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.utils.Season;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
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
                float multiplier = FabricSeasons.CONFIG.getSeasonCropMultiplier(cropIdentifier, season);
                boolean sneak = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), MinecraftClient.getInstance().options.sneakKey.boundKey.getCode());
                if (sneak) {
                    tooltip.add(Text.translatable("tooltip.seasons.crop_multipliers"));
                    MutableText spring = Text.translatable("tooltip.seasons.spring");
                    MutableText springMultiplier = Text.literal(String.format("%.1f", (FabricSeasons.CONFIG.getSeasonCropMultiplier(cropIdentifier, Season.SPRING) * 100)) + "% speed");
                    tooltip.add(spring.append(Text.literal(": ").setStyle(spring.getStyle()).append(springMultiplier)));
                    MutableText summer = Text.translatable("tooltip.seasons.summer");
                    MutableText summerMultiplier = Text.literal(String.format("%.1f", (FabricSeasons.CONFIG.getSeasonCropMultiplier(cropIdentifier, Season.SUMMER) * 100)) + "% speed");
                    tooltip.add(summer.append(Text.literal(": ").setStyle(summer.getStyle()).append(summerMultiplier)));
                    MutableText fall = Text.translatable("tooltip.seasons.fall");
                    MutableText fallMultiplier = Text.literal(String.format("%.1f", (FabricSeasons.CONFIG.getSeasonCropMultiplier(cropIdentifier, Season.FALL) * 100)) + "% speed");
                    tooltip.add(fall.append(Text.literal(": ").setStyle(fall.getStyle()).append(fallMultiplier)));
                    MutableText winter = Text.translatable("tooltip.seasons.winter");
                    MutableText winterMultiplier = Text.literal(String.format("%.1f", (FabricSeasons.CONFIG.getSeasonCropMultiplier(cropIdentifier, Season.WINTER) * 100)) + "% speed");
                    tooltip.add(winter.append(Text.literal(": ").setStyle(winter.getStyle()).append(winterMultiplier)));
                } else {
                    if (multiplier == 0f) {
                        tooltip.add(Text.translatable("tooltip.seasons.not_grow"));
                    } else if (multiplier < 1.0f) {
                        tooltip.add(Text.translatable("tooltip.seasons.slowed_grow"));
                    } else if (multiplier == 1.0f) {
                        tooltip.add(Text.translatable("tooltip.seasons.normal_grow"));
                    } else {
                        tooltip.add(Text.translatable("tooltip.seasons.faster_grow"));
                    }
                    tooltip.add(Text.literal(Text.translatable("tooltip.seasons.show_more").getString().replace("{KEY}", Text.translatable(MinecraftClient.getInstance().options.sneakKey.getBoundKeyTranslationKey()).getString())));
                }
            }
        }
    }

}
