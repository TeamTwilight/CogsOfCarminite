package com.cogsofcarminite.data;

import com.cogsofcarminite.CogsOfCarminite;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.AquaticPlacements;
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
    public static final ResourceKey<BiomeModifier> LAKE_KELP = key("lake_kelp");

    private static ResourceKey<BiomeModifier> key(String name) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, CogsOfCarminite.prefix(name));
    }

    public static void bootstrap(BootstapContext<BiomeModifier> ctx) {
        HolderGetter<Biome> biomeLookup = ctx.lookup(Registries.BIOME);
        HolderSet<Biome> isTwilightForest = biomeLookup.getOrThrow(BiomeTagGenerator.IS_TWILIGHT);
        HolderSet<Biome> isTwilightForestLake = biomeLookup.getOrThrow(CCTags.Biomes.TWILIGHT_FOREST_KELP_WATER);

        HolderGetter<PlacedFeature> featureLookup = ctx.lookup(Registries.PLACED_FEATURE);
        Holder<PlacedFeature> zincOre = featureLookup.getOrThrow(CCPlacedFeatures.ZINC_ORE);
        Holder<PlacedFeature> kelp = featureLookup.getOrThrow(AquaticPlacements.KELP_COLD);

        ctx.register(ZINC_ORE, addOre(isTwilightForest, zincOre));
        ctx.register(LAKE_KELP, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(isTwilightForestLake, HolderSet.direct(kelp), GenerationStep.Decoration.VEGETAL_DECORATION));
    }

    private static ForgeBiomeModifiers.AddFeaturesBiomeModifier addOre(HolderSet<Biome> biomes, Holder<PlacedFeature> feature) {
        return new ForgeBiomeModifiers.AddFeaturesBiomeModifier(biomes, HolderSet.direct(feature), GenerationStep.Decoration.UNDERGROUND_ORES);
    }
}
