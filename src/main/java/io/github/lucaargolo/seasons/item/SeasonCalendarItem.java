package io.github.lucaargolo.seasons.item;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.utils.Season;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class SeasonCalendarItem extends Item {

    public SeasonCalendarItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        if(world != null) {
            Season season = FabricSeasons.getCurrentSeason(world);
            int seasonLength = FabricSeasons.CONFIG.getSeasonLength();
            tooltip.add(Text.translatable("tooltip.seasons.calendar_info_1").append(Text.translatable("tooltip.seasons."+season.name().toLowerCase(Locale.ROOT))));
            if(!FabricSeasons.CONFIG.isSeasonLocked() && !FabricSeasons.CONFIG.isSeasonTiedWithSystemTime())
                tooltip.add(Text.literal(Long.toString(((seasonLength - (world.getTimeOfDay() - ((world.getTimeOfDay()/seasonLength)*seasonLength) )) % seasonLength)/24000L)).append(Text.translatable("tooltip.seasons.calendar_info_2").append(Text.translatable("tooltip.seasons."+season.getNext().name().toLowerCase(Locale.ROOT)))));
        }

    }
}
