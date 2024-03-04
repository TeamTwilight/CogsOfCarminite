package com.cogsofcarminite.mixin;

import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Consumer;

@Mixin(FilteringBehaviour.class)
public interface FilteringBehaviourAccessor {
    @Accessor("filter")
    FilterItemStack getFilterItemStack();

    @Accessor("filter")
    void setFilterItemStack(FilterItemStack filterItemStack);

    @Accessor("callback")
    Consumer<ItemStack> getCallback();
}
