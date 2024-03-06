package com.cogsofcarminite.data;

import com.cogsofcarminite.CogsOfCarminite;
import com.cogsofcarminite.reg.CCBlocks;
import com.cogsofcarminite.reg.CCItems;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder;
import com.simibubi.create.foundation.data.recipe.CreateRecipeProvider;
import com.simibubi.create.foundation.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import twilightforest.init.TFBlocks;
import twilightforest.init.TFItems;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SuppressWarnings("SameParameterValue")
public class CCProcessingRecipeGen extends CreateRecipeProvider {
    protected static final List<ProcessingRecipeGen> CCGENERATORS = new ArrayList<>();

    public CCProcessingRecipeGen(PackOutput output) {
        super(output);
    }

    public static void registerAll(DataGenerator gen, PackOutput output) {
        CCGENERATORS.add(new ItemApplication(output));
        CCGENERATORS.add(new Pressing(output));
        CCGENERATORS.add(new Milling(output));
        CCGENERATORS.add(new Crushing(output));
        CCGENERATORS.add(new Mixing(output));
        CCGENERATORS.add(new Haunting(output));
        CCGENERATORS.add(new SequencedAssembly(output));

        gen.addProvider(true, new DataProvider() {
            @Override
            public String getName() {
                return "Cogs Of Carminite's Processing Recipes";
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

        GeneratedRecipe CARMINITE_CLOCK = create(CogsOfCarminite.prefix("carminite_clock"), b -> b
                .require(TFBlocks.TIME_LOG_CORE.get())
                .require(CCItems.CARMINITE_GEAR_GADGET)
                .output(CCBlocks.MECHANICAL_TIMEWOOD_CLOCK.get()));

        GeneratedRecipe CARMINITE_CORE = create(CogsOfCarminite.prefix("carminite_core"), b -> b
                .require(TFBlocks.MINING_LOG_CORE.get())
                .require(CCItems.CARMINITE_GEAR_GADGET)
                .output(CCBlocks.MECHANICAL_MINEWOOD_CORE.get()));

        GeneratedRecipe CARMINITE_ENGINE = create(CogsOfCarminite.prefix("carminite_engine"), b -> b
                .require(TFBlocks.SORTING_LOG_CORE.get())
                .require(CCItems.CARMINITE_GEAR_GADGET)
                .output(CCBlocks.MECHANICAL_SORTINGWOOD_ENGINE.get()));

        GeneratedRecipe CARMINITE_HEART = create(CogsOfCarminite.prefix("carminite_heart"), b -> b
                .require(TFBlocks.TRANSFORMATION_LOG_CORE.get())
                .require(CCItems.CARMINITE_GEAR_GADGET)
                .output(CCBlocks.MECHANICAL_HEART_OF_TRANSFORMATION.get()));

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

        GeneratedRecipe ARMOR_SHARD = create(CogsOfCarminite.prefix("armor_shard"), b -> b.duration(200).withItemIngredients(Ingredient.of(TFItems.ARMOR_SHARD_CLUSTER.get()))
                .output(TFItems.ARMOR_SHARD.get(), 9));

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

        GeneratedRecipe ARMOR_SHARD = create(CogsOfCarminite.prefix("armor_shard"), b -> b.duration(200).withItemIngredients(Ingredient.of(TFItems.KNIGHTMETAL_INGOT.get()))
                .output(TFItems.ARMOR_SHARD.get(), 7)
                .output(.5f, TFItems.ARMOR_SHARD.get())
                .output(.25f, TFItems.ARMOR_SHARD.get()));

        public Crushing(PackOutput generator) {
            super(generator);
        }

        @Override
        protected IRecipeTypeInfo getRecipeType() {
            return AllRecipeTypes.CRUSHING;
        }
    }

    public static class Mixing extends ProcessingRecipeGen {
        /*GeneratedRecipe CARMINITE = create(CogsOfCarminite.prefix("carminite"), b -> b.require(TFItems.BORER_ESSENCE.get())
                .require(TFItems.BORER_ESSENCE.get())
                .require(TFItems.BORER_ESSENCE.get())
                .require(Items.REDSTONE)
                .require(Items.REDSTONE)
                .require(Items.REDSTONE)
                .require(Items.GHAST_TEAR)
                .requiresHeat(HeatCondition.HEATED)
                .output(TFItems.CARMINITE.get(), 1));*/

        public Mixing(PackOutput generator) {
            super(generator);
        }

        @Override
        protected IRecipeTypeInfo getRecipeType() {
            return AllRecipeTypes.MIXING;
        }
    }

    public static class Haunting extends ProcessingRecipeGen {
        GeneratedRecipe HAUNT_TOWERWOOD_PLANKS = convert(TFBlocks.TOWERWOOD.get(), TFBlocks.INFESTED_TOWERWOOD.get());

        public Haunting(PackOutput generator) {
            super(generator);
        }

        public GeneratedRecipe convert(ItemLike input, ItemLike result) {
            return convert(() -> Ingredient.of(input), () -> result);
        }

        public GeneratedRecipe convert(Supplier<Ingredient> input, Supplier<ItemLike> result) {
            return create(Create.asResource(RegisteredObjects.getKeyOrThrow(result.get()
                                    .asItem())
                            .getPath()),
                    p -> p.withItemIngredients(input.get())
                            .output(result.get()));
        }
        @Override
        protected IRecipeTypeInfo getRecipeType() {
            return AllRecipeTypes.HAUNTING;
        }
    }

    public static class SequencedAssembly extends ProcessingRecipeGen {
        GeneratedRecipe CARMINITE_GEAR_GADGET = assemble("carminite_gear_gadget", b -> b.require(AllBlocks.FLYWHEEL)
                .transitionTo(CCItems.INCOMPLETE_GEAR_GADGET)
                .addOutput(CCItems.CARMINITE_GEAR_GADGET, 120)
                .addOutput(AllItems.PRECISION_MECHANISM, 12)
                .addOutput(AllBlocks.FLYWHEEL, 10)
                .addOutput(AllItems.BRASS_INGOT, 3)
                .addOutput(TFItems.CARMINITE.get(), 2)
                .addOutput(AllBlocks.SHAFT, 1)
                .addOutput(Items.REDSTONE, 1)
                .addOutput(TFBlocks.ENCASED_TOWERWOOD.get(), 1)
                .loops(4)
                .addStep(DeployerApplicationRecipe::new, rb -> rb.require(TFItems.CARMINITE.get()))
                .addStep(DeployerApplicationRecipe::new, rb -> rb.require(Items.REDSTONE))
                .addStep(PressingRecipe::new, rb -> rb)
                .addStep(DeployerApplicationRecipe::new, rb -> rb.require(AllItems.PRECISION_MECHANISM))
                .addStep(DeployerApplicationRecipe::new, rb -> rb.require(TFBlocks.ENCASED_TOWERWOOD.get()))
                .addStep(CuttingRecipe::new, rb -> rb));

        public SequencedAssembly(PackOutput generator) {
            super(generator);
        }

        protected GeneratedRecipe assemble(String name, UnaryOperator<SequencedAssemblyRecipeBuilder> transform) {
            GeneratedRecipe generatedRecipe =
                    c -> transform.apply(new SequencedAssemblyRecipeBuilder(CogsOfCarminite.prefix(name)))
                            .build(c);
            all.add(generatedRecipe);
            return generatedRecipe;
        }

        @Override
        protected IRecipeTypeInfo getRecipeType() {
            return AllRecipeTypes.SEQUENCED_ASSEMBLY;
        }
    }
}
