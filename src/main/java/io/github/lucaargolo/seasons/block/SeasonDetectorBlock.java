package io.github.lucaargolo.seasons.block;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.Season;
import io.github.lucaargolo.seasons.block.entity.SeasonDetectorBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DaylightDetectorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SeasonDetectorBlock extends DaylightDetectorBlock {

    public static final EnumProperty<Season> SEASON = EnumProperty.of("season", Season.class);

    public SeasonDetectorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(POWER, 0).with(SEASON, Season.SPRING).with(INVERTED, false));
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new SeasonDetectorBlockEntity(FabricSeasons.SEASON_DETECTOR_ENTITY);
    }

    public static void updateState(BlockState state, World world, BlockPos pos) {
        Season configuredSeason = state.get(SEASON);
        Season currentSeason = FabricSeasons.getCurrentSeason(world);
        int i = state.get(POWER);
        if(state.get(INVERTED)) {
            if(configuredSeason == currentSeason && i != 0) {
                world.setBlockState(pos, state.with(POWER, 0), 3);
            }else if(configuredSeason != currentSeason && i != 15) {
                world.setBlockState(pos, state.with(POWER, 15), 3);
            }
        }else{
            if(configuredSeason == currentSeason && i != 15) {
                world.setBlockState(pos, state.with(POWER, 15), 3);
            }else if(configuredSeason != currentSeason && i != 0) {
                world.setBlockState(pos, state.with(POWER, 0), 3);
            }
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.canModifyBlocks()) {
            if (world.isClient) {
                return ActionResult.SUCCESS;
            } else {
                BlockState blockState = player.isSneaking() ? state.cycle(INVERTED) : state.cycle(SEASON);
                world.setBlockState(pos, blockState, 4);
                updateState(blockState, world, pos);
                return ActionResult.CONSUME;
            }
        } else {
            return super.onUse(state, world, pos, player, hand, hit);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWER);
        builder.add(INVERTED);
        builder.add(SEASON);
    }
}
