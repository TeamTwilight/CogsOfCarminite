package com.cogsofcarminite.data;

import com.cogsofcarminite.CogsOfCarminite;
import com.simibubi.create.infrastructure.worldgen.ConfigPlacementFilter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static net.minecraft.data.worldgen.placement.PlacementUtils.register;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CCPlacedFeatures {
    public static final ResourceKey<PlacedFeature> ZINC_ORE = key("zinc_ore");
    public static final ResourceKey<PlacedFeature> STRIATED_ORES_TWILIGHT_FOREST = key("striated_ores_twilight_forest");
    public static final ResourceKey<PlacedFeature> STRIATED_ANDESITE_TWILIGHT_FOREST = key("striated_andesite_twilight_forest");

    private static ResourceKey<PlacedFeature> key(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, CogsOfCarminite.prefix(name));
    }

    public static void bootstrap(BootstapContext<PlacedFeature> ctx) {
        HolderGetter<ConfiguredFeature<?, ?>> featureLookup = ctx.lookup(Registries.CONFIGURED_FEATURE);
        Holder<ConfiguredFeature<?, ?>> zincOre = featureLookup.getOrThrow(CCConfiguredFeatures.ZINC_ORE);
        Holder<ConfiguredFeature<?, ?>> striatedOresTwilightForest = featureLookup.getOrThrow(CCConfiguredFeatures.STRIATED_ORES_TWILIGHT_FOREST);
        Holder<ConfiguredFeature<?, ?>> striatedAndesiteTwilightForest = featureLookup.getOrThrow(CCConfiguredFeatures.STRIATED_ANDESITE_TWILIGHT_FOREST);

        register(ctx, ZINC_ORE, zincOre, placement(CountPlacement.of(8), -25, 5));
        register(ctx, STRIATED_ORES_TWILIGHT_FOREST, striatedOresTwilightForest, placement(RarityFilter.onAverageOnceEvery(18), -30, 70));
        register(ctx, STRIATED_ANDESITE_TWILIGHT_FOREST, striatedAndesiteTwilightForest, placement(RarityFilter.onAverageOnceEvery(6), -30, 100));
    }

    @Contract("_, _, _ -> new")
    private static @Unmodifiable List<PlacementModifier> placement(PlacementModifier frequency, int minHeight, int maxHeight) {
        return List.of(
                frequency,
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(minHeight), VerticalAnchor.absolute(maxHeight)),
                ConfigPlacementFilter.INSTANCE
        );
    }
}
