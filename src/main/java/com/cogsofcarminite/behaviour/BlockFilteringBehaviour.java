package com.cogsofcarminite.behaviour;

import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockFilteringBehaviour extends FilteringBehaviour {
    public BlockFilteringBehaviour(SmartBlockEntity be, ValueBoxTransform slot) {
        super(be, slot);
    }

    @Override
    public boolean setFilter(ItemStack stack) {
        if (stack.isEmpty() || stack.getItem() instanceof BlockItem || stack.getItem() instanceof FilterItem) return super.setFilter(stack);
        return false;
    }

    public static Optional<BlockItem> unfiltered(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) return Optional.of(blockItem);
        return Optional.empty();
    }
}
