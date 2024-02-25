package com.cogsofcarminite.client.instances;

import com.cogsofcarminite.blocks.MechanicalRootPullerBlock;
import com.cogsofcarminite.blocks.entities.MechanicalRootPullerBlockEntity;
import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.SingleRotatingInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MechanicalRootPullerInstance extends SingleRotatingInstance<MechanicalRootPullerBlockEntity> {

    public MechanicalRootPullerInstance(MaterialManager materialManager, MechanicalRootPullerBlockEntity blockEntity) {
        super(materialManager, blockEntity);
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
