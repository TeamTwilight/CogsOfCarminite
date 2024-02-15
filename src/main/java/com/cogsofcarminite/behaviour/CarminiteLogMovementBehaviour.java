package com.cogsofcarminite.behaviour;

import com.cogsofcarminite.blocks.CarminiteMagicLogBlock;
import com.cogsofcarminite.blocks.entities.CarminiteMagicLogBlockEntity;
import com.cogsofcarminite.client.renderers.CarminiteMagicLogRenderer;
import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import twilightforest.TwilightForestMod;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CarminiteLogMovementBehaviour implements MovementBehaviour {
    @Override
    public void tick(MovementContext context) {
        CompoundTag blockData = context.blockEntityData;

        Level level = context.world;
        float nextTick = blockData.getFloat("core_next_tick");
        float flywheelAngle = blockData.getFloat("flywheel_angle");
        LerpedFloat flywheelSpeed = LerpedFloat.linear();
        flywheelSpeed.readNBT(blockData.getCompound("flywheel_speed"), level.isClientSide);
        flywheelSpeed.chase(0, 1.0F / 64.0F, LerpedFloat.Chaser.EXP);

        flywheelSpeed.updateChaseTarget(context.getAnimationSpeed() * 0.1F);
        flywheelSpeed.tickChaser();
        flywheelAngle += flywheelSpeed.getValue() * 3 / 10f;
        flywheelAngle %= 360;

        if (level instanceof ServerLevel serverLevel && context.state.getBlock() instanceof CarminiteMagicLogBlock log && log.doesCoreFunction()) {
            nextTick -= Math.abs(flywheelSpeed.getValue());
            while (nextTick <= 0.0F) {
                BlockPos pos = new BlockPos(Math.round((float)context.position.x) - 1, Math.round((float)context.position.y) - 1, Math.round((float)context.position.z) - 1);
                log.performTreeEffect(serverLevel, pos, serverLevel.random, blockData.getCompound("Filter"));
                log.playSound(serverLevel, pos, serverLevel.random);
                nextTick += CarminiteMagicLogBlockEntity.TICK_INTERVAL;
                CarminiteMagicLogBlock.spawnParticles(serverLevel, pos, context.position);
            }
        }

        blockData.putFloat("core_next_tick", nextTick);
        blockData.putFloat("flywheel_angle", flywheelAngle);
        blockData.put("flywheel_speed", flywheelSpeed.writeNBT());

        float angl = flywheelAngle + (flywheelSpeed.getValue(Minecraft.getInstance().getPartialTick()) * 3 / 10f) * Minecraft.getInstance().getPartialTick();
        TwilightForestMod.LOGGER.error("ANGL AT CODE WAS {}!", angl);
    }

    @Override
    @OnlyIn(value = Dist.CLIENT)
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        CarminiteMagicLogRenderer.renderInContraption(context, renderWorld, matrices, buffer);
    }
}
