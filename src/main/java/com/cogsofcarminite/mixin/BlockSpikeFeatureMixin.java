package com.cogsofcarminite.mixin;

import com.cogsofcarminite.util.CCStalactites;
import com.mojang.serialization.Codec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import twilightforest.world.components.feature.BlockSpikeFeature;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Mixin(BlockSpikeFeature.class)
public abstract class BlockSpikeFeatureMixin extends Feature<NoneFeatureConfiguration> {

    public BlockSpikeFeatureMixin(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    /**
     * @author jodlodi
     * @reason i wanna
     */
    @Overwrite
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource random = context.random();
        return BlockSpikeFeature.startSpike(context.level(), context.origin(), CCStalactites.getRandomStone(random), random, false);
    }
}
