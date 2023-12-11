package com.cogsofcarminite.blocks.entities;

import com.cogsofcarminite.blocks.CarminiteMagicLogBlock;
import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import twilightforest.network.ParticlePacket;
import twilightforest.network.TFPacketHandler;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class CarminiteMagicLogBlockEntity extends KineticBlockEntity {
    public static final float TICK_INTERVAL = 1000.0F;
    protected float nextTick = TICK_INTERVAL;

    public LerpedFloat flywheelSpeed = LerpedFloat.linear().chase(this.getGeneratedSpeed(), 1.0F / 64.0F, LerpedFloat.Chaser.EXP);;
    public float flywheelAngle;

    public CarminiteMagicLogBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void tick() {
        super.tick();

        this.flywheelSpeed.updateChaseTarget(this.getSpeed());
        this.flywheelSpeed.tickChaser();
        this.flywheelAngle += this.flywheelSpeed.getValue() * 3 / 10f;
        this.flywheelAngle %= 360;

        if (this.level != null && level instanceof ServerLevel serverLevel && this.doesCoreFunction()) {
            this.nextTick -= Math.abs(this.flywheelSpeed.getValue());
            while (this.nextTick <= 0.0F) {
                this.performTreeEffect(serverLevel, this.getBlockPos(), this.level.random);
                this.playSound(this.level, this.getBlockPos(), this.level.random);
                this.nextTick += TICK_INTERVAL;
                spawnParticles(serverLevel, this.getBlockPos());
            }
        }
    }

    protected static void spawnParticles(ServerLevel level, BlockPos pos) {
        Vec3 xyz = Vec3.atCenterOf(pos);

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
                        particlePacket.queueParticle(DustParticleOptions.REDSTONE, false, (double)pos.getX() + x, (double)pos.getY() + y, (double)pos.getZ() + z, 0.0D, 0.0D, 0.0D);
                    }
                }
                TFPacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverplayer), particlePacket);
            }
        }
    }


    @Override
    protected AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().inflate(2);
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putFloat("core_next_tick", this.nextTick);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        this.nextTick = compound.getFloat("core_next_tick");
    }

    public abstract PartialModel getFlywheelModel();

    public abstract RenderType getRenderType();

    public abstract boolean doesCoreFunction();

    protected abstract void performTreeEffect(ServerLevel level, BlockPos pos, RandomSource rand);

    protected void playSound(Level level, BlockPos pos, RandomSource rand) {

    }

    public static class BlockFilteringBehaviour extends FilteringBehaviour {
        public BlockFilteringBehaviour(SmartBlockEntity be, ValueBoxTransform slot) {
            super(be, slot);
        }

        @Override
        public boolean setFilter(ItemStack stack) {
            if (stack.isEmpty() || stack.getItem() instanceof BlockItem || stack.getItem() instanceof FilterItem) return super.setFilter(stack);
            return false;
        }
    }

    public static class MagicLogSlot extends ValueBoxTransform {

        @Override
        public Vec3 getLocalOffset(BlockState state) {
            Direction direction = getSide(state);

            Vec3 xyz = VecHelper.voxelSpace(8, 13, 15.5f);
            if (!direction.getAxis().equals(Direction.Axis.Y)) {
                xyz = VecHelper.rotateCentered(xyz, AngleHelper.horizontalAngle(Direction.UP), Direction.Axis.Y);
                xyz = VecHelper.rotateCentered(xyz, AngleHelper.verticalAngle(Direction.UP), Direction.Axis.X);
                xyz = VecHelper.rotateCentered(xyz, AngleHelper.horizontalAngle(direction.getOpposite()), Direction.Axis.Y);
            } else {
                if (direction == Direction.UP) {
                    xyz = VecHelper.rotateCentered(xyz, AngleHelper.horizontalAngle(Direction.NORTH), Direction.Axis.Y);
                } else {
                    xyz = VecHelper.rotateCentered(xyz, 180.0F, Direction.Axis.Z);
                }
            }
            return xyz;
        }

        @Override
        public void rotate(BlockState state, PoseStack ms) {
            Direction direction = getSide(state);
            float yRot = direction == Direction.DOWN ? 180.0F : direction != Direction.UP ? direction.toYRot() + (direction.getAxis() == Direction.Axis.Z ? 180.0F : 0.0F) : 0.0F;
            float xRot = direction.getAxis() != Direction.Axis.Y ? 90.0F : 0.0F;
            TransformStack.cast(ms).rotateY(yRot).rotateX(xRot);
        }

        public Direction getSide(BlockState state) {
            return state.getBlock() instanceof CarminiteMagicLogBlock log ? log.getDirection(state) : Direction.UP;
        }
    }
}
