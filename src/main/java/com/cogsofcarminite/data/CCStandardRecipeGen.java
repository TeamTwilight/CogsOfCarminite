package com.cogsofcarminite.data;

import com.cogsofcarminite.reg.CCBlocks;
import com.cogsofcarminite.reg.CCItems;
import com.simibubi.create.AllBlocks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CCStandardRecipeGen extends RecipeProvider {
    public CCStandardRecipeGen(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CCBlocks.HORNBLOWER)
                .pattern("S")
                .pattern("C")
                .define('S', Ingredient.of(CCItems.IRONWOOD_SHEET))
                .define('C', Ingredient.of(AllBlocks.ANDESITE_CASING))
                .unlockedBy("has_item", has(CCItems.IRONWOOD_SHEET))
                .save(consumer, CCBlocks.HORNBLOWER.getId());

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CCBlocks.MECHANICAL_ROOT_PULLER)
                .pattern(" P ")
                .pattern("PCP")
                .pattern(" A ")
                .define('P', Ingredient.of(CCItems.IRONWOOD_SHEET))
                .define('C', Ingredient.of(AllBlocks.COGWHEEL))
                .define('A', Ingredient.of(AllBlocks.ANDESITE_CASING))
                .unlockedBy("has_item", has(CCItems.IRONWOOD_SHEET))
                .save(consumer, CCBlocks.MECHANICAL_ROOT_PULLER.getId());
    }
}
