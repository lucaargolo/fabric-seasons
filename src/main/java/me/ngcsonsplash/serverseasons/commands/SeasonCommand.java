package me.ngcsonsplash.serverseasons.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.ngcsonsplash.serverseasons.FabricSeasons;
import me.ngcsonsplash.serverseasons.utils.Season;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TimeCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class SeasonCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("season")
            .then(CommandManager.literal("set").requires((source) -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("spring")
                    .executes(
                        context -> TimeCommand.executeSet(context.getSource(),
                        switch(FabricSeasons.CONFIG.getStartingSeason()) {
                            case SPRING -> 0;
                            case WINTER -> FabricSeasons.CONFIG.getWinterLength();
                            case FALL -> FabricSeasons.CONFIG.getFallLength() + FabricSeasons.CONFIG.getWinterLength();
                            case SUMMER -> FabricSeasons.CONFIG.getSummerLength() + FabricSeasons.CONFIG.getFallLength() + FabricSeasons.CONFIG.getWinterLength();
                        }
                    ))
                )
                .then(CommandManager.literal("summer")
                    .executes(context -> TimeCommand.executeSet(
                        context.getSource(),
                        switch(FabricSeasons.CONFIG.getStartingSeason()) {
                            case SUMMER -> 0;
                            case SPRING -> FabricSeasons.CONFIG.getSpringLength();
                            case WINTER -> FabricSeasons.CONFIG.getWinterLength() + FabricSeasons.CONFIG.getSpringLength();
                            case FALL -> FabricSeasons.CONFIG.getFallLength() + FabricSeasons.CONFIG.getWinterLength() + FabricSeasons.CONFIG.getSpringLength();
                        }
                    ))
                )
                .then(CommandManager.literal("fall")
                    .executes(context -> TimeCommand.executeSet(
                        context.getSource(),
                        switch(FabricSeasons.CONFIG.getStartingSeason()) {
                            case FALL -> 0;
                            case SUMMER -> FabricSeasons.CONFIG.getSummerLength();
                            case SPRING -> FabricSeasons.CONFIG.getSpringLength() + FabricSeasons.CONFIG.getSummerLength();
                            case WINTER -> FabricSeasons.CONFIG.getWinterLength() + FabricSeasons.CONFIG.getSpringLength() + FabricSeasons.CONFIG.getSummerLength();
                        }
                    ))
                )
                .then(CommandManager.literal("winter")
                    .executes(context -> TimeCommand.executeSet(
                        context.getSource(),
                        switch(FabricSeasons.CONFIG.getStartingSeason()) {
                            case WINTER -> 0;
                            case FALL -> FabricSeasons.CONFIG.getFallLength();
                            case SUMMER -> FabricSeasons.CONFIG.getSummerLength() + FabricSeasons.CONFIG.getFallLength();
                            case SPRING -> FabricSeasons.CONFIG.getSpringLength() + FabricSeasons.CONFIG.getSummerLength() + FabricSeasons.CONFIG.getFallLength();
                        }
                    ))
                )
            )
            .then(CommandManager.literal("query")
                .executes(context -> {
                    World world = context.getSource().getWorld();
                    Season currentSeason = FabricSeasons.getCurrentSeason(world);
                    Season nextSeason = FabricSeasons.getNextSeason(world, currentSeason);
                    long ticksLeft = FabricSeasons.getTimeToNextSeason(world);
                    context.getSource().sendFeedback(() -> Text.translatable("commands.seasons.query_1",
                            Text.translatable(currentSeason.getTranslationKey()).formatted(currentSeason.getFormatting())
                    ), false);
                    context.getSource().sendFeedback(() -> Text.translatable("commands.seasons.query_2",
                            Long.toString(ticksLeft/24000L),
                            Long.toString(ticksLeft),
                            Text.translatable(nextSeason.getTranslationKey()).formatted(nextSeason.getFormatting())
                    ), false);
                    return currentSeason.ordinal();
                })
            )
            .then(CommandManager.literal("skip").requires((source) -> source.hasPermissionLevel(2))
                .executes(context -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(context.getSource().getWorld())))
                .then(CommandManager.literal("spring")
                    .executes(context -> {
                        World world = context.getSource().getWorld();
                        Season season = FabricSeasons.getCurrentSeason(world);
                        return switch (season) {
                            case SPRING -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world) + Season.SUMMER.getSeasonLength() + Season.FALL.getSeasonLength() + Season.WINTER.getSeasonLength());
                            case SUMMER -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world) + Season.FALL.getSeasonLength() + Season.WINTER.getSeasonLength());
                            case FALL -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world) + Season.WINTER.getSeasonLength());
                            case WINTER -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world));
                        };
                    })
                )
                .then(CommandManager.literal("summer")
                    .executes(context -> {
                        World world = context.getSource().getWorld();
                        Season season = FabricSeasons.getCurrentSeason(world);
                        return switch (season) {
                            case SPRING -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world));
                            case SUMMER -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world) + Season.FALL.getSeasonLength() + Season.WINTER.getSeasonLength() + Season.SPRING.getSeasonLength());
                            case FALL -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world) + Season.WINTER.getSeasonLength() + Season.SPRING.getSeasonLength());
                            case WINTER -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world) + Season.SPRING.getSeasonLength());
                        };
                    })
                )
                .then(CommandManager.literal("fall")
                    .executes(context -> {
                        World world = context.getSource().getWorld();
                        Season season = FabricSeasons.getCurrentSeason(world);
                        return switch (season) {
                            case SPRING -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world) + Season.SUMMER.getSeasonLength());
                            case SUMMER -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world));
                            case FALL -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world) + Season.WINTER.getSeasonLength() + Season.SPRING.getSeasonLength() + Season.SUMMER.getSeasonLength());
                            case WINTER -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world) + Season.SPRING.getSeasonLength() + Season.SUMMER.getSeasonLength());
                        };
                    })
                )
                .then(CommandManager.literal("winter")
                    .executes(context -> {
                        World world = context.getSource().getWorld();
                        Season season = FabricSeasons.getCurrentSeason(world);
                        return switch (season) {
                            case SPRING -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world) + Season.SUMMER.getSeasonLength() + Season.FALL.getSeasonLength());
                            case SUMMER -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world) + Season.FALL.getSeasonLength());
                            case FALL -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world));
                            case WINTER -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world) + Season.SPRING.getSeasonLength() + Season.SUMMER.getSeasonLength() + Season.FALL.getSeasonLength());
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
        source.sendFeedback(() -> Text.translatable("commands.time.set", i), true);
        return i;
    }

}
