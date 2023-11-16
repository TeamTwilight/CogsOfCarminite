package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.blocks.entities.CarminiteClockBlockEntity;
import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogwheelBlock;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class CarminiteClockRenderer extends KineticBlockEntityRenderer<CarminiteClockBlockEntity> {

    public CarminiteClockRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(CarminiteClockBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        if (Backend.canUseInstancing(be.getLevel())) return;

        BlockState blockState = be.getBlockState();
        Block block = blockState.getBlock();
        if (!(block instanceof IRotate def)) return;

        Direction.Axis axis = getRotationAxisOf(be);
        BlockPos pos = be.getBlockPos();
        float angle = getAngleForTe(be, pos, axis);

        for (Direction d : Iterate.directionsInAxis(getRotationAxisOf(be))) {
            if (!def.hasShaftTowards(be.getLevel(), be.getBlockPos(), blockState, d)) {
                ExampleMod.LOGGER.warn("Skipped side {}", d);
                continue;
            }
            SuperByteBuffer shaft = CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, be.getBlockState(), d);
            kineticRotationTransform(shaft, be, axis, angle, light);
            shaft.renderInto(ms, buffer.getBuffer(RenderType.solid()));
        }
    }

    @Override
    protected SuperByteBuffer getRotatedModel(CarminiteClockBlockEntity be, BlockState state) {
        return CachedBufferer.partialFacingVertical(
                AllPartialModels.SHAFTLESS_COGWHEEL, state,
                Direction.fromAxisAndDirection(state.getValue(EncasedCogwheelBlock.AXIS), Direction.AxisDirection.POSITIVE));
    }

}
