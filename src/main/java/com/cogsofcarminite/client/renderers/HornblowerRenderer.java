package com.cogsofcarminite.client.renderers;

import com.cogsofcarminite.blocks.HornblowerBlock;
import com.cogsofcarminite.blocks.entities.HornblowerBlockEntity;
import com.cogsofcarminite.reg.CCPartialBlockModels;
import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.contraptions.render.ContraptionRenderDispatcher;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.BlockEntityRenderHelper;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class HornblowerRenderer extends SafeBlockEntityRenderer<HornblowerBlockEntity> {

    public HornblowerRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderSafe(HornblowerBlockEntity hornblower, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState blockState = hornblower.getBlockState();
        if (!(blockState.getBlock() instanceof HornblowerBlock)) return;

        Direction direction = blockState.getValue(HornblowerBlock.FACING);

        float offset = hornblower.animation.getValue(partialTicks);
        if (hornblower.animation.getChaseTarget() > 0.0F && hornblower.animation.getValue() > 0.5F) {
            float wiggleProgress = (AnimationTickHolder.getTicks(hornblower.getLevel()) + partialTicks) / 8.0F;
            offset -= (float) (Math.sin(wiggleProgress * (2.0F * Mth.PI) * 2.0F) / 16.0F);
        }

        CachedBufferer.partial(CCPartialBlockModels.HORNBLOWER, blockState)
                .centre()
                .rotateY(AngleHelper.horizontalAngle(direction.getOpposite()))
                .unCentre()
                .translate(0, offset * 2.F / 16.0F, 0)
                .light(light)
                .renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));

        if (!hornblower.horn.isEmpty()) {
            ms.pushPose();
            Direction facing = hornblower.getBlockState().getValue(HORIZONTAL_FACING);
            Vec3 vec = Vec3.atLowerCornerOf(facing.getNormal()).scale(0.25D).add(0.5D, 0.625D, 0.5D);

            ms.translate(vec.x, vec.y, vec.z);
            ms.scale(1 / 2.0F, 1 / 2.0F, 1 / 2.0F);
            float yRot = AngleHelper.horizontalAngle(facing);
            ms.mulPose(Axis.YP.rotationDegrees(yRot + 90.0F));

            ms.translate(0, 0, -1 / 256.0F);
            ms.mulPose(Axis.YP.rotationDegrees(180));
            Minecraft.getInstance()
                    .getItemRenderer()
                    .renderStatic(hornblower.horn, ItemDisplayContext.FIXED, light, overlay, ms, buffer, hornblower.getLevel(), 0);

            ms.popPose();
        }
    }

    public static void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer, LerpedFloat animation) {
        BlockState state = context.state;
        Direction facing = state.getValue(HornblowerBlock.FACING).getOpposite();

        SuperByteBuffer superBuffer = CachedBufferer.partial(CCPartialBlockModels.HORNBLOWER, state);
        float partialTicks = Minecraft.getInstance().getPartialTick();

        float offset = animation.getValue(partialTicks);
        if (animation.getChaseTarget() > 0.0F && animation.getValue() > 0.5F) {
            float wiggleProgress = (AnimationTickHolder.getTicks(renderWorld) + partialTicks) / 8.0F;
            offset -= (float) (Math.sin(wiggleProgress * (2.0F * Mth.PI) * 2.0F) / 16.0F);
        }

        ItemStack horn = context.blockEntityData.contains("HornStack") ? ItemStack.of(context.blockEntityData.getCompound("HornStack")) : ItemStack.EMPTY;
        if (!horn.isEmpty()) {
            PoseStack ms = matrices.getModelViewProjection();
            ms.pushPose();
            TransformStack.cast(ms).translate(context.localPos);
            Vec3 vec = Vec3.atLowerCornerOf(facing.getOpposite().getNormal()).scale(0.25D).add(0.5D, 0.625D, 0.5D);

            ms.translate(vec.x, vec.y, vec.z);
            ms.scale(1 / 2.0F, 1 / 2.0F, 1 / 2.0F);
            float yRot = AngleHelper.horizontalAngle(facing.getOpposite());
            ms.mulPose(Axis.YP.rotationDegrees(yRot + 90.0F));

            ms.translate(0, 0, -1 / 256.0F);
            ms.mulPose(Axis.YP.rotationDegrees(180));

            Minecraft mc = Minecraft.getInstance();
            Level level = mc.level != null ? mc.level : renderWorld;
            int light = BlockEntityRenderHelper.getCombinedLight(level, getLightPos(matrices.getLight(), context.localPos), renderWorld, context.localPos);
            Minecraft.getInstance()
                    .getItemRenderer()
                    .renderStatic(horn, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, ms, buffer, renderWorld, 0);

            ms.popPose();
        }

        superBuffer.transform(matrices.getModel())
                .centre()
                .rotateY(AngleHelper.horizontalAngle(facing))
                .rotateX(AngleHelper.verticalAngle(facing))
                .unCentre()
                .translate(0, offset * 2.F / 16.0F, 0)
                .light(matrices.getWorld(), ContraptionRenderDispatcher.getContraptionWorldLight(context, renderWorld))
                .renderInto(matrices.getViewProjection(), buffer.getBuffer(RenderType.cutoutMipped()));
    }

    private static BlockPos getLightPos(@Nullable Matrix4f lightTransform, BlockPos contraptionPos) {
        if (lightTransform != null) {
            Vector4f lightVec = new Vector4f(contraptionPos.getX() + .5f, contraptionPos.getY() + .5f, contraptionPos.getZ() + .5f, 1);
            lightVec.mul(lightTransform);
            return BlockPos.containing(lightVec.x(), lightVec.y(), lightVec.z());
        } else {
            return contraptionPos;
        }
    }

}
