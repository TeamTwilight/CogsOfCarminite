package com.cogsofcarminite.mixin;

import com.cogsofcarminite.util.CCStalactites;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import twilightforest.data.custom.stalactites.entry.Stalactite;
import twilightforest.init.TFBlocks;
import twilightforest.world.components.structures.HollowHillComponent;
import twilightforest.world.components.structures.YetiCaveComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(YetiCaveComponent.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class YetiCaveComponentMixin extends HollowHillComponent {

    @Shadow @Final private static Stalactite BLUE_ICE_SPIKE;

    @Shadow @Final private static Stalactite PACKED_ICE_SPIKE;

    @Shadow @Final private static Stalactite ICE_SPIKE;

    public YetiCaveComponentMixin(StructurePieceSerializationContext ctx, CompoundTag nbt) {
        super(ctx, nbt);
    }

    /**
     * @author jodlodi
     * @reason this is twilight tweaks now
     */
    @Overwrite
    public void postProcess(WorldGenLevel world, StructureManager manager, ChunkGenerator generator, RandomSource rand, BoundingBox sbb, ChunkPos chunkPosIn, BlockPos blockPos) {
        int sn = 64;

        // fill in features

//		// ore or glowing stalactites! (smaller, less plentiful)
//		for (int i = 0; i < sn; i++)
//		{
//			int[] dest = getCoordsInHill2D(rand);
//			generateOreStalactite(world, dest[0], 1, dest[1], sbb);
//		}
        // blue ice stalactites!
        for (int i = 0; i < sn; i++) {
            BlockPos.MutableBlockPos dest = ((HollowHillComponentAccessor)this).getRandomCeilingCoordinates(rand, 24);
            this.generateBlockSpike(world, BLUE_ICE_SPIKE, dest.getX(), dest.getY(), dest.getZ(), sbb, true);
        }
        // packed ice stalactites!
        for (int i = 0; i < sn; i++) {
            BlockPos.MutableBlockPos dest = ((HollowHillComponentAccessor)this).getRandomCeilingCoordinates(rand, 24);
            this.generateBlockSpike(world, PACKED_ICE_SPIKE, dest.getX(), dest.getY(), dest.getZ(), sbb, true);
        }
        // ice stalactites!
        for (int i = 0; i < sn; i++) {
            BlockPos.MutableBlockPos dest = ((HollowHillComponentAccessor)this).getRandomCeilingCoordinates(rand, 24);
            this.generateBlockSpike(world, ICE_SPIKE, dest.getX(), dest.getY(), dest.getZ(), sbb, true);
        }
        // stone stalactites!
        for (int i = 0; i < sn; i++) {
            BlockPos.MutableBlockPos dest = ((HollowHillComponentAccessor)this).getRandomCeilingCoordinates(rand, 24);
            this.generateBlockSpike(world, CCStalactites.getRandomCold(rand), dest.getX(), dest.getY(), dest.getZ(), sbb, true);
        }

        // spawn alpha yeti
        final BlockState yetiSpawner = TFBlocks.ALPHA_YETI_BOSS_SPAWNER.get().defaultBlockState();
        this.setBlockStateRotated(world, yetiSpawner, ((HollowHillComponentAccessor)this).getRadius(), 1, ((HollowHillComponentAccessor)this).getRadius(), Rotation.NONE, sbb);
    }
}
