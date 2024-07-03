package com.cogsofcarminite.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import twilightforest.world.components.structures.HollowHillComponent;

@Mixin(HollowHillComponent.class)
public interface HollowHillComponentAccessor {

    @Accessor("radius")
    int getRadius();

    @Invoker("randomCeilingCoordinates")
    BlockPos.MutableBlockPos getRandomCeilingCoordinates(RandomSource rand, float maximumRadius);

}
