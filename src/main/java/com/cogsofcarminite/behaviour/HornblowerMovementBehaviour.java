package com.cogsofcarminite.behaviour;

import com.cogsofcarminite.blocks.HornblowerBlock;
import com.cogsofcarminite.client.renderers.HornblowerRenderer;
import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import twilightforest.init.TFItems;
import twilightforest.init.TFRecipes;
import twilightforest.init.TFSounds;
import twilightforest.util.WorldUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.cogsofcarminite.blocks.entities.HornblowerBlockEntity.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class HornblowerMovementBehaviour implements MovementBehaviour {
    @Override
    public void tick(MovementContext context) {
        CompoundTag blockData = context.blockEntityData;

        Level level = context.world;
        float breath = blockData.getFloat("Breath");
        AtomicInteger cooldown = new AtomicInteger(blockData.getInt("Cooldown"));
        ItemStack horn = blockData.contains("HornStack") ? ItemStack.of(blockData.getCompound("HornStack")) : ItemStack.EMPTY;

        if (cooldown.getAndDecrement() <= 0 && !horn.isEmpty() && level != null) {
            breath += Math.abs(context.getAnimationSpeed() * 0.1F) * 0.1F;
            if (breath >= BREATH_CAPACITY) {
                breath = -0.1F;
                if (horn.is(TFItems.CRUMBLE_HORN.get())) {
                    cooldown.set(CRUMBLE_COOLDOWN);
                    Vec3 vec3 = context.position.add(context.rotation.apply(Vec3.atLowerCornerOf(context.state.getValue(HornblowerBlock.FACING).getNormal())).scale(2.0F));
                    AABB crumbleBox = new AABB(vec3.x() - RADIUS, vec3.y() - RADIUS, vec3.z() - RADIUS, vec3.x() + RADIUS, vec3.y() + RADIUS, vec3.z() + RADIUS);
                    for (BlockPos pos : WorldUtil.getAllInBB(crumbleBox)) {
                        BlockState state = level.getBlockState(pos);
                        Block block = state.getBlock();
                        AtomicBoolean flag = new AtomicBoolean(false);

                        if (state.isAir()) continue;

                        if (level instanceof ServerLevel serverLevel) {
                            serverLevel.getRecipeManager().getAllRecipesFor(TFRecipes.CRUMBLE_RECIPE.get()).forEach(recipe -> {
                                if (flag.get()) return;
                                if (recipe.result().is(Blocks.AIR)) {
                                    if (recipe.input().is(block) && serverLevel.getRandom().nextInt(CHANCE_HARVEST) == 0 && !flag.get()) {
                                        serverLevel.destroyBlock(pos, true);
                                        flag.set(true);
                                    }
                                } else {
                                    if (recipe.input().is(block) && serverLevel.getRandom().nextInt(CHANCE_CRUMBLE) == 0 && !flag.get()) {
                                        serverLevel.setBlock(pos, recipe.result().getBlock().withPropertiesOf(state), 3);
                                        serverLevel.levelEvent(2001, pos, Block.getId(state));
                                        flag.set(true);
                                    }
                                }
                            });
                        }
                    }
                    level.playSound(null, context.position.x, context.position.y, context.position.z, TFSounds.QUEST_RAM_AMBIENT.get(), SoundSource.RECORDS, 1.0F, 0.8F);
                } else if (horn.getItem() instanceof InstrumentItem item) {
                    item.getInstrument(horn).ifPresent(instrument -> {
                        SoundEvent soundEvent = instrument.value().soundEvent().value();
                        float f = instrument.value().range() / 16.0F;
                        level.playSound(null, context.position.x, context.position.y, context.position.z, soundEvent, SoundSource.RECORDS, f, 1.0F);
                        level.gameEvent(GameEvent.INSTRUMENT_PLAY, context.position, GameEvent.Context.of(context.state));
                        cooldown.set(instrument.value().useDuration());
                    });
                }
            }
        }

        boolean powered = cooldown.get() > 0 && !horn.isEmpty();
        LerpedFloat animation = getAnimation(context);
        animation.chase(powered ? 1.0D : 0.0D, powered ? 0.5D : 0.4D, powered ? LerpedFloat.Chaser.EXP : LerpedFloat.Chaser.LINEAR);
        animation.tickChaser();

        blockData.putFloat("Breath", breath);
        blockData.putInt("Cooldown", cooldown.get());
    }

    private LerpedFloat getAnimation(MovementContext context) {
        if (!(context.temporaryData instanceof LerpedFloat)) context.temporaryData = LerpedFloat.linear();
        return (LerpedFloat) context.temporaryData;
    }

    @Override
    @OnlyIn(value = Dist.CLIENT)
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        HornblowerRenderer.renderInContraption(context, renderWorld, matrices, buffer, this.getAnimation(context));
    }

}
