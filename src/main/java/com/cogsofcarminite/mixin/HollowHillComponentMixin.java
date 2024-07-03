package com.cogsofcarminite.mixin;

import com.cogsofcarminite.util.CCStalactites;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import twilightforest.world.components.feature.BlockSpikeFeature;
import twilightforest.world.components.structures.HollowHillComponent;
import twilightforest.world.components.structures.TFStructureComponentOld;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(HollowHillComponent.class)
@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class HollowHillComponentMixin extends TFStructureComponentOld {

    @Shadow protected abstract float getCeilingHeight(float dist);
    @Shadow @Final private static float ORE_STALACTITE_CHANCE;
    @Shadow @Final private int hillSize;

    public HollowHillComponentMixin(StructurePieceType piece, CompoundTag nbt) {
        super(piece, nbt);
    }

    /**
     * @author jodlodi
     * @reason this is twilight tweaks now
     */
    @Overwrite(remap = false)
    private void placeCeilingFeature(WorldGenLevel world, RandomSource rand, BlockPos.MutableBlockPos pos, int distSq) {
        BlockPos ceiling = pos.atY(this.getWorldY(Mth.ceil(this.getCeilingHeight(Mth.sqrt(distSq)))));
        if (rand.nextFloat() > ORE_STALACTITE_CHANCE) {
            BlockSpikeFeature.startSpike(world, ceiling, rand.nextInt(20) == 0 ? CCStalactites.ZINC : BlockSpikeFeature.makeRandomOreStalactite(rand, this.hillSize), rand, true);
        } else {
            BlockSpikeFeature.startSpike(world, ceiling, CCStalactites.getRandomStone(rand), rand, true);
        }
    }
}
