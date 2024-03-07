package com.cogsofcarminite.mixin;

import net.minecraft.MethodsReturnNonnullByDefault;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twilightforest.item.OreMagnetItem;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(OreMagnetItem.class)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class OreMagnetItemMixin {
    @Inject(method = "initOre2BlockMap", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private static void initOre2BlockMap(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "buildOreMagnetCache", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private static void buildOreMagnetCache(CallbackInfo ci) {
        ci.cancel();
    }
}
