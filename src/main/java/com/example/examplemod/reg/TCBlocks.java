package com.example.examplemod.reg;

import com.example.examplemod.blocks.CarminiteClock;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.material.MapColor;

import static com.example.examplemod.ExampleMod.TWILIGHT_REGISTRATE;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class TCBlocks {
    static { TWILIGHT_REGISTRATE.useCreativeTab(AllCreativeModeTabs.MAIN_TAB); }

    public static final BlockEntry<CarminiteClock> CARMINITE_CLOCK =
            TWILIGHT_REGISTRATE.block("carminite_clock", CarminiteClock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(p -> p.mapColor(MapColor.COLOR_GRAY))
                    .transform(pickaxeOnly())
                    .transform(BlockStressDefaults.setImpact(0))
                    .blockstate((c, p) -> p.horizontalBlock(c.get(), AssetLookup.partialBaseModel(c, p)))
                    .lang("Carminite Clock")
                    .item()
                    .transform(customItemModel())
                    .register();

    public static void register() {}
}
