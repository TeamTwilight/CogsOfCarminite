package com.cogsofcarminite.client.instances;

import com.cogsofcarminite.blocks.CarminiteMagicLogBlock;
import com.cogsofcarminite.blocks.entities.CarminiteMagicLogBlockEntity;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CarminiteMagicLogInstance extends KineticBlockEntityInstance<CarminiteMagicLogBlockEntity> implements DynamicInstance {

    protected final RotatingData shaft;
    protected final ModelData wheel;
    protected float lastAngle = Float.NaN;

    public CarminiteMagicLogInstance(MaterialManager materialManager, CarminiteMagicLogBlockEntity blockEntity) {
        super(materialManager, blockEntity);

        Direction facing = this.blockState.getBlock() instanceof CarminiteMagicLogBlock logBlock ? logBlock.getDirection(this.blockState).getOpposite() : Direction.UP;
        this.shaft = setup(getRotatingMaterial().getModel(AllPartialModels.SHAFT_HALF, CarminiteMagicLogBlock.pointedTo(this.blockState, facing), facing).createInstance());

        this.wheel = getTransformMaterial().getModel(blockEntity.getFlywheelModel(), this.blockState).createInstance();
        this.animate(blockEntity.flywheelAngle);
    }

    @Override
    public void beginFrame() {
        float partialTicks = AnimationTickHolder.getPartialTicks();
        float speed = this.blockEntity.flywheelSpeed.getValue(partialTicks) * 3 / 10.0F;
        float angle = this.blockEntity.flywheelAngle + speed * partialTicks;

        if (Math.abs(angle - this.lastAngle) < 0.001D) return;

        this.animate(angle);
        this.lastAngle = angle;
    }

    private void animate(float angle) {
        PoseStack ms = new PoseStack();
        TransformStack msr = TransformStack.cast(ms);

        msr.translate(getInstancePosition());

        boolean f = this.blockState.getValue(CarminiteMagicLogBlock.AXIS_POSITIVE);

        switch (this.axis) {
            case X -> msr.centre()
                    .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Z), AngleHelper.rad(f ? 270.0F : 90.0F))
                    .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Y), -AngleHelper.rad(f ? -angle : angle))
                    .unCentre();
            case Y -> msr.centre()
                    .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.X), AngleHelper.rad(f ? 00.0F : 180.0F))
                    .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Y), AngleHelper.rad(f ? angle : -angle))
                    .unCentre();
            case Z -> msr.centre()
                    .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.X), AngleHelper.rad(f ? 90.0F : 270.0F))
                    .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Y), AngleHelper.rad(f ? angle : -angle))
                    .unCentre();
        }

        this.wheel.setTransform(ms);
    }

    @Override
    public void update() {
        this.updateRotation(this.shaft);
    }

    @Override
    public void updateLight() {
        this.relight(this.pos, this.shaft, this.wheel);
    }

    @Override
    public void remove() {
        this.shaft.delete();
        this.wheel.delete();
    }
}
