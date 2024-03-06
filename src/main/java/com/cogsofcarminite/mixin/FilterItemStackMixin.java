package com.cogsofcarminite.mixin;

import com.cogsofcarminite.reg.CCItems;
import com.cogsofcarminite.util.BlockFilterItemStack;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(FilterItemStack.class)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FilterItemStackMixin {
    @Inject(method = "of(Lnet/minecraft/world/item/ItemStack;)Lcom/simibubi/create/content/logistics/filter/FilterItemStack;", at = @At(value = "RETURN"), cancellable = true, remap = false)
    private static void od(ItemStack filter, CallbackInfoReturnable<FilterItemStack> cir) {
        if (filter.hasTag()) {
            if (CCItems.BLOCK_ATTRIBUTE_FILTER.isIn(filter)) cir.setReturnValue(new BlockFilterItemStack(filter));
        }
    }
}
