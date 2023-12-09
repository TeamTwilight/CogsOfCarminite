package com.cogsofcarminite.client.renderers;

import com.cogsofcarminite.blocks.CarminiteMagicLogBlock;
import com.cogsofcarminite.blocks.entities.CarminiteMagicLogBlockEntity;
import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.flywheel.FlywheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogwheelBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CarminiteMagicLogRenderer extends KineticBlockEntityRenderer<CarminiteMagicLogBlockEntity> {

    public CarminiteMagicLogRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(CarminiteMagicLogBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState blockState = be.getBlockState();
        Block block = blockState.getBlock();

        if (block instanceof CarminiteMagicLogBlock logBlock && be.getLevel() != null) {
            BlockPos pos = be.getBlockPos();
            Direction.Axis axis = getRotationAxisOf(be);
            Direction dir = Direction.fromAxisAndDirection(axis, logBlock.getAxisDirection(blockState));

            ms.pushPose();
            ms.translate(0.5D, 0.5D, 0.5D);
            ms.pushPose();

            ms.translate((float) dir.getStepX() * 0.5D, (float) dir.getStepY() * 0.5D, (float) dir.getStepZ() * 0.5D);

            switch (axis) {
                case X -> ms.mulPose(Axis.YP.rotationDegrees(dir.toYRot() + 180.0F));
                case Y -> ms.mulPose(Axis.XP.rotationDegrees(dir == Direction.DOWN ? 90.0F : 270.0F));
                case Z -> ms.mulPose(Axis.YP.rotationDegrees(dir.toYRot()));
            }

            PoseStack.Pose posestack$pose = ms.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            VertexConsumer vertexconsumer = buffer.getBuffer(be.getRenderType());

            vertex(vertexconsumer, matrix4f, matrix3f, 0.0F, 0, 0, 1);
            vertex(vertexconsumer, matrix4f, matrix3f, 1.0F, 0, 1, 1);
            vertex(vertexconsumer, matrix4f, matrix3f, 1.0F, 1, 1, 0);
            vertex(vertexconsumer, matrix4f, matrix3f, 0.0F, 1, 0, 0);

            ms.popPose();
            ms.popPose();

            super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
            FilteringRenderer.renderOnBlockEntity(be, partialTicks, ms, buffer, light, overlay);
            if (Backend.canUseInstancing(be.getLevel())) return;


            float angle = getAngleForTe(be, pos, axis);

            for (Direction d : Iterate.directionsInAxis(getRotationAxisOf(be))) {
                if (!logBlock.hasShaftTowards(be.getLevel(), be.getBlockPos(), blockState, d)) {
                    continue;
                }
                SuperByteBuffer shaft = CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, be.getBlockState(), d);
                kineticRotationTransform(shaft, be, axis, angle, light);
                shaft.renderInto(ms, buffer.getBuffer(RenderType.solid()));
            }

            float speed = be.flywheelSpeed.getValue(partialTicks) * 3 / 10f;
            float angl = be.flywheelAngle + speed * partialTicks;

            VertexConsumer vb = buffer.getBuffer(RenderType.solid());

            BlockState state = AllBlocks.FLYWHEEL.getDefaultState().setValue(FlywheelBlock.AXIS, blockState.getValue(CarminiteMagicLogBlock.AXIS));
            renderFlywheel(be, ms, light, state, angl, vb);
        }
    }

    private void renderFlywheel(CarminiteMagicLogBlockEntity be, PoseStack ms, int light, BlockState blockState, float angle, VertexConsumer vb) {
        SuperByteBuffer wheel = CachedBufferer.block(blockState);
        kineticRotationTransform(wheel, be, getRotationAxisOf(be), AngleHelper.rad(angle), light);
        wheel.renderInto(ms, vb);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix4f, Matrix3f matrix3f, float x, int y, int u, int v) {
        consumer.vertex(matrix4f, x - 0.5F, (float)y - 0.5F, 0.0F).color(255, 255, 255, 255).uv((float)u, (float)v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
    }

    @Override
    protected SuperByteBuffer getRotatedModel(CarminiteMagicLogBlockEntity be, BlockState state) {
        return CachedBufferer.partialFacingVertical(
                AllPartialModels.SHAFTLESS_LARGE_COGWHEEL, state,
                Direction.fromAxisAndDirection(state.getValue(EncasedCogwheelBlock.AXIS), Direction.AxisDirection.POSITIVE));
    }

}
