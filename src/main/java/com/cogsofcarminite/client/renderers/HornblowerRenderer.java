package com.cogsofcarminite.client.renderers;

import com.cogsofcarminite.blocks.HornblowerBlock;
import com.cogsofcarminite.blocks.entities.HornblowerBlockEntity;
import com.cogsofcarminite.reg.CCPartialBlockModels;
import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

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

        PartialModel mouth = CCPartialBlockModels.HORNBLOWER;

        float offset = hornblower.animation.getValue(partialTicks);
        if (hornblower.animation.getChaseTarget() > 0.0F && hornblower.animation.getValue() > 0.5F) {
            float wiggleProgress = (AnimationTickHolder.getTicks(hornblower.getLevel()) + partialTicks) / 8.0F;
            offset -= Math.sin(wiggleProgress * (2.0F * Mth.PI) * 2.0F) / 16.0F;
        }

        CachedBufferer.partial(mouth, blockState)
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

}
