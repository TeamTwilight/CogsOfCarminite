package com.cogsofcarminite.blocks;

import com.cogsofcarminite.CogsOfCarminite;
import com.cogsofcarminite.blocks.entities.CarminiteHeartBlockEntity;
import com.cogsofcarminite.reg.CCBlockEntities;
import com.cogsofcarminite.reg.CCPartialBlockModels;
import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import twilightforest.TFConfig;
import twilightforest.init.TFBiomes;
import twilightforest.init.TFSounds;
import twilightforest.util.WorldUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MechanicalHeartOfTransformation extends CarminiteMagicLogBlock implements IBE<CarminiteHeartBlockEntity> {
    private static final ResourceLocation TEXTURE_LOCATION = CogsOfCarminite.prefix("textures/block/transformation_log_core_on.png");
    private static final RenderType RENDER_TYPE = RenderType.armorCutoutNoCull(TEXTURE_LOCATION);

    public static final byte NOTE_OFFSET = 4;
    public static final byte MIN_NOTE = 5;
    public static final byte MAX_NOTE = 20;

    public static byte lastNote = 12;

    public MechanicalHeartOfTransformation(Properties properties) {
        super(properties);
    }

    @Override
    public Class<CarminiteHeartBlockEntity> getBlockEntityClass() {
        return CarminiteHeartBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CarminiteHeartBlockEntity> getBlockEntityType() {
        return CCBlockEntities.CARMINITE_HEART.get();
    }

    @Override
    public boolean doesCoreFunction() {
        return !(Boolean) TFConfig.COMMON_CONFIG.MAGIC_TREES.disableTransformation.get();
    }

    @Override
    public void performTreeEffect(ServerLevel level, BlockPos pos, RandomSource rand, CompoundTag filter) {
        ResourceKey<Biome> target = TFBiomes.ENCHANTED_FOREST;
        Holder<Biome> biome = level.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(target);
        int range = TFConfig.COMMON_CONFIG.MAGIC_TREES.transformationRange.get();
        for (int i = 0; i < 16; i++) {
            BlockPos dPos = WorldUtil.randomOffset(rand, pos, range, 0, range);
            if (dPos.distSqr(pos) > 256.0)
                continue;

            if (level.getBiome(dPos).is(target))
                continue;

            int minY = QuartPos.fromBlock(level.getMinBuildHeight());
            int maxY = minY + QuartPos.fromBlock(level.getHeight()) - 1;

            int x = QuartPos.fromBlock(dPos.getX());
            int z = QuartPos.fromBlock(dPos.getZ());

            LevelChunk chunkAt = level.getChunk(dPos.getX() >> 4, dPos.getZ() >> 4);
            for (LevelChunkSection section : chunkAt.getSections()) {
                for (int sy = 0; sy < 16; sy += 4) {
                    int y = Mth.clamp(QuartPos.fromBlock(chunkAt.getMinSection() + sy), minY, maxY);
                    if (section.getBiomes().get(x & 3, y & 3, z & 3).is(target))
                        continue;
                    if (section.getBiomes() instanceof PalettedContainer<Holder<Biome>> container)
                        container.set(x & 3, y & 3, z & 3, biome);
                }
            }

            if (!chunkAt.isUnsaved()) chunkAt.setUnsaved(true);
            level.getChunkSource().chunkMap.resendBiomesForChunks(List.of(chunkAt));
            break;
        }
    }

    @Override
    public void playSound(Level level, BlockPos pos, RandomSource rand) {
        level.playSound(null, pos, TFSounds.TRANSFORMATION_CORE.get(), SoundSource.BLOCKS, 0.1F, NoteBlock.getPitchFromNote(nextNote(rand)) * 0.75F);
    }

    public static int nextNote(RandomSource rand) {
        if (lastNote <= MIN_NOTE) return lastNote = (byte) (lastNote + rand.nextInt(NOTE_OFFSET));
        else if (lastNote >= MAX_NOTE) return lastNote = (byte) (lastNote - rand.nextInt(NOTE_OFFSET));
        else return lastNote = (byte) (lastNote + rand.nextInt(NOTE_OFFSET) - rand.nextInt(NOTE_OFFSET));
    }

    @Override
    public PartialModel getFlywheelModel() {
        return CCPartialBlockModels.HEART_FLYWHEEL;
    }

    @Override
    public RenderType getRenderType() {
        return RENDER_TYPE;
    }
}
