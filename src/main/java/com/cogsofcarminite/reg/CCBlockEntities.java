package com.cogsofcarminite.reg;

import com.cogsofcarminite.blocks.entities.*;
import com.cogsofcarminite.client.instances.CarminiteMagicLogInstance;
import com.cogsofcarminite.client.instances.HornblowerInstance;
import com.cogsofcarminite.client.renderers.CarminiteMagicLogRenderer;
import com.cogsofcarminite.client.renderers.HornblowerRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.cogsofcarminite.CogsOfCarminite.TWILIGHT_REGISTRATE;

public class CCBlockEntities {
    public static final BlockEntityEntry<CarminiteCoreBlockEntity> CARMINITE_CORE = TWILIGHT_REGISTRATE
            .blockEntity("carminite_core", CarminiteCoreBlockEntity::new)
            .instance(() -> CarminiteMagicLogInstance::new)
            .renderer(() -> CarminiteMagicLogRenderer::new)
            .validBlocks(CCBlocks.MECHANICAL_MINEWOOD_CORE)
            .register();

    public static final BlockEntityEntry<CarminiteHeartBlockEntity> CARMINITE_HEART = TWILIGHT_REGISTRATE
            .blockEntity("carminite_heart", CarminiteHeartBlockEntity::new)
            .instance(() -> CarminiteMagicLogInstance::new)
            .renderer(() -> CarminiteMagicLogRenderer::new)
            .validBlocks(CCBlocks.MECHANICAL_HEART_OF_TRANSFORMATION)
            .register();

    public static final BlockEntityEntry<CarminiteEngineBlockEntity> CARMINITE_ENGINE = TWILIGHT_REGISTRATE
            .blockEntity("carminite_engine", CarminiteEngineBlockEntity::new)
            .instance(() -> CarminiteMagicLogInstance::new)
            .renderer(() -> CarminiteMagicLogRenderer::new)
            .validBlocks(CCBlocks.MECHANICAL_SORTINGWOOD_ENGINE)
            .register();

    public static final BlockEntityEntry<CarminiteClockBlockEntity> CARMINITE_CLOCK = TWILIGHT_REGISTRATE
            .blockEntity("carminite_clock", CarminiteClockBlockEntity::new)
            .instance(() -> CarminiteMagicLogInstance::new)
            .renderer(() -> CarminiteMagicLogRenderer::new)
            .validBlocks(CCBlocks.MECHANICAL_TIMEWOOD_CLOCK)
            .register();

    public static final BlockEntityEntry<HornblowerBlockEntity> HORNBLOWER = TWILIGHT_REGISTRATE
            .blockEntity("hornblower", HornblowerBlockEntity::new)
            .instance(() -> HornblowerInstance::new)
            .renderer(() -> HornblowerRenderer::new)
            .validBlocks(CCBlocks.HORNBLOWER)
            .register();

    public static void register() {}
}
