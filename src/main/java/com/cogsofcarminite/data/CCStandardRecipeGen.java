package com.cogsofcarminite.data;

import com.cogsofcarminite.reg.CCBlocks;
import com.cogsofcarminite.reg.CCItems;
import com.simibubi.create.AllBlocks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.crafting.Ingredient;
import twilightforest.init.TFBlocks;
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

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CCItems.IRONWOOD_NUGGET, 9)
                .requires(TFItems.IRONWOOD_INGOT.get())
                .unlockedBy("has_item", has(TFItems.IRONWOOD_INGOT.get()))
                .save(consumer, CCItems.IRONWOOD_NUGGET.getId());

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TFItems.IRONWOOD_INGOT.get())
                .pattern("NNN")
                .pattern("NNN")
                .pattern("NNN")
                .define('N', Ingredient.of(CCItems.IRONWOOD_NUGGET))
                .unlockedBy("has_item", has(CCItems.IRONWOOD_NUGGET))
                .save(consumer, TFItems.IRONWOOD_INGOT.getId().withSuffix("_from_nuggets"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CCItems.BLOCK_ATTRIBUTE_FILTER)
                .requires(CCItems.BLOCK_ATTRIBUTE_FILTER)
                .unlockedBy("has_item", has(CCItems.BLOCK_ATTRIBUTE_FILTER))
                .save(consumer, CCItems.BLOCK_ATTRIBUTE_FILTER.getId().withSuffix("_clear"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CCItems.BLOCK_ATTRIBUTE_FILTER)
                .pattern("NFN")
                .define('N', Ingredient.of(CCItems.IRONWOOD_NUGGET))
                .define('F', Ingredient.of(TFBlocks.ARCTIC_FUR_BLOCK.get()))
                .unlockedBy("has_item", has(TFItems.ARCTIC_FUR.get()))
                .save(consumer, CCItems.BLOCK_ATTRIBUTE_FILTER.getId());
    }
}
