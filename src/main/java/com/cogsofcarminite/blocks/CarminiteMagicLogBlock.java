package com.cogsofcarminite.blocks;

import com.cogsofcarminite.CCUtil;
import com.cogsofcarminite.blocks.entities.CarminiteCoreBlockEntity;
import com.cogsofcarminite.blocks.entities.CarminiteMagicLogBlockEntity;
import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import twilightforest.network.ParticlePacket;
import twilightforest.network.TFPacketHandler;

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
        Direction facing = context.getNearestLookingDirection().getOpposite();
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) facing = facing.getOpposite();
        return pointedTo(this.defaultBlockState(), facing);
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

    public static void spawnParticles(ServerLevel level, BlockPos pos) {
        spawnParticles(level, pos, Vec3.atCenterOf(pos));
    }

    public static void spawnParticles(ServerLevel level, BlockPos pos, Vec3 xyz) {
        for (ServerPlayer serverplayer : level.players()) {
            if (serverplayer.distanceToSqr(xyz) < 4096.0D) {
                ParticlePacket particlePacket = new ParticlePacket();

                for(Direction direction : Direction.values()) {
                    BlockPos blockpos = pos.relative(direction);
                    if (!level.getBlockState(blockpos).isSolidRender(level, blockpos)) {
                        Direction.Axis axis = direction.getAxis();
                        double x = axis == Direction.Axis.X ? 0.5D + 0.5625D * (double)direction.getStepX() : (double)level.random.nextFloat();
                        double y = axis == Direction.Axis.Y ? 0.5D + 0.5625D * (double)direction.getStepY() : (double)level.random.nextFloat();
                        double z = axis == Direction.Axis.Z ? 0.5D + 0.5625D * (double)direction.getStepZ() : (double)level.random.nextFloat();
                        particlePacket.queueParticle(DustParticleOptions.REDSTONE, false, xyz.x - 0.5D + x, xyz.y - 0.5D + y, xyz.z - 0.5D + z, 0.0D, 0.0D, 0.0D);
                    }
                }
                TFPacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverplayer), particlePacket);
            }
        }
    }

    public abstract boolean doesCoreFunction();

    public abstract void performTreeEffect(ServerLevel level, BlockPos pos, RandomSource rand, CompoundTag filter);

    public void playSound(Level level, BlockPos pos, RandomSource rand) {

    }

    public CompoundTag getFilter(CarminiteMagicLogBlockEntity blockEntity) {
        return new CompoundTag();
    }

    public abstract PartialModel getFlywheelModel();

    public abstract RenderType getRenderType();
}
