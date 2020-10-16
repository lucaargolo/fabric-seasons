package io.github.lucaargolo.seasons.item;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.Season;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SeasonCalendarItem extends Item {

    public SeasonCalendarItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        if(world != null) {
            Season season = FabricSeasons.getCurrentSeason(world);
            Season nextSeason;
            switch (season) {
                case SUMMER:
                    nextSeason = Season.FALL;
                    break;
                case FALL:
                    nextSeason = Season.WINTER;
                    break;
                case WINTER:
                    nextSeason = Season.SPRING;
                    break;
                default:
                    nextSeason = Season.SUMMER;
            }
            tooltip.add(new TranslatableText("tooltip.seasons.calendar_info_1").append(new TranslatableText("tooltip.seasons."+season.name().toLowerCase())));
            tooltip.add(new LiteralText(Long.toString(((FabricSeasons.SEASON_LENGTH - (world.getTimeOfDay() - ((world.getTimeOfDay()/FabricSeasons.SEASON_LENGTH)*FabricSeasons.SEASON_LENGTH) )) % FabricSeasons.SEASON_LENGTH)/24000L)).append(new TranslatableText("tooltip.seasons.calendar_info_2").append(new TranslatableText("tooltip.seasons."+nextSeason.name().toLowerCase()))));
        }

    }
}
