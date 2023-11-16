package com.example.examplemod.blocks;

import com.example.examplemod.blocks.entities.CarminiteClockBlockEntity;
import com.example.examplemod.reg.TCBlockEntities;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CarminiteClock extends KineticBlock implements IBE<CarminiteClockBlockEntity>, IWrenchable, ICogWheel {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public CarminiteClock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.UP));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public Class<CarminiteClockBlockEntity> getBlockEntityClass() {
        return CarminiteClockBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CarminiteClockBlockEntity> getBlockEntityType() {
        return TCBlockEntities.CARMINITE_CLOCK.get();
    }
}
