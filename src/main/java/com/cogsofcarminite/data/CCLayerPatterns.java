package com.cogsofcarminite.data;

import com.simibubi.create.infrastructure.worldgen.LayerPattern;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.world.level.block.Blocks;

public class CCLayerPatterns {
    public static final NonNullSupplier<LayerPattern> ANDESITE = () -> LayerPattern.builder()
            .layer(l -> l.weight(2)
                    .block(Blocks.ANDESITE)
                    .size(2, 5))
            .layer(l -> l.weight(1)
                    .blocks(Blocks.TUFF, Blocks.ANDESITE)
                    .size(2, 2))
            .layer(l -> l.weight(1)
                    .block(Blocks.DIORITE)
                    .size(1, 2))
            .build();
}
