package com.cogsofcarminite.blocks;

import com.cogsofcarminite.CCUtil;
import com.cogsofcarminite.blocks.entities.CarminiteCoreBlockEntity;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import twilightforest.TwilightForestMod;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class CarminiteMagicLogBlock extends KineticBlock implements IRotate {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    public static final BooleanProperty AXIS_POSITIVE = BooleanProperty.create("axis_positive");

    public CarminiteMagicLogBlock(Properties properties) {
        super(properties.lightLevel((state) -> 15));
        this.registerDefaultState(this.defaultBlockState().setValue(AXIS, Direction.Axis.Y).setValue(AXIS_POSITIVE, true));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return pointedTo(this.defaultBlockState(), context.getClickedFace());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, AXIS_POSITIVE);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    }

    public Direction.AxisDirection getAxisDirection(BlockState state) {
        return state.getValue(AXIS_POSITIVE) ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (level.getBlockEntity(pos) instanceof CarminiteCoreBlockEntity entity) {
            entity.removeSource();
            TwilightForestMod.LOGGER.error("REMOVED SOURCE");
        }
        super.onPlace(state, level, pos, oldState, isMoving);
    }

    public Direction getDirection(BlockState state) {
        return Direction.fromAxisAndDirection(this.getRotationAxis(state), this.getAxisDirection(state));
    }

    public static BlockState pointedTo(BlockState state, Direction direction) {
        if (state.getBlock() instanceof CarminiteMagicLogBlock) {
            return state.setValue(AXIS, direction.getAxis()).setValue(AXIS_POSITIVE, CCUtil.isPositive(direction));
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot) {
        return pointedTo(state, rot.rotate(this.getDirection(state)));
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return state.getValue(AXIS).equals(face.getAxis()) && state.getValue(AXIS_POSITIVE) != CCUtil.isPositive(face);
    }
}
