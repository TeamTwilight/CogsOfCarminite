package com.cogsofcarminite.client.instances;

import com.cogsofcarminite.blocks.MechanicalRootPullerBlock;
import com.cogsofcarminite.blocks.entities.MechanicalRootPullerBlockEntity;
import com.cogsofcarminite.reg.CCPartialBlockModels;
import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.SingleRotatingInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MechanicalRootPullerInstance extends SingleRotatingInstance<MechanicalRootPullerBlockEntity> {
    protected final RotatingData coggies;

    public MechanicalRootPullerInstance(MaterialManager materialManager, MechanicalRootPullerBlockEntity blockEntity) {
        super(materialManager, blockEntity);
        Direction facing = this.blockState.getBlock() instanceof MechanicalRootPullerBlock ? this.blockState.getValue(MechanicalRootPullerBlock.FACING) : Direction.NORTH;
        RotatingData data = this.getRotatingMaterial().getModel(CCPartialBlockModels.ROOT_PULLER_GEARS, this.blockState, facing).createInstance();
        BlockPos pos = this.getInstancePosition();
        data.setRotationAxis(this.axis)
                .setRotationalSpeed(this.getBlockEntitySpeed())
                .setRotationOffset(getRotationOffset(this.axis))
                .setColor(blockEntity)
                .setPosition((float) pos.getX() + ((float)facing.getStepX() * 0.125F), pos.getY(), (float) pos.getZ() + ((float)facing.getStepZ() * 0.125F));
        this.coggies = data;
    }

    @Override
    public void update() {
        super.update();
        float speed = Math.abs(this.getBlockEntitySpeed());
        switch (this.blockState.getValue(MechanicalRootPullerBlock.FACING)) {
            case SOUTH, WEST -> speed = -speed;
        }
        this.updateRotation(this.coggies, this.getRotationAxis(), speed);
    }

    @Override
    public void updateLight() {
        super.updateLight();
        this.relight(this.pos, this.coggies);
    }

    @Override
    public void remove() {
        super.remove();
        this.coggies.delete();
    }

    @Override
    protected Instancer<RotatingData> getModel() {
        Direction facing = this.blockState.getValue(MechanicalRootPullerBlock.FACING);
        return getRotatingMaterial().getModel(AllPartialModels.COGWHEEL_SHAFT, this.blockState, facing, () -> {
            PoseStack stack = new PoseStack();
            TransformStack.cast(stack)
                    .centre()
                    .multiply(Axis.XP.rotationDegrees(90))
                    .unCentre();

            if (facing.getAxis().equals(Direction.Axis.Z)) {
                TransformStack.cast(stack)
                        .centre()
                        .multiply(Axis.ZP.rotationDegrees(90))
                        .rotateToFace(facing)
                        .unCentre();
            } else {
                TransformStack.cast(stack)
                        .centre()
                        .rotateToFace(facing)
                        .unCentre();
            }
            return stack;
        });
    }
}
