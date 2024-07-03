package com.cogsofcarminite.mixin;

import com.cogsofcarminite.util.CCStalactites;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import twilightforest.init.TFBlocks;
import twilightforest.world.components.structures.HollowHillComponent;
import twilightforest.world.components.structures.HydraLairComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Mixin(HydraLairComponent.class)
public abstract class HydraLairComponentMixin extends HollowHillComponent {
    public HydraLairComponentMixin(StructurePieceSerializationContext ctx, CompoundTag nbt) {
        super(ctx, nbt);
    }

    /**
     * @author jodlodi
     * @reason this is twilight tweaks now
     */
    @Overwrite
    public void postProcess(WorldGenLevel world, StructureManager manager, ChunkGenerator generator, RandomSource rand, BoundingBox sbb, ChunkPos chunkPosIn, BlockPos blockPos) {
        int stalacts = 64;
        int stalags = 8;

        // fill in features
        // ore or glowing stalactites! (smaller, less plentiful)
        for (int i = 0; i < stalacts; i++) {
            BlockPos.MutableBlockPos dest = ((HollowHillComponentAccessor)this).getRandomCeilingCoordinates(rand, ((HollowHillComponentAccessor)this).getRadius());
            this.generateOreStalactite(world, dest.move(0, 1, 0), sbb);
        }
        // stone stalactites!
        for (int i = 0; i < stalacts; i++) {
            BlockPos.MutableBlockPos dest = ((HollowHillComponentAccessor)this).getRandomCeilingCoordinates(rand, ((HollowHillComponentAccessor)this).getRadius());
            this.generateBlockSpike(world, CCStalactites.getRandomHot(rand), dest.getX(), dest.getY(), dest.getZ(), sbb, true);
        }
        // stone stalagmites!
        for (int i = 0; i < stalags; i++) {
            BlockPos.MutableBlockPos dest = ((HollowHillComponentAccessor)this).getRandomCeilingCoordinates(rand, ((HollowHillComponentAccessor)this).getRadius());
            this.generateBlockSpike(world, CCStalactites.getRandomHot(rand), dest.getX(), dest.getY(), dest.getZ(), sbb, false);
        }

        // boss spawner seems important
        placeBlock(world, TFBlocks.HYDRA_BOSS_SPAWNER.get().defaultBlockState(), 27, 3, 27, sbb);
    }
}
