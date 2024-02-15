package com.cogsofcarminite.client.renderers;

import com.cogsofcarminite.blocks.CarminiteMagicLogBlock;
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
            Direction dir = Direction.fromAxisAndDirection(axis, logBlock.getAxisDirection(blockState));

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


            float speed = magicLog.flywheelSpeed.getValue(partialTicks) * 3 / 10f;
            float angl = magicLog.flywheelAngle + speed * partialTicks;

            SuperByteBuffer wheel = CachedBufferer.partial(logBlock.getFlywheelModel(), blockState);
            boolean f = blockState.getValue(CarminiteMagicLogBlock.AXIS_POSITIVE);
            switch (axis) {
                case X -> wheel.centre()
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Z), AngleHelper.rad(f ? 270.0F : 90.0F))
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Y), -AngleHelper.rad(f ? -angl : angl))
                        .unCentre();
                case Y -> wheel.centre()
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.X), AngleHelper.rad(f ? 00.0F : 180.0F))
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Y), AngleHelper.rad(f ? angl : -angl))
                        .unCentre();
                case Z -> wheel.centre()
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.X), AngleHelper.rad(f ? 90.0F : 270.0F))
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Y), AngleHelper.rad(f ? angl : -angl))
                        .unCentre();
            }
            wheel.light(light).renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));

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
            Direction dir = Direction.fromAxisAndDirection(axis, logBlock.getAxisDirection(blockState));

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

            float speed = flywheelSpeed.getValue(mc.getPartialTick()) * 3 / 10f;
            float angl = context.blockEntityData.getFloat("flywheel_angle") + speed * mc.getPartialTick();

            ms.popPose();
            ms.popPose();

            SuperByteBuffer wheel = CachedBufferer.partial(logBlock.getFlywheelModel(), blockState).transform(matrices.getModel());
            boolean f = blockState.getValue(CarminiteMagicLogBlock.AXIS_POSITIVE);
            switch (axis) {
                case X -> wheel.centre()
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Z), AngleHelper.rad(f ? 270.0F : 90.0F))
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Y), -AngleHelper.rad(f ? -angl : angl))
                        .unCentre();
                case Y -> wheel.centre()
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.X), AngleHelper.rad(f ? 00.0F : 180.0F))
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Y), AngleHelper.rad(f ? angl : -angl))
                        .unCentre();
                case Z -> wheel.centre()
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.X), AngleHelper.rad(f ? 90.0F : 270.0F))
                        .rotate(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Y), AngleHelper.rad(f ? angl : -angl))
                        .unCentre();
            }
            wheel
                    .light(matrices.getWorld(), ContraptionRenderDispatcher.getContraptionWorldLight(context, renderWorld))
                    .renderInto(matrices.getViewProjection(), buffer.getBuffer(RenderType.cutoutMipped()));
        }
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
