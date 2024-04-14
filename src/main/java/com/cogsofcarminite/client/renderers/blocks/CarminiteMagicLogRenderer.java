package com.cogsofcarminite.client.renderers.blocks;

import com.cogsofcarminite.blocks.CarminiteMagicLogBlock;
import com.cogsofcarminite.blocks.DirectedDirectionalKineticBlock;
import com.cogsofcarminite.blocks.entities.CarminiteMagicLogBlockEntity;
import com.jozufozu.flywheel.backend.Backend;
import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.contraptions.render.ContraptionRenderDispatcher;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogwheelBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
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
    protected void renderSafe(CarminiteMagicLogBlockEntity magicLog, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState blockState = magicLog.getBlockState();
        if (blockState.getBlock() instanceof CarminiteMagicLogBlock logBlock && magicLog.getLevel() != null) {
            BlockPos pos = magicLog.getBlockPos();
            Direction.Axis axis = getRotationAxisOf(magicLog);
            Direction dir = logBlock.getDirection(blockState);

            ms.pushPose();
            ms.translate(0.5D, 0.5D, 0.5D);

            ms.translate((float) dir.getStepX() * 0.5D, (float) dir.getStepY() * 0.5D, (float) dir.getStepZ() * 0.5D);

            switch (axis) {
                case X -> ms.mulPose(Axis.YP.rotationDegrees(dir.toYRot() + 180.0F));
                case Y -> ms.mulPose(Axis.XP.rotationDegrees(dir == Direction.DOWN ? 90.0F : 270.0F));
                case Z -> ms.mulPose(Axis.YP.rotationDegrees(dir.toYRot()));
            }

            PoseStack.Pose posestack$pose = ms.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            VertexConsumer vertexconsumer = buffer.getBuffer(logBlock.getRenderType());

            vertex(vertexconsumer, matrix4f, matrix3f, 0.0F, 0, 0, 1);
            vertex(vertexconsumer, matrix4f, matrix3f, 1.0F, 0, 1, 1);
            vertex(vertexconsumer, matrix4f, matrix3f, 1.0F, 1, 1, 0);
            vertex(vertexconsumer, matrix4f, matrix3f, 0.0F, 1, 0, 0);
            ms.popPose();
            ms.pushPose();


            float ogSpeed = magicLog.flywheelSpeed.getValue(partialTicks);
            float speed = ogSpeed * 3.0F / 10.0F;
            float angl = magicLog.flywheelAngle + speed * partialTicks;

            switch (DirectedDirectionalKineticBlock.getTargetDirection(blockState)) {
                case DOWN -> TransformStack.cast(ms).centre()
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.X), AngleHelper.rad(180.0F))
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Y), AngleHelper.rad(-angl))
                        .unCentre();
                case UP -> TransformStack.cast(ms).centre()
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.X), AngleHelper.rad(00.0F))
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Y), AngleHelper.rad(angl))
                        .unCentre();
                case NORTH -> TransformStack.cast(ms).centre()
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.X), AngleHelper.rad(270.0F))
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Y), AngleHelper.rad(-angl))
                        .unCentre();
                case SOUTH -> TransformStack.cast(ms).centre()
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.X), AngleHelper.rad(90.0F))
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Y), AngleHelper.rad(angl))
                        .unCentre();
                case WEST -> TransformStack.cast(ms).centre()
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Z), AngleHelper.rad(90.0F))
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Y), -AngleHelper.rad(angl))
                        .unCentre();
                case EAST -> TransformStack.cast(ms).centre()
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Z), AngleHelper.rad(270.0F))
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Y), -AngleHelper.rad(-angl))
                        .unCentre();
            }

            CachedBufferer.partial(logBlock.getFlywheelModel(), blockState).light(light).renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));

            int alpha = Mth.clamp(Math.abs((int) ogSpeed) * 2, 1, 255);

            CachedBufferer.partial(logBlock.getFlywheelOverlay(), blockState)
                    .light(light).color(255, 255, 255, alpha).renderInto(ms, buffer.getBuffer(RenderType.translucentMovingBlock()));

            ms.popPose();

            super.renderSafe(magicLog, partialTicks, ms, buffer, light, overlay);
            FilteringRenderer.renderOnBlockEntity(magicLog, partialTicks, ms, buffer, light, overlay);
            if (Backend.canUseInstancing(magicLog.getLevel())) return;

            float angle = getAngleForTe(magicLog, pos, axis);

            for (Direction d : Iterate.directionsInAxis(getRotationAxisOf(magicLog))) {
                if (!logBlock.hasShaftTowards(magicLog.getLevel(), magicLog.getBlockPos(), blockState, d)) {
                    continue;
                }
                SuperByteBuffer shaft = CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, magicLog.getBlockState(), d);
                kineticRotationTransform(shaft, magicLog, axis, angle, light);
                shaft.renderInto(ms, buffer.getBuffer(RenderType.solid()));
            }
        }
    }

    public static void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        BlockState blockState = context.state;
        if (blockState.getBlock() instanceof CarminiteMagicLogBlock logBlock) {
            Direction.Axis axis = logBlock.getRotationAxis(blockState);
            Direction dir = logBlock.getDirection(blockState);

            PoseStack ms = matrices.getModelViewProjection();
            ms.pushPose();
            TransformStack.cast(ms).translate(context.localPos);
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
            VertexConsumer vertexconsumer = buffer.getBuffer(logBlock.getRenderType());

            vertex(vertexconsumer, matrix4f, matrix3f, 0.0F, 0, 0, 1);
            vertex(vertexconsumer, matrix4f, matrix3f, 1.0F, 0, 1, 1);
            vertex(vertexconsumer, matrix4f, matrix3f, 1.0F, 1, 1, 0);
            vertex(vertexconsumer, matrix4f, matrix3f, 0.0F, 1, 0, 0);

            Minecraft mc = Minecraft.getInstance();
            Level level = mc.level != null ? mc.level : renderWorld;

            LerpedFloat flywheelSpeed = LerpedFloat.linear();
            flywheelSpeed.readNBT(context.blockEntityData.getCompound("flywheel_speed"), level.isClientSide);
            flywheelSpeed.chase(0, 1.0F / 64.0F, LerpedFloat.Chaser.EXP);

            float ogSpeed = flywheelSpeed.getValue(mc.getPartialTick());
            float speed = ogSpeed * 3.0F / 10.0F;
            float angl = context.blockEntityData.getFloat("flywheel_angle") + speed * mc.getPartialTick();

            ms.popPose();
            ms.popPose();

            PoseStack msPaint = matrices.getViewProjection();

            switch (DirectedDirectionalKineticBlock.getTargetDirection(blockState)) {
                case DOWN -> TransformStack.cast(ms).centre()
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.X), AngleHelper.rad(180.0F))
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Y), AngleHelper.rad(-angl))
                        .unCentre();
                case UP -> TransformStack.cast(ms).centre()
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.X), AngleHelper.rad(00.0F))
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Y), AngleHelper.rad(angl))
                        .unCentre();
                case NORTH -> TransformStack.cast(ms).centre()
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.X), AngleHelper.rad(270.0F))
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Y), AngleHelper.rad(-angl))
                        .unCentre();
                case SOUTH -> TransformStack.cast(ms).centre()
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.X), AngleHelper.rad(90.0F))
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Y), AngleHelper.rad(angl))
                        .unCentre();
                case WEST -> TransformStack.cast(ms).centre()
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Z), AngleHelper.rad(90.0F))
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Y), -AngleHelper.rad(angl))
                        .unCentre();
                case EAST -> TransformStack.cast(ms).centre()
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Z), AngleHelper.rad(270.0F))
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Y), -AngleHelper.rad(-angl))
                        .unCentre();
            }

            CachedBufferer.partial(logBlock.getFlywheelModel(), blockState).transform(matrices.getModel())
                    .light(matrices.getWorld(), ContraptionRenderDispatcher.getContraptionWorldLight(context, renderWorld))
                    .renderInto(msPaint, buffer.getBuffer(RenderType.cutoutMipped()));

            int alpha = Mth.clamp(Math.abs((int) ogSpeed) * 2, 1, 255);

            CachedBufferer.partial(logBlock.getFlywheelOverlay(), blockState).transform(matrices.getModel())
                    .light(matrices.getWorld(), ContraptionRenderDispatcher.getContraptionWorldLight(context, renderWorld))
                    .color(255, 255, 255, alpha).renderInto(msPaint, buffer.getBuffer(RenderType.translucentMovingBlock()));
        }
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix4f, Matrix3f matrix3f, float x, int y, int u, int v) {
        if (true) return;
        consumer.vertex(matrix4f, x - 0.5F, (float)y - 0.5F, 0.0F).color(255, 255, 255, 255).uv((float)u, (float)v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
    }

    @Override
    protected SuperByteBuffer getRotatedModel(CarminiteMagicLogBlockEntity be, BlockState state) {
        return CachedBufferer.partialFacingVertical(
                AllPartialModels.SHAFTLESS_LARGE_COGWHEEL, state,
                Direction.fromAxisAndDirection(state.getValue(EncasedCogwheelBlock.AXIS), Direction.AxisDirection.POSITIVE));
    }

}
