package com.cogsofcarminite;

import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.minecraft.world.level.block.entity.BlockEntity;
import twilightforest.TwilightForestMod;

public class ASMHooks {
    public static boolean filterAsBlock(boolean prevResult, FilteringBehaviour filter, BlockEntity be) {
        TwilightForestMod.LOGGER.error("Prev result was {}", prevResult);
        return prevResult;
    }
}
