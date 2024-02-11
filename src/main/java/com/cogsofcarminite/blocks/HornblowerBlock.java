package com.cogsofcarminite.blocks;

import com.cogsofcarminite.blocks.entities.HornblowerBlockEntity;
import com.cogsofcarminite.reg.CCBlockEntities;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import twilightforest.init.TFItems;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class HornblowerBlock extends KineticBlock implements IRotate, IBE<HornblowerBlockEntity> {
    protected static final VoxelShape PLATE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 4.0D, 15.0D);

    protected static final VoxelShape HEAD_NORTH = Block.box(4.5D, 4.0D, 7.5D, 11.5, 14.0D, 14.5D);
    protected static final VoxelShape HEAD_SOUTH = Block.box(4.5D, 4.0D, 1.5D, 11.5, 14.0D, 8.5D);
    protected static final VoxelShape HEAD_WEST = Block.box(7.5D, 4.0D, 4.5D, 14.5D, 14.0D, 11.5);
    protected static final VoxelShape HEAD_EAST = Block.box(1.5D, 4.0D, 4.5D, 8.5D, 14.0D, 11.5);

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public HornblowerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof HornblowerBlockEntity hornblowerBlock) {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.is(TFItems.CRUMBLE_HORN.get()) || stack.is(Items.GOAT_HORN) || stack.isEmpty()) {
                ItemStack horn = hornblowerBlock.setHorn(stack);
                if (horn == null) return InteractionResult.FAIL;
                player.setItemInHand(hand, horn);
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(state, level, pos, player, hand, result);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> Shapes.or(PLATE, HEAD_NORTH);
            case WEST -> Shapes.or(PLATE, HEAD_WEST);
            case EAST -> Shapes.or(PLATE, HEAD_EAST);
            default -> Shapes.or(PLATE, HEAD_SOUTH);
        };
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.DOWN;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public Class<HornblowerBlockEntity> getBlockEntityClass() {
        return HornblowerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends HornblowerBlockEntity> getBlockEntityType() {
        return CCBlockEntities.HORNBLOWER.get();
    }
}
