package com.cogsofcarminite.data;

import com.cogsofcarminite.CogsOfCarminite;
import com.simibubi.create.AllItems;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import twilightforest.init.TFItems;

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
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AllItems.ANDESITE_ALLOY)
                .pattern("BA")
                .pattern("AB")
                .define('A', Ingredient.of(Blocks.ANDESITE))
                .define('B', Ingredient.of(TFItems.ARMOR_SHARD.get()))
                .unlockedBy("has_item", has(TFItems.ARMOR_SHARD.get()))
                .save(consumer, CogsOfCarminite.prefix("andesite_alloy_from_knightmetal"));
    }
}
