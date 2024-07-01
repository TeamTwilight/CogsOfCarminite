package com.cogsofcarminite.blocks;

import com.cogsofcarminite.blocks.entities.CarminiteCoreBlockEntity;
import com.cogsofcarminite.blocks.entities.CarminiteMagicLogBlockEntity;
import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.content.kinetics.base.IRotate;
import net.minecraft.MethodsReturnNonnullByDefault;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import twilightforest.network.ParticlePacket;
import twilightforest.network.TFPacketHandler;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class CarminiteMagicLogBlock extends DirectedDirectionalKineticBlock implements IRotate {

    public CarminiteMagicLogBlock(Properties properties) {
        super(properties.lightLevel((state) -> 15));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState();
        Direction preferredFacing = context.getPlayer() != null && context.getPlayer()
                .isShiftKeyDown() ? context.getNearestLookingDirection() : context.getNearestLookingDirection().getOpposite();

        if (preferredFacing.getAxis() == Direction.Axis.Y) {
            state = state.setValue(TARGET, preferredFacing == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR);
            preferredFacing = preferredFacing == Direction.UP ? context.getHorizontalDirection() : context.getHorizontalDirection().getOpposite();
        }

        return state.setValue(HORIZONTAL_FACING, preferredFacing);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return DirectedDirectionalKineticBlock.getTargetAxis(state);
    }

    public Direction getDirection(BlockState state) {
        return DirectedDirectionalKineticBlock.getTargetDirection(state);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (level.getBlockEntity(pos) instanceof CarminiteCoreBlockEntity entity) entity.removeSource();
        super.onPlace(state, level, pos, oldState, isMoving);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == this.getDirection(state).getOpposite();
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

    public abstract PartialModel getFlywheelOverlay();
}
