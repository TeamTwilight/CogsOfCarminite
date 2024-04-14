package com.cogsofcarminite.blocks.entities;

import com.cogsofcarminite.CogsOfCarminite;
import com.cogsofcarminite.data.CCLangGenerator;
import com.cogsofcarminite.reg.CCIcons;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import twilightforest.init.TFBiomes;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CarminiteHeartBlockEntity extends CarminiteMagicLogBlockEntity {
    public ScrollOptionBehaviour<HeartMode> heartMode;
    public Holder<Biome> storedBiome;
    public ResourceLocation biomeID;

    public CarminiteHeartBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        if (this.biomeID != null) compound.putString("BiomeID", this.biomeID.toString());
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);

        this.biomeID = compound.contains("BiomeID") ? ResourceLocation.tryParse(compound.getString("BiomeID")) : null;
        if (this.biomeID == null) this.biomeID = TFBiomes.ENCHANTED_FOREST.location();

        if (this.level != null) {
            Registry<Biome> reg = this.level.registryAccess().registryOrThrow(Registries.BIOME);

            Biome biome = reg.get(this.biomeID);
            if (biome == null) this.storedBiome = reg.getHolderOrThrow(TFBiomes.ENCHANTED_FOREST);
            else this.storedBiome = reg.wrapAsHolder(biome);
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.heartMode = new ScrollOptionBehaviour<>(HeartMode.class,
                Component.translatable(CogsOfCarminite.MODID + ".logistics.mechanical_heart_mode"), this, new MagicLogSlot());
        behaviours.add(this.heartMode);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CCLangGenerator.translate("tooltip.mechanical_heart.header")
                .forGoggles(tooltip);

        CCLangGenerator.translate("tooltip.mechanical_heart.set_to")
                .style(ChatFormatting.GOLD)
                .forGoggles(tooltip);

        String[] id = this.biomeID != null ? this.biomeID.toString().split(":") : TFBiomes.ENCHANTED_FOREST.location().toString().split(":");
        Lang.builder("biome").translate(id[0] + "." + id[1])
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);

        float stressAtBase = calculateStressApplied();
        if (IRotate.StressImpact.isEnabled() && !Mth.equal(stressAtBase, 0)) {
            tooltip.add(Components.immutableEmpty());
            addStressImpactStats(tooltip, stressAtBase);
        }

        return true;
    }

    public enum HeartMode implements INamedIconOptions {
        TRANSFORM(CCIcons.HEART_TRANSFORM),
        ADAPT(CCIcons.HEART_ADAPT),
        REVERT(CCIcons.HEART_REVERT);

        private final String translationKey;
        private final CCIcons icon;

        HeartMode(CCIcons icon) {
            this.icon = icon;
            this.translationKey = CogsOfCarminite.MODID + ".logistics.mechanical_heart_mode." + Lang.asId(name());
        }

        @Override
        public AllIcons getIcon() {
            return this.icon;
        }

        @Override
        public String getTranslationKey() {
            return this.translationKey;
        }
    }
}
