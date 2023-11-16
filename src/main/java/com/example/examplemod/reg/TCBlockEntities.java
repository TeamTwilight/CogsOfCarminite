package com.example.examplemod.reg;

import com.example.examplemod.blocks.entities.CarminiteClockBlockEntity;
import com.example.examplemod.client.CarminiteClockRenderer;
import com.example.examplemod.instances.CarminiteClockInstance;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.example.examplemod.ExampleMod.TWILIGHT_REGISTRATE;

public class TCBlockEntities {
    public static final BlockEntityEntry<CarminiteClockBlockEntity> CARMINITE_CLOCK = TWILIGHT_REGISTRATE
            .blockEntity("carminite_clock", CarminiteClockBlockEntity::new)
            .instance(() -> CarminiteClockInstance::new)
            .renderer(() -> CarminiteClockRenderer::new)
            .validBlocks(TCBlocks.CARMINITE_CLOCK)
            .register();

    public static void register() {}
}
