package com.cogsofcarminite.reg;

import com.cogsofcarminite.blocks.entities.CarminiteClockBlockEntity;
import com.cogsofcarminite.blocks.entities.CarminiteCoreBlockEntity;
import com.cogsofcarminite.blocks.entities.CarminiteEngineBlockEntity;
import com.cogsofcarminite.blocks.entities.CarminiteHeartBlockEntity;
import com.cogsofcarminite.client.instances.CarminiteMagicLogInstance;
import com.cogsofcarminite.client.renderers.CarminiteMagicLogRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.cogsofcarminite.CogsOfCarminite.TWILIGHT_REGISTRATE;

public class CCBlockEntities {
    public static final BlockEntityEntry<CarminiteCoreBlockEntity> CARMINITE_CORE = TWILIGHT_REGISTRATE
            .blockEntity("carminite_core", CarminiteCoreBlockEntity::new)
            .instance(() -> CarminiteMagicLogInstance::new)
            .renderer(() -> CarminiteMagicLogRenderer::new)
            .validBlocks(CCBlocks.CARMINITE_CORE)
            .register();

    public static final BlockEntityEntry<CarminiteHeartBlockEntity> CARMINITE_HEART = TWILIGHT_REGISTRATE
            .blockEntity("carminite_heart", CarminiteHeartBlockEntity::new)
            .instance(() -> CarminiteMagicLogInstance::new)
            .renderer(() -> CarminiteMagicLogRenderer::new)
            .validBlocks(CCBlocks.CARMINITE_HEART)
            .register();

    public static final BlockEntityEntry<CarminiteEngineBlockEntity> CARMINITE_ENGINE = TWILIGHT_REGISTRATE
            .blockEntity("carminite_engine", CarminiteEngineBlockEntity::new)
            .instance(() -> CarminiteMagicLogInstance::new)
            .renderer(() -> CarminiteMagicLogRenderer::new)
            .validBlocks(CCBlocks.CARMINITE_ENGINE)
            .register();

    public static final BlockEntityEntry<CarminiteClockBlockEntity> CARMINITE_CLOCK = TWILIGHT_REGISTRATE
            .blockEntity("carminite_clock", CarminiteClockBlockEntity::new)
            .instance(() -> CarminiteMagicLogInstance::new)
            .renderer(() -> CarminiteMagicLogRenderer::new)
            .validBlocks(CCBlocks.CARMINITE_CLOCK)
            .register();

    public static void register() {}
}
