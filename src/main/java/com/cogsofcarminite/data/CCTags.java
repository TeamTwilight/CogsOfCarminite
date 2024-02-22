package com.cogsofcarminite.data;

import com.cogsofcarminite.CogsOfCarminite;
import com.cogsofcarminite.reg.CCBlocks;
import com.cogsofcarminite.reg.CCItems;
import com.simibubi.create.AllTags;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import twilightforest.data.tags.ItemTagGenerator;
import twilightforest.init.TFBlocks;
import twilightforest.init.TFItems;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CCTags {
    public static class Blocks extends BlockTagsProvider {
        public static final TagKey<Block> ROOTS = BlockTags.create(CogsOfCarminite.prefix("roots"));

        public Blocks(PackOutput output, CompletableFuture<HolderLookup.Provider> future, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, future, CogsOfCarminite.MODID, existingFileHelper);
        }

        @Override
        public @NotNull String getName() {
            return "Cogs Of Carminite Block Tags";
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            this.tag(AllTags.AllBlockTags.FAN_TRANSPARENT.tag).add(
                    TFBlocks.WROUGHT_IRON_FENCE.get()
            );

            this.tag(AllTags.AllBlockTags.PASSIVE_BOILER_HEATERS.tag).add(
                    TFBlocks.FIERY_BLOCK.get(),
                    TFBlocks.FIRE_JET.get(),
                    TFBlocks.ENCASED_FIRE_JET.get()
            );

            this.tag(ROOTS).add(
                    net.minecraft.world.level.block.Blocks.MANGROVE_ROOTS,
                    TFBlocks.ROOT_BLOCK.get(),
                    TFBlocks.LIVEROOT_BLOCK.get(),
                    TFBlocks.MANGROVE_ROOT.get()
            );
        }
    }

    public static class Items extends ItemTagsProvider {
        public Items(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> future, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, future, CogsOfCarminite.MODID, existingFileHelper);
        }

        @Override
        public @NotNull String getName() {
            return "Cogs Of Carminite Item Tags";
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            this.tag(AllTags.AllItemTags.UPRIGHT_ON_BELT.tag).add(
                    TFItems.FIERY_BLOOD.get(),
                    TFItems.FIERY_TEARS.get(),
                    TFItems.EXPERIMENT_115.get()
            );

            this.tag(ItemTagGenerator.NYI).add(
                    CCItems.INCOMPLETE_GEAR_GADGET.get(),
                    CCItems.CARMINITE_GEAR_GADGET.get(),
                    CCBlocks.MECHANICAL_TIMEWOOD_CLOCK.asItem(),
                    CCBlocks.MECHANICAL_MINEWOOD_CORE.asItem(),
                    CCBlocks.MECHANICAL_SORTINGWOOD_ENGINE.asItem(),
                    CCBlocks.MECHANICAL_HEART_OF_TRANSFORMATION.asItem(),
                    CCItems.FIERY_SHEET.get(),
                    CCItems.KNIGHTMETAL_SHEET.get()
            );
        }
    }
}
