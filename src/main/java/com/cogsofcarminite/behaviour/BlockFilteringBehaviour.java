package com.cogsofcarminite.behaviour;

import com.cogsofcarminite.CCUtil;
import com.cogsofcarminite.CogsOfCarminite;
import com.cogsofcarminite.items.BlockFilterItem;
import com.cogsofcarminite.util.BlockFilterItemStack;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockFilteringBehaviour extends FilteringBehaviour {
    public BlockFilteringBehaviour(SmartBlockEntity be, ValueBoxTransform slot) {
        super(be, slot);
    }

    @Override
    public MutableComponent getLabel() {
        return Component.translatable(CogsOfCarminite.MODID + ".logistics.block_filter");
    }

    @Override
    public void write(CompoundTag nbt, boolean clientPacket) {
        nbt.put("Filter", this.getFilter().serializeNBT());
        nbt.putInt("FilterAmount", this.count);
        nbt.putBoolean("UpTo", this.upTo);
    }

    @Override
    public void read(CompoundTag nbt, boolean clientPacket) {
        CCUtil.reflectAndSet(CCUtil.FILTER_FIELD, this, BlockFilterItemStack.od(nbt.getCompound("Filter")));
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
            CCUtil.<FilterItemStack>reflectAndSet(CCUtil.FILTER_FIELD, this, new BlockFilterItemStack(stack));
            if (!this.upTo) this.count = Math.min(this.count, stack.getMaxStackSize());
            CCUtil.<Consumer<ItemStack>>reflectAndGet(CCUtil.CALLBACK, this).accept(filter);
            this.blockEntity.setChanged();
            this.blockEntity.sendData();
            return true;
        }
        if (stack.isEmpty() || stack.getItem() instanceof BlockItem || stack.getItem() instanceof FilterItem) return super.setFilter(stack);
        return false;
    }

    public static boolean emptyOrBlock(ItemStack stack) {
        return stack.isEmpty() || stack.getItem() instanceof BlockItem;
    }

    public static Optional<BlockItem> unfiltered(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) return Optional.of(blockItem);
        return Optional.empty();
    }
}
