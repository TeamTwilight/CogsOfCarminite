package com.cogsofcarminite.data;

import com.cogsofcarminite.CogsOfCarminite;
import com.cogsofcarminite.reg.CCBlocks;
import com.cogsofcarminite.reg.CCItems;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.Create;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.data.recipe.CreateRecipeProvider;
import com.simibubi.create.foundation.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;
import twilightforest.init.TFBlocks;
import twilightforest.init.TFItems;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CCRecipeGen extends CreateRecipeProvider {
    protected static final List<ProcessingRecipeGen> CCGENERATORS = new ArrayList<>();

    public CCRecipeGen(PackOutput output) {
        super(output);
    }

    public static void registerAll(DataGenerator gen, PackOutput output) {
        CCGENERATORS.add(new ItemApplication(output));
        CCGENERATORS.add(new Pressing(output));
        CCGENERATORS.add(new Milling(output));
        CCGENERATORS.add(new Crushing(output));

        gen.addProvider(true, new DataProvider() {

            @Override
            public String getName() {
                return "Cogs Of Carminite's Recipes";
            }

            @Override
            public CompletableFuture<?> run(CachedOutput dc) {
                return CompletableFuture.allOf(CCGENERATORS.stream()
                        .map(gen -> gen.run(dc))
                        .toArray(CompletableFuture[]::new));
            }
        });
    }

    public static class ItemApplication extends ProcessingRecipeGen {
        GeneratedRecipe DARK_TOWER_CASING = create(CogsOfCarminite.prefix("dark_tower_casing"), b -> b
                .require(TFBlocks.ENCASED_TOWERWOOD.get())
                .require(Tags.Items.DYES)
                .output(CCBlocks.DARK_TOWER_CASING.get()));

        protected <T extends ProcessingRecipe<?>> GeneratedRecipe create(String name, UnaryOperator<ProcessingRecipeBuilder<T>> transform) {
            return create(Create.asResource(name), transform);
        }

        public ItemApplication(PackOutput output) {
            super(output);
        }

        @Override
        protected AllRecipeTypes getRecipeType() {
            return AllRecipeTypes.ITEM_APPLICATION;
        }
    }

    public static class Pressing extends ProcessingRecipeGen {
        GeneratedRecipe IRONWOOD = create(CogsOfCarminite.prefix("ironwood_ingot"), b -> b.require(TFItems.IRONWOOD_INGOT.get()).output(CCItems.IRONWOOD_SHEET));
        GeneratedRecipe KNIGHTMETAL = create(CogsOfCarminite.prefix("knightmetal_ingot"), b -> b.require(TFItems.KNIGHTMETAL_INGOT.get()).output(CCItems.KNIGHTMETAL_SHEET));
        GeneratedRecipe FIERY = create(CogsOfCarminite.prefix("fiery_ingot"), b -> b.require(TFItems.FIERY_INGOT.get()).output(CCItems.FIERY_SHEET));

        GeneratedRecipe FIREFLY = create(CogsOfCarminite.prefix("firefly_squish"), b -> b.require(TFItems.FIREFLY.get()).output(Items.GLOWSTONE_DUST));
        GeneratedRecipe CICADA = create(CogsOfCarminite.prefix("cicada_squish"), b -> b.require(TFItems.CICADA.get()).output(Items.GRAY_DYE));
        GeneratedRecipe MOONWORM = create(CogsOfCarminite.prefix("moonworm_squish"), b -> b.require(TFItems.MOONWORM.get()).output(Items.LIME_DYE));


        public Pressing(PackOutput generator) {
            super(generator);
        }

        @Override
        protected IRecipeTypeInfo getRecipeType() {
            return AllRecipeTypes.PRESSING;
        }
    }

    public static class Milling extends ProcessingRecipeGen {
        GeneratedRecipe HUGE_WATER_LILLY = create(CogsOfCarminite.prefix("huge_water_lilly"), b -> b.duration(50).withItemIngredients(Ingredient.of(TFBlocks.HUGE_WATER_LILY.get()))
                .output(Items.MAGENTA_DYE, 2)
                .output(.1f, Items.PURPLE_DYE)
                .output(.1f, Items.PINK_DYE, 2));

        GeneratedRecipe THORN_ROSE = create(CogsOfCarminite.prefix("thorn_rose"), b -> b.duration(50).withItemIngredients(Ingredient.of(TFBlocks.THORN_ROSE.get()))
                .output(Items.RED_DYE, 2)
                .output(.1f, Items.RED_DYE, 1)
                .output(.1f, Items.GREEN_DYE, 2));

        GeneratedRecipe FIDDLEHEAD = create(CogsOfCarminite.prefix("fiddlehead"), b -> b.duration(50).withItemIngredients(Ingredient.of(TFBlocks.FIDDLEHEAD.get()))
                .output(Items.GREEN_DYE)
                .output(.1f, Items.WHEAT_SEEDS));

        GeneratedRecipe MAYAPPLE = create(CogsOfCarminite.prefix("mayapple"), b -> b.duration(50).withItemIngredients(Ingredient.of(TFBlocks.MAYAPPLE.get()))
                .output(Items.GREEN_DYE)
                .output(.1f, Items.GREEN_DYE));

        public Milling(PackOutput generator) {
            super(generator);
        }

        @Override
        protected IRecipeTypeInfo getRecipeType() {
            return AllRecipeTypes.MILLING;
        }
    }

    public static class Crushing extends ProcessingRecipeGen {
        GeneratedRecipe DEADROCK = create(CogsOfCarminite.prefix("deadrock"), b -> b.duration(250).withItemIngredients(Ingredient.of(TFBlocks.DEADROCK.get()))
                .output(TFBlocks.CRACKED_DEADROCK.get())
			.output(.1f, Items.FLINT)
			.output(.05f, Items.CLAY_BALL));

        public Crushing(PackOutput generator) {
            super(generator);
        }

        @Override
        protected IRecipeTypeInfo getRecipeType() {
            return AllRecipeTypes.CRUSHING;
        }
    }
}
