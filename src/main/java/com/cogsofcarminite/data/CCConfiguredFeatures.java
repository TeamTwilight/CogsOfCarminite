package com.cogsofcarminite.data;

import com.cogsofcarminite.CogsOfCarminite;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.infrastructure.worldgen.AllFeatures;
import com.simibubi.create.infrastructure.worldgen.AllLayerPatterns;
import com.simibubi.create.infrastructure.worldgen.LayerPattern;
import com.simibubi.create.infrastructure.worldgen.LayeredOreConfiguration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CCConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> ZINC_ORE = key("zinc_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> STRIATED_ORES_TWILIGHT_FOREST = key("striated_ores_twilight_forest");
    public static final ResourceKey<ConfiguredFeature<?, ?>> STRIATED_ANDESITE_TWILIGHT_FOREST = key("striated_andesite_twilight_forest");

    private static ResourceKey<ConfiguredFeature<?, ?>> key(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, CogsOfCarminite.prefix(name));
    }

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> ctx) {
        List<OreConfiguration.TargetBlockState> zincTargetStates = List.of(
                OreConfiguration.target(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), AllBlocks.ZINC_ORE.get().defaultBlockState())
        );

        FeatureUtils.register(ctx, ZINC_ORE, Feature.ORE, new OreConfiguration(zincTargetStates, 12));

        List<LayerPattern> twilightForestLayerPatterns = List.of(
                AllLayerPatterns.SCORIA.get(),
                AllLayerPatterns.CINNABAR.get(),
                AllLayerPatterns.MAGNETITE.get(),
                AllLayerPatterns.MALACHITE.get(),
                AllLayerPatterns.LIMESTONE.get(),
                AllLayerPatterns.OCHRESTONE.get()
        );

        FeatureUtils.register(ctx, STRIATED_ORES_TWILIGHT_FOREST, AllFeatures.LAYERED_ORE.get(), new LayeredOreConfiguration(twilightForestLayerPatterns, 32, 0));

        FeatureUtils.register(ctx, STRIATED_ANDESITE_TWILIGHT_FOREST, AllFeatures.LAYERED_ORE.get(), new LayeredOreConfiguration(List.of(CCLayerPatterns.ANDESITE.get()), 48, 0));
    }
}