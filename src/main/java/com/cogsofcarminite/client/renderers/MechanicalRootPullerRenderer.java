package com.cogsofcarminite.client.renderers;

import com.cogsofcarminite.blocks.MechanicalRootPullerBlock;
import com.cogsofcarminite.blocks.entities.MechanicalRootPullerBlockEntity;
import com.cogsofcarminite.reg.CCPartialBlockModels;
import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.contraptions.render.ContraptionRenderDispatcher;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MechanicalRootPullerRenderer extends KineticBlockEntityRenderer<MechanicalRootPullerBlockEntity> {

    public MechanicalRootPullerRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(MechanicalRootPullerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState blockState = be.getBlockState();
        if (blockState.getBlock() instanceof MechanicalRootPullerBlock) {
            Direction facing = blockState.getValue(MechanicalRootPullerBlock.FACING);

            BlockPos pos = be.getBlockPos();
            Direction dir = blockState.getValue(MechanicalRootPullerBlock.FACING);

            float time = AnimationTickHolder.getRenderTime(be.getLevel());
            float offset = getRotationOffsetForPosition(be, pos, dir.getClockWise().getAxis());
            float angle = ((time * Math.abs(be.getSpeed()) * 3f / 10 + offset) % 360) / 180 * (float) Math.PI;

            ms.pushPose();
            ms.translate(dir.getStepX() * 2.0D / 16.0D, 0.0D, dir.getStepZ() * 2.0D / 16.0D);

            CachedBufferer.partial(CCPartialBlockModels.ROOT_PULLER_GEARS, blockState)
                    .rotateCentered(Direction.UP, AngleHelper.rad(AngleHelper.horizontalAngle(facing)))
                    .centre()
                    .rotate(Direction.WEST, angle)
                    .unCentre()
                    .light(light)
                    .renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));;

            ms.popPose();

            super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
            FilteringRenderer.renderOnBlockEntity(be, partialTicks, ms, buffer, light, overlay);
        }
    }

    public static void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        BlockState blockState = context.state;
        if (blockState.getBlock() instanceof MechanicalRootPullerBlock) {
            Direction facing = blockState.getValue(MechanicalRootPullerBlock.FACING);
            SuperByteBuffer superBuffer = CachedBufferer.partial(CCPartialBlockModels.ROOT_PULLER_GEARS, blockState);

            float speed = 0.0F;
            if (context.contraption.stalled || !VecHelper.isVecPointingTowards(context.relativeMotion, facing.getOpposite())) speed = context.getAnimationSpeed();

            superBuffer.transform(matrices.getModel());

            PoseStack ms = new PoseStack();
            ms.translate(facing.getStepX() * 2.0D / 16.0D, 0.0D, facing.getStepZ() * 2.0D / 16.0D);
            superBuffer.transform(ms);

            transform(context.world, facing, superBuffer, speed);

            superBuffer.light(matrices.getWorld(), ContraptionRenderDispatcher.getContraptionWorldLight(context, renderWorld))
                    .renderInto(matrices.getViewProjection(), buffer.getBuffer(RenderType.cutoutMipped()));
        }
    }

    public static void transform(Level world, Direction facing, SuperByteBuffer superBuffer, float speed) {
        float time = AnimationTickHolder.getRenderTime(world) / 20;
        float angle = (time * speed) % 360;

        superBuffer.rotateCentered(Direction.UP, AngleHelper.rad(AngleHelper.horizontalAngle(facing)))
                .centre()
                .rotate(Direction.WEST, AngleHelper.rad(angle))
                .unCentre();
    }

    @Override
    protected SuperByteBuffer getRotatedModel(MechanicalRootPullerBlockEntity be, BlockState state) {
        return CachedBufferer.partialFacingVertical(AllPartialModels.COGWHEEL_SHAFT, state, state.getValue(MechanicalRootPullerBlock.FACING).getClockWise());
    }
}
