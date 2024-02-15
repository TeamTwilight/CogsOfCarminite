package com.cogsofcarminite.blocks.entities;

import com.cogsofcarminite.blocks.HornblowerBlock;
import com.cogsofcarminite.data.CCLangGenerator;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import twilightforest.init.TFItems;
import twilightforest.init.TFRecipes;
import twilightforest.init.TFSounds;
import twilightforest.util.WorldUtil;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class HornblowerBlockEntity extends KineticBlockEntity {
    public static final int CHANCE_HARVEST = 20;
    public static final int CHANCE_CRUMBLE = 5;

    public static final int OFFSET = 3;
    public static final int RADIUS = 2;

    public static final float BREATH_CAPACITY = 128.0F;
    public static final int CRUMBLE_COOLDOWN = 20;

    public LerpedFloat animation = LerpedFloat.linear();
    public ItemStack horn = ItemStack.EMPTY;
    public float breath = 0.0F;
    public int cooldown = 0;

    public HornblowerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.cooldown-- <= 0 && !this.horn.isEmpty() && this.level != null) {
            this.breath += Math.abs(this.getSpeed()) * 0.1F;
            if (this.breath >= BREATH_CAPACITY) {
                this.breath = -0.1F;
                if (this.horn.is(TFItems.CRUMBLE_HORN.get())) {
                    this.cooldown = CRUMBLE_COOLDOWN;
                    Vec3 vec3 = Vec3.atCenterOf(this.getBlockPos().west().relative(this.getBlockState().getValue(HornblowerBlock.FACING), OFFSET));
                    AABB crumbleBox = new AABB(vec3.x() - RADIUS, vec3.y() - RADIUS, vec3.z() - RADIUS, vec3.x() + RADIUS, vec3.y() + RADIUS, vec3.z() + RADIUS);
                    for (BlockPos pos : WorldUtil.getAllInBB(crumbleBox)) {
                        BlockState state = this.level.getBlockState(pos);
                        Block block = state.getBlock();
                        AtomicBoolean flag = new AtomicBoolean(false);

                        if (state.isAir()) continue;

                        if (this.level instanceof ServerLevel serverLevel) {
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
                    this.level.playSound(null, this.getBlockPos(), TFSounds.QUEST_RAM_AMBIENT.get(), SoundSource.RECORDS, 1.0F, 0.8F);
                } else if (this.horn.getItem() instanceof InstrumentItem item) {
                    item.getInstrument(this.horn).ifPresent(instrument -> {
                        SoundEvent soundEvent = instrument.value().soundEvent().value();
                        float f = instrument.value().range() / 16.0F;
                        this.level.playSound(null, this.getBlockPos(), soundEvent, SoundSource.RECORDS, f, 1.0F);
                        this.level.gameEvent(GameEvent.INSTRUMENT_PLAY, this.getBlockPos(), GameEvent.Context.of(this.getBlockState()));
                        this.cooldown = instrument.value().useDuration();
                    });
                }
            }
        }

        boolean powered = this.cooldown > 0 && !this.horn.isEmpty();
        this.animation.chase(powered ? 1.0D : 0.0D, powered ? 0.5D : 0.4D, powered ? LerpedFloat.Chaser.EXP : LerpedFloat.Chaser.LINEAR);
        this.animation.tickChaser();
    }

    public @Nullable ItemStack setHorn(ItemStack stack) {
        this.breath = 0.0F;
        if (this.horn.isEmpty() && !stack.isEmpty()) {
            this.horn = stack.copy();
            stack.shrink(1);
            sendData();
            setChanged();
            return stack;
        } else if (!this.horn.isEmpty() && stack.isEmpty()) {
            stack = this.horn.copy();
            this.horn.shrink(1);
            sendData();
            setChanged();
            return stack;
        }
        return null;
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        if (!this.horn.isEmpty()) compound.put("HornStack", this.horn.serializeNBT());
        compound.putFloat("Breath", this.breath);
        compound.putInt("Cooldown", this.cooldown);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        this.horn = compound.contains("HornStack") ? ItemStack.of(compound.getCompound("HornStack")) : ItemStack.EMPTY;
        this.breath = compound.getFloat("Breath");
        this.cooldown = compound.getInt("Cooldown");
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CCLangGenerator.translate("tooltip.hornblower.header")
                .forGoggles(tooltip);

        if (!this.horn.isEmpty()) {
            CCLangGenerator.translate("tooltip.hornblower.contains", Components.translatable(this.horn.getDescriptionId()).getString())
                    .style(ChatFormatting.GREEN)
                    .forGoggles(tooltip);
            if (this.horn.getItem() instanceof InstrumentItem instrumentItem) {
                Optional<ResourceKey<Instrument>> optional = instrumentItem.getInstrument(this.horn).flatMap(Holder::unwrapKey);
                optional.ifPresent(instrumentResourceKey ->
                        CCLangGenerator.translate("tooltip.hornblower.instrument", Component.translatable(Util.makeDescriptionId("instrument", instrumentResourceKey.location())).getString())
                        .style(ChatFormatting.GRAY)
                        .forGoggles(tooltip));

            }
        } else
            CCLangGenerator.translate("tooltip.hornblower.empty")
                .style(ChatFormatting.YELLOW)
                .forGoggles(tooltip);

        float stressAtBase = calculateStressApplied();
        if (IRotate.StressImpact.isEnabled() && !Mth.equal(stressAtBase, 0)) {
            tooltip.add(Components.immutableEmpty());
            addStressImpactStats(tooltip, stressAtBase);
        }

        return true;
    }

}
