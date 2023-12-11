package com.cogsofcarminite.data;

import com.cogsofcarminite.CogsOfCarminite;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;
import twilightforest.data.tags.BiomeTagGenerator;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CCBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ZINC_ORE = key("zinc_ore");
    public static final ResourceKey<BiomeModifier> STRIATED_ORES_TWILIGHT_FOREST = key("striated_ores_twilight_forest");
    public static final ResourceKey<BiomeModifier> STRIATED_ANDESITE_TWILIGHT_FOREST = key("striated_andesite_twilight_forest");

    private static ResourceKey<BiomeModifier> key(String name) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, CogsOfCarminite.prefix(name));
    }

    public static void bootstrap(BootstapContext<BiomeModifier> ctx) {
        HolderGetter<Biome> biomeLookup = ctx.lookup(Registries.BIOME);
        HolderSet<Biome> isTwilightForest = biomeLookup.getOrThrow(BiomeTagGenerator.IS_TWILIGHT);

        HolderGetter<PlacedFeature> featureLookup = ctx.lookup(Registries.PLACED_FEATURE);
        Holder<PlacedFeature> zincOre = featureLookup.getOrThrow(CCPlacedFeatures.ZINC_ORE);
        Holder<PlacedFeature> striatedOresTwilightForest = featureLookup.getOrThrow(CCPlacedFeatures.STRIATED_ORES_TWILIGHT_FOREST);
        Holder<PlacedFeature> striatedAndesiteTwilightForest = featureLookup.getOrThrow(CCPlacedFeatures.STRIATED_ANDESITE_TWILIGHT_FOREST);

        ctx.register(ZINC_ORE, addOre(isTwilightForest, zincOre));
        ctx.register(STRIATED_ORES_TWILIGHT_FOREST, addOre(isTwilightForest, striatedOresTwilightForest));
        ctx.register(STRIATED_ANDESITE_TWILIGHT_FOREST, addOre(isTwilightForest, striatedAndesiteTwilightForest));
    }

    private static ForgeBiomeModifiers.AddFeaturesBiomeModifier addOre(HolderSet<Biome> biomes, Holder<PlacedFeature> feature) {
        return new ForgeBiomeModifiers.AddFeaturesBiomeModifier(biomes, HolderSet.direct(feature), GenerationStep.Decoration.UNDERGROUND_ORES);
    }
}
