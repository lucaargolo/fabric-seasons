package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.utils.Season;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
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
        if(FabricSeasons.MOD_CONFIG.isSeasonMessingCrops()) {
            Season season = FabricSeasons.getCurrentSeason();
            Item item = stack.getItem();
            Block block = FabricSeasons.SEEDS_MAP.getOrDefault(item, null);
            if (block != null) {
                Identifier cropIdentifier = Registry.BLOCK.getId(block);
                float multiplier = FabricSeasons.MOD_CONFIG.getSeasonCropMultiplier(cropIdentifier, season);
                boolean sneak = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), MinecraftClient.getInstance().options.keySneak.boundKey.getCode());
                if (sneak) {
                    tooltip.add(new TranslatableText("tooltip.seasons.crop_multipliers"));
                    TranslatableText spring = new TranslatableText("tooltip.seasons.spring");
                    LiteralText springMultiplier = new LiteralText(String.format("%.1f", (FabricSeasons.MOD_CONFIG.getSeasonCropMultiplier(cropIdentifier, Season.SPRING) * 100)) + "% speed");
                    tooltip.add(spring.append(new LiteralText(": ").setStyle(spring.getStyle()).append(springMultiplier)));
                    TranslatableText summer = new TranslatableText("tooltip.seasons.summer");
                    LiteralText summerMultiplier = new LiteralText(String.format("%.1f", (FabricSeasons.MOD_CONFIG.getSeasonCropMultiplier(cropIdentifier, Season.SUMMER) * 100)) + "% speed");
                    tooltip.add(summer.append(new LiteralText(": ").setStyle(summer.getStyle()).append(summerMultiplier)));
                    TranslatableText fall = new TranslatableText("tooltip.seasons.fall");
                    LiteralText fallMultiplier = new LiteralText(String.format("%.1f", (FabricSeasons.MOD_CONFIG.getSeasonCropMultiplier(cropIdentifier, Season.FALL) * 100)) + "% speed");
                    tooltip.add(fall.append(new LiteralText(": ").setStyle(fall.getStyle()).append(fallMultiplier)));
                    TranslatableText winter = new TranslatableText("tooltip.seasons.winter");
                    LiteralText winterMultiplier = new LiteralText(String.format("%.1f", (FabricSeasons.MOD_CONFIG.getSeasonCropMultiplier(cropIdentifier, Season.WINTER) * 100)) + "% speed");
                    tooltip.add(winter.append(new LiteralText(": ").setStyle(winter.getStyle()).append(winterMultiplier)));
                } else {
                    if (multiplier == 0f) {
                        tooltip.add(new TranslatableText("tooltip.seasons.not_grow"));
                    } else if (multiplier < 1.0f) {
                        tooltip.add(new TranslatableText("tooltip.seasons.slowed_grow"));
                    } else if (multiplier == 1.0f) {
                        tooltip.add(new TranslatableText("tooltip.seasons.normal_grow"));
                    } else {
                        tooltip.add(new TranslatableText("tooltip.seasons.faster_grow"));
                    }
                    tooltip.add(new LiteralText(new TranslatableText("tooltip.seasons.show_more").getString().replace("{KEY}", new TranslatableText(MinecraftClient.getInstance().options.keySneak.getBoundKeyTranslationKey()).getString())));
                }
            }
        }
    }

}
