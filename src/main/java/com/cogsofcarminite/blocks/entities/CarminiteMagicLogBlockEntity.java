package com.cogsofcarminite.blocks.entities;

import com.cogsofcarminite.blocks.CarminiteMagicLogBlock;
import com.cogsofcarminite.blocks.DirectedDirectionalKineticBlock;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class CarminiteMagicLogBlockEntity extends KineticBlockEntity {
    public static final float TICK_INTERVAL = 1000.0F;
    protected float nextTick = TICK_INTERVAL;

    public LerpedFloat flywheelSpeed = LerpedFloat.linear().chase(this.getGeneratedSpeed(), 1.0F / 64.0F, LerpedFloat.Chaser.EXP);
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

        if (this.level instanceof ServerLevel serverLevel && this.getBlockState().getBlock() instanceof CarminiteMagicLogBlock log && log.doesCoreFunction()) {
            this.nextTick -= Math.abs(this.flywheelSpeed.getValue());
            while (this.nextTick <= 0.0F) {
                log.performTreeEffect(serverLevel, this.getBlockPos(), serverLevel.random, log.getFilter(this));
                log.playSound(serverLevel, this.getBlockPos(), serverLevel.random);
                this.nextTick += TICK_INTERVAL;
                CarminiteMagicLogBlock.spawnParticles(serverLevel, this.getBlockPos());
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
        compound.putFloat("flywheel_angle", this.flywheelAngle);
        compound.put("flywheel_speed", this.flywheelSpeed.writeNBT());
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        this.nextTick = compound.getFloat("core_next_tick");
        this.flywheelAngle = compound.getFloat("flywheel_angle");
        this.flywheelSpeed.readNBT(compound.getCompound("flywheel_speed"), clientPacket);
        this.flywheelSpeed.chase(0, 1.0F / 64.0F, LerpedFloat.Chaser.EXP);
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
                if (direction != Direction.UP) xyz = VecHelper.rotateCentered(xyz, 180.0F, Direction.Axis.Z);
                direction = direction == Direction.DOWN ? state.getValue(DirectedDirectionalKineticBlock.HORIZONTAL_FACING).getOpposite() : state.getValue(DirectedDirectionalKineticBlock.HORIZONTAL_FACING);
                xyz = VecHelper.rotateCentered(xyz, AngleHelper.horizontalAngle(direction.getOpposite()), Direction.Axis.Y);
            }
            return xyz;
        }

        @Override
        public void rotate(BlockState state, PoseStack ms) {
            Direction direction = getSide(state);
            if (direction.getAxis() == Direction.Axis.Y) {
                direction = direction == Direction.DOWN ? state.getValue(DirectedDirectionalKineticBlock.HORIZONTAL_FACING).getOpposite() : state.getValue(DirectedDirectionalKineticBlock.HORIZONTAL_FACING);
                float yRot = direction.toYRot() + (direction.getAxis() != Direction.Axis.Z ? 180.0F : 0.0F);
                TransformStack.cast(ms).rotateY(yRot);
            } else {
                float yRot = direction.toYRot() + (direction.getAxis() == Direction.Axis.Z ? 180.0F : 0.0F);
                TransformStack.cast(ms).rotateY(yRot).rotateX(90.0F);
            }
        }

        public Direction getSide(BlockState state) {
            return state.getBlock() instanceof CarminiteMagicLogBlock log ? log.getDirection(state) : Direction.UP;
        }
    }
}
