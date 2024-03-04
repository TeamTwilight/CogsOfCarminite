package com.cogsofcarminite.behaviour;

import com.cogsofcarminite.items.BlockFilterItem;
import com.cogsofcarminite.mixin.FilteringBehaviourAccessor;
import com.cogsofcarminite.util.BlockFilterItemStack;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockOrItemFilteringBehaviour extends FilteringBehaviour {
    public BlockOrItemFilteringBehaviour(SmartBlockEntity be, ValueBoxTransform slot) {
        super(be, slot);
    }

    public FilterItemStack getFilterStack() {
        return ((FilteringBehaviourAccessor)this).getFilterItemStack();
    }

    @Override
    public void read(CompoundTag nbt, boolean clientPacket) {
        ((FilteringBehaviourAccessor)this).setFilterItemStack(BlockFilterItemStack.od(nbt.getCompound("Filter")));
        this.count = nbt.getInt("FilterAmount");
        this.upTo = nbt.getBoolean("UpTo");

        // Migrate from previous behaviour
        if (this.count == 0) {
            this.upTo = true;
            this.count = this.getFilter().getMaxStackSize();
        }
    }

    @Override
    public boolean setFilter(ItemStack stack) {
        if (stack.getItem() instanceof BlockFilterItem) {
            ItemStack filter = stack.copy();
            ((FilteringBehaviourAccessor)this).setFilterItemStack(BlockFilterItemStack.od(stack));
            if (!this.upTo) this.count = Math.min(this.count, stack.getMaxStackSize());
            ((FilteringBehaviourAccessor)this).getCallback().accept(filter);
            this.blockEntity.setChanged();
            this.blockEntity.sendData();
            return true;
        }
        return super.setFilter(stack);
    }
}
