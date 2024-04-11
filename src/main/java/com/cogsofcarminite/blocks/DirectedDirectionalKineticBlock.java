package com.cogsofcarminite.blocks;

import com.simibubi.create.content.contraptions.ITransformableBlock;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class DirectedDirectionalKineticBlock extends HorizontalKineticBlock implements IWrenchable, ITransformableBlock {

    public static final EnumProperty<AttachFace> TARGET = EnumProperty.create("target", AttachFace.class);

    public DirectedDirectionalKineticBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState().setValue(TARGET, AttachFace.WALL));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(TARGET));
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        for (Direction direction : pContext.getNearestLookingDirections()) {
            BlockState blockstate;
            if (direction.getAxis() == Direction.Axis.Y) {
                blockstate = this.defaultBlockState()
                        .setValue(TARGET, direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR)
                        .setValue(HORIZONTAL_FACING, pContext.getHorizontalDirection());
            } else {
                blockstate = this.defaultBlockState()
                        .setValue(TARGET, AttachFace.WALL)
                        .setValue(HORIZONTAL_FACING, direction.getOpposite());
            }

            return blockstate;
        }

        return null;
    }

    public static Direction getTargetDirection(BlockState pState) {
        return switch (pState.getValue(TARGET)) {
            case CEILING -> Direction.UP;
            case FLOOR -> Direction.DOWN;
            default -> pState.getValue(HORIZONTAL_FACING);
        };
    }

    public static Direction.Axis getTargetAxis(BlockState pState) {
        return switch (pState.getValue(TARGET)) {
            case CEILING, FLOOR -> Direction.Axis.Y;
            default -> pState.getValue(HORIZONTAL_FACING).getAxis();
        };
    }

    @Override
    public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
        if (targetedFace.getAxis() == Direction.Axis.Y)
            return super.getRotatedBlockState(originalState, targetedFace);

        Direction targetDirection = getTargetDirection(originalState);
        Direction newFacing = targetDirection.getClockWise(targetedFace.getAxis());
        if (targetedFace.getAxisDirection() == Direction.AxisDirection.NEGATIVE)
            newFacing = newFacing.getOpposite();

        if (newFacing.getAxis() == Direction.Axis.Y)
            return originalState.setValue(TARGET, newFacing == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR);
        return originalState.setValue(TARGET, AttachFace.WALL)
                .setValue(HORIZONTAL_FACING, newFacing);
    }

    @Override
    public BlockState transform(BlockState state, StructureTransform transform) {
        if (transform.mirror != null)
            state = mirror(state, transform.mirror);
        if (transform.rotationAxis == Direction.Axis.Y)
            return rotate(state, transform.rotation);

        Direction targetDirection = getTargetDirection(state);
        Direction newFacing = transform.rotateFacing(targetDirection);

        if (newFacing.getAxis() == Direction.Axis.Y)
            return state.setValue(TARGET, newFacing == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR);
        return state.setValue(TARGET, AttachFace.WALL)
                .setValue(HORIZONTAL_FACING, newFacing);
    }

}
