package io.github.lucaargolo.seasons.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.utils.Season;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TimeCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.Locale;

public class SeasonCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("season").requires((source) -> source.hasPermissionLevel(2))
            .then(CommandManager.literal("set")
                .then(CommandManager.literal("spring")
                    .executes(context -> TimeCommand.executeSet(context.getSource(), 0))
                )
                .then(CommandManager.literal("summer")
                    .executes(context -> TimeCommand.executeSet(context.getSource(), FabricSeasons.CONFIG.getSeasonLength()))
                )
                .then(CommandManager.literal("fall")
                    .executes(context -> TimeCommand.executeSet(context.getSource(), FabricSeasons.CONFIG.getSeasonLength()*2))
                )
                .then(CommandManager.literal("winter")
                    .executes(context -> TimeCommand.executeSet(context.getSource(), FabricSeasons.CONFIG.getSeasonLength()*3))
                )
            )
            .then(CommandManager.literal("query")
                .executes(context -> {
                    World world = context.getSource().getWorld();
                    Season season = FabricSeasons.getCurrentSeason(world);
                    context.getSource().sendFeedback(Text.translatable("tooltip.seasons.calendar_info_1").append(Text.translatable("tooltip.seasons."+season.name().toLowerCase(Locale.ROOT))), false);
                    context.getSource().sendFeedback(Text.literal(Long.toString(((FabricSeasons.CONFIG.getSeasonLength() - (world.getTimeOfDay() - ((world.getTimeOfDay()/FabricSeasons.CONFIG.getSeasonLength())*FabricSeasons.CONFIG.getSeasonLength()) )) % FabricSeasons.CONFIG.getSeasonLength())/24000L)).append(Text.translatable("tooltip.seasons.calendar_info_2").append(Text.translatable("tooltip.seasons."+season.getNext().name().toLowerCase(Locale.ROOT)))), false);
                    return season.ordinal();
                })
            )
            .then(CommandManager.literal("skip")
                .executes(context -> executeLongAdd(context.getSource(), FabricSeasons.CONFIG.getSeasonLength()))
                .then(CommandManager.literal("spring")
                    .executes(context -> {
                        World world = context.getSource().getWorld();
                        Season season = FabricSeasons.getCurrentSeason(world);
                        long timeToNextSeason = (FabricSeasons.CONFIG.getSeasonLength() - (world.getTimeOfDay() - ((world.getTimeOfDay()/FabricSeasons.CONFIG.getSeasonLength())*FabricSeasons.CONFIG.getSeasonLength()) )) % FabricSeasons.CONFIG.getSeasonLength();
                        return switch (season) {
                            case SPRING -> executeLongAdd(context.getSource(), timeToNextSeason + (FabricSeasons.CONFIG.getSeasonLength() * 3L));
                            case SUMMER -> executeLongAdd(context.getSource(), timeToNextSeason + (FabricSeasons.CONFIG.getSeasonLength() * 2L));
                            case FALL -> executeLongAdd(context.getSource(), timeToNextSeason + FabricSeasons.CONFIG.getSeasonLength());
                            case WINTER -> executeLongAdd(context.getSource(), timeToNextSeason);
                        };
                    })
                )
                .then(CommandManager.literal("summer")
                    .executes(context -> {
                        World world = context.getSource().getWorld();
                        Season season = FabricSeasons.getCurrentSeason(world);
                        long timeToNextSeason = (FabricSeasons.CONFIG.getSeasonLength() - (world.getTimeOfDay() - ((world.getTimeOfDay()/FabricSeasons.CONFIG.getSeasonLength())*FabricSeasons.CONFIG.getSeasonLength()) )) % FabricSeasons.CONFIG.getSeasonLength();
                        return switch (season) {
                            case SPRING -> executeLongAdd(context.getSource(), timeToNextSeason);
                            case SUMMER -> executeLongAdd(context.getSource(), timeToNextSeason + (FabricSeasons.CONFIG.getSeasonLength() * 3L));
                            case FALL -> executeLongAdd(context.getSource(), timeToNextSeason + (FabricSeasons.CONFIG.getSeasonLength() * 2L));
                            case WINTER -> executeLongAdd(context.getSource(), timeToNextSeason + FabricSeasons.CONFIG.getSeasonLength());
                        };
                    })
                )
                .then(CommandManager.literal("fall")
                    .executes(context -> {
                        World world = context.getSource().getWorld();
                        Season season = FabricSeasons.getCurrentSeason(world);
                        long timeToNextSeason = (FabricSeasons.CONFIG.getSeasonLength() - (world.getTimeOfDay() - ((world.getTimeOfDay()/FabricSeasons.CONFIG.getSeasonLength())*FabricSeasons.CONFIG.getSeasonLength()) )) % FabricSeasons.CONFIG.getSeasonLength();
                        return switch (season) {
                            case SPRING -> executeLongAdd(context.getSource(), timeToNextSeason + FabricSeasons.CONFIG.getSeasonLength());
                            case SUMMER -> executeLongAdd(context.getSource(), timeToNextSeason);
                            case FALL -> executeLongAdd(context.getSource(), timeToNextSeason + (FabricSeasons.CONFIG.getSeasonLength() * 3L));
                            case WINTER -> executeLongAdd(context.getSource(), timeToNextSeason + (FabricSeasons.CONFIG.getSeasonLength() * 2L));
                        };
                    })
                )
                .then(CommandManager.literal("winter")
                    .executes(context -> {
                        World world = context.getSource().getWorld();
                        Season season = FabricSeasons.getCurrentSeason(world);
                        long timeToNextSeason = (FabricSeasons.CONFIG.getSeasonLength() - (world.getTimeOfDay() - ((world.getTimeOfDay()/FabricSeasons.CONFIG.getSeasonLength())*FabricSeasons.CONFIG.getSeasonLength()) )) % FabricSeasons.CONFIG.getSeasonLength();
                        return switch (season) {
                            case SPRING -> executeLongAdd(context.getSource(), timeToNextSeason + (FabricSeasons.CONFIG.getSeasonLength() * 2L));
                            case SUMMER -> executeLongAdd(context.getSource(), timeToNextSeason + FabricSeasons.CONFIG.getSeasonLength());
                            case FALL -> executeLongAdd(context.getSource(), timeToNextSeason);
                            case WINTER -> executeLongAdd(context.getSource(), timeToNextSeason + (FabricSeasons.CONFIG.getSeasonLength() * 3L));
                        };
                    })
                )
            )
        );
    }

    public static int executeLongAdd(ServerCommandSource source, long time) {

        for (ServerWorld serverWorld : source.getServer().getWorlds()) {
            serverWorld.setTimeOfDay(serverWorld.getTimeOfDay() + time);
        }

        int i = (int) (source.getWorld().getTimeOfDay() % 24000L);
        source.sendFeedback(Text.translatable("commands.time.set", i), true);
        return i;
    }

}
