package com.cogsofcarminite.data;

import com.cogsofcarminite.CogsOfCarminite;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CCWorldgenDataProvider extends DatapackBuiltinEntriesProvider {
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, (RegistrySetBuilder.RegistryBootstrap) CCConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, CCPlacedFeatures::bootstrap)
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, CCBiomeModifiers::bootstrap);

    public CCWorldgenDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(CogsOfCarminite.MODID));
    }

    public static DataProvider.Factory<CCWorldgenDataProvider> makeFactory(CompletableFuture<HolderLookup.Provider> registries) {
        return output -> new CCWorldgenDataProvider(output, registries);
    }

    @Override
    public String getName() {
        return "Cogs Of Carminite's Worldgen Data";
    }
}
