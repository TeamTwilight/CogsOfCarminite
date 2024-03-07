package com.cogsofcarminite.mixin;

import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import twilightforest.item.OreMagnetItem;

import java.util.HashMap;

@Mixin(OreMagnetItem.class)
public interface OreMagnetItemAccessor {
    @Accessor("ORE_TO_BLOCK_REPLACEMENTS")
    HashMap<Block, Block> getOreToBlockReplacements();
}
