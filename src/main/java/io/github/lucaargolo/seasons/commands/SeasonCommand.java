package io.github.lucaargolo.seasons.commands;

import com.mojang.brigadier.CommandDispatcher;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.utils.Season;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TimeCommand;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;

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
                    context.getSource().sendFeedback(new TranslatableText("tooltip.seasons.calendar_info_1").append(new TranslatableText("tooltip.seasons."+season.name().toLowerCase())), false);
                    context.getSource().sendFeedback(new LiteralText(Long.toString(((FabricSeasons.CONFIG.getSeasonLength() - (world.getTimeOfDay() - ((world.getTimeOfDay()/FabricSeasons.CONFIG.getSeasonLength())*FabricSeasons.CONFIG.getSeasonLength()) )) % FabricSeasons.CONFIG.getSeasonLength())/24000L)).append(new TranslatableText("tooltip.seasons.calendar_info_2").append(new TranslatableText("tooltip.seasons."+season.getNext().name().toLowerCase()))), false);
                    return season.ordinal();
                })
            )
            .then(CommandManager.literal("skip")
                .executes(context -> TimeCommand.executeAdd(context.getSource(), FabricSeasons.CONFIG.getSeasonLength()))
                .then(CommandManager.literal("spring")
                    .executes(context -> {
                        World world = context.getSource().getWorld();
                        Season season = FabricSeasons.getCurrentSeason(world);
                        int timeToNextSeason = Math.toIntExact((FabricSeasons.CONFIG.getSeasonLength() - (world.getTimeOfDay() - ((world.getTimeOfDay()/FabricSeasons.CONFIG.getSeasonLength())*FabricSeasons.CONFIG.getSeasonLength()) )) % FabricSeasons.CONFIG.getSeasonLength());
                        switch (season) {
                            case SPRING:
                                return TimeCommand.executeAdd(context.getSource(), timeToNextSeason+(FabricSeasons.CONFIG.getSeasonLength()*3));
                            case SUMMER:
                                return TimeCommand.executeAdd(context.getSource(), timeToNextSeason+(FabricSeasons.CONFIG.getSeasonLength()*2));
                            case FALL:
                                return TimeCommand.executeAdd(context.getSource(), timeToNextSeason+FabricSeasons.CONFIG.getSeasonLength());
                            case WINTER:
                                return TimeCommand.executeAdd(context.getSource(), timeToNextSeason);
                        }
                        return 0;
                    })
                )
                .then(CommandManager.literal("summer")
                    .executes(context -> {
                        World world = context.getSource().getWorld();
                        Season season = FabricSeasons.getCurrentSeason(world);
                        int timeToNextSeason = Math.toIntExact((FabricSeasons.CONFIG.getSeasonLength() - (world.getTimeOfDay() - ((world.getTimeOfDay()/FabricSeasons.CONFIG.getSeasonLength())*FabricSeasons.CONFIG.getSeasonLength()) )) % FabricSeasons.CONFIG.getSeasonLength());
                        switch (season) {
                            case SPRING:
                                return TimeCommand.executeAdd(context.getSource(), timeToNextSeason);
                            case SUMMER:
                                return TimeCommand.executeAdd(context.getSource(), timeToNextSeason+(FabricSeasons.CONFIG.getSeasonLength()*3));
                            case FALL:
                                return TimeCommand.executeAdd(context.getSource(), timeToNextSeason+(FabricSeasons.CONFIG.getSeasonLength()*2));
                            case WINTER:
                                return TimeCommand.executeAdd(context.getSource(), timeToNextSeason+FabricSeasons.CONFIG.getSeasonLength());
                        }
                        return 0;
                    })
                )
                .then(CommandManager.literal("fall")
                    .executes(context -> {
                        World world = context.getSource().getWorld();
                        Season season = FabricSeasons.getCurrentSeason(world);
                        int timeToNextSeason = Math.toIntExact((FabricSeasons.CONFIG.getSeasonLength() - (world.getTimeOfDay() - ((world.getTimeOfDay()/FabricSeasons.CONFIG.getSeasonLength())*FabricSeasons.CONFIG.getSeasonLength()) )) % FabricSeasons.CONFIG.getSeasonLength());
                        switch (season) {
                            case SPRING:
                                return TimeCommand.executeAdd(context.getSource(), timeToNextSeason+FabricSeasons.CONFIG.getSeasonLength());
                            case SUMMER:
                                return TimeCommand.executeAdd(context.getSource(), timeToNextSeason);
                            case FALL:
                                return TimeCommand.executeAdd(context.getSource(), timeToNextSeason+(FabricSeasons.CONFIG.getSeasonLength()*3));
                            case WINTER:
                                return TimeCommand.executeAdd(context.getSource(), timeToNextSeason+(FabricSeasons.CONFIG.getSeasonLength()*2));
                        }
                        return 0;
                    })
                )
                .then(CommandManager.literal("winter")
                    .executes(context -> {
                        World world = context.getSource().getWorld();
                        Season season = FabricSeasons.getCurrentSeason(world);
                        int timeToNextSeason = Math.toIntExact((FabricSeasons.CONFIG.getSeasonLength() - (world.getTimeOfDay() - ((world.getTimeOfDay()/FabricSeasons.CONFIG.getSeasonLength())*FabricSeasons.CONFIG.getSeasonLength()) )) % FabricSeasons.CONFIG.getSeasonLength());
                        switch (season) {
                            case SPRING:
                                return TimeCommand.executeAdd(context.getSource(), timeToNextSeason+(FabricSeasons.CONFIG.getSeasonLength()*2));
                            case SUMMER:
                                return TimeCommand.executeAdd(context.getSource(), timeToNextSeason+FabricSeasons.CONFIG.getSeasonLength());
                            case FALL:
                                return TimeCommand.executeAdd(context.getSource(), timeToNextSeason);
                            case WINTER:
                                return TimeCommand.executeAdd(context.getSource(), timeToNextSeason+(FabricSeasons.CONFIG.getSeasonLength()*3));
                        }
                        return 0;
                    })
                )
            )
        );
    }

}
