package com.cogsofcarminite.behaviour;

import com.cogsofcarminite.CogsOfCarminite;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import twilightforest.data.tags.BlockTagGenerator;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class OreFilteringBehaviour extends BlockFilteringBehaviour {
    public OreFilteringBehaviour(SmartBlockEntity be, ValueBoxTransform slot) {
        super(be, slot);
    }

    @Override
    public MutableComponent getLabel() {
        return Component.translatable(CogsOfCarminite.MODID + ".logistics.ore_filter");
    }

    @Override
    public boolean setFilter(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            BlockState state = blockItem.getBlock().defaultBlockState();
            if (state.is(Tags.Blocks.ORES) && !state.is(BlockTagGenerator.ORE_MAGNET_IGNORE))
                return super.setFilter(stack);
        }
        return super.setFilter(stack);
    }
}
