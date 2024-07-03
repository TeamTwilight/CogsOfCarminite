package com.cogsofcarminite.util;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.RandomSource;
import twilightforest.data.custom.stalactites.entry.Stalactite;
import twilightforest.world.components.feature.BlockSpikeFeature;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CCStalactites {
    public static final Stalactite ZINC = new Stalactite(Map.of(AllBlocks.ZINC_ORE.get(), 20, AllBlocks.RAW_ZINC_BLOCK.get(), 1), 0.7F, 8, 24);

    public static final Stalactite ASURINE = new Stalactite(Map.of(AllPaletteStoneTypes.ASURINE.getBaseBlock().get(), 1), 0.25F, 11, 1);
    public static final Stalactite CRIMSITE = new Stalactite(Map.of(AllPaletteStoneTypes.CRIMSITE.getBaseBlock().get(), 1), 0.25F, 11, 1);
    public static final Stalactite LIMESTONE = new Stalactite(Map.of(AllPaletteStoneTypes.LIMESTONE.getBaseBlock().get(), 1), 0.25F, 11, 1);
    public static final Stalactite OCHRUM = new Stalactite(Map.of(AllPaletteStoneTypes.OCHRUM.getBaseBlock().get(), 1), 0.25F, 11, 1);
    public static final Stalactite SCORIA = new Stalactite(Map.of(AllPaletteStoneTypes.SCORIA.getBaseBlock().get(), 1), 0.25F, 11, 1);
    public static final Stalactite SCORCHIA = new Stalactite(Map.of(AllPaletteStoneTypes.SCORCHIA.getBaseBlock().get(), 1), 0.25F, 11, 1);
    public static final Stalactite VERIDIUM = new Stalactite(Map.of(AllPaletteStoneTypes.VERIDIUM.getBaseBlock().get(), 1), 0.25F, 11, 1);
    
    public static final Stalactite[] STONE_STALACTITES = new Stalactite[] { LIMESTONE, OCHRUM, VERIDIUM };
    public static final Stalactite[] HOT_STALACTITES = new Stalactite[] { CRIMSITE, SCORIA, SCORCHIA };

    public static Stalactite getRandomStone(RandomSource rand) {
        return rand.nextInt(10) == 0 ? CCStalactites.STONE_STALACTITES[rand.nextInt(CCStalactites.STONE_STALACTITES.length)] : BlockSpikeFeature.STONE_STALACTITE;
    }

    public static Stalactite getRandomHot(RandomSource rand) {
        return rand.nextInt(10) == 0 ? CCStalactites.HOT_STALACTITES[rand.nextInt(CCStalactites.HOT_STALACTITES.length)] : BlockSpikeFeature.STONE_STALACTITE;
    }

    public static Stalactite getRandomCold(RandomSource rand) {
        return  rand.nextInt(5) == 0 ? CCStalactites.ASURINE : BlockSpikeFeature.STONE_STALACTITE;
    }
}
