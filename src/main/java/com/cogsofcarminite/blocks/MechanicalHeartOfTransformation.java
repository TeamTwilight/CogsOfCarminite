package com.cogsofcarminite.blocks;

import com.cogsofcarminite.CogsOfCarminite;
import com.cogsofcarminite.blocks.entities.CarminiteHeartBlockEntity;
import com.cogsofcarminite.blocks.entities.CarminiteMagicLogBlockEntity;
import com.cogsofcarminite.reg.CCBlockEntities;
import com.cogsofcarminite.reg.CCPartialBlockModels;
import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
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
        switch (filter.getInt("ScrollValue")) {
            case 0:
                transform(level, pos, rand, filter);
                break;
            case 1:
                adapt(level, pos, filter);
                break;
            default: revert(level, pos.south(), rand);
        }
    }

    protected void transform(ServerLevel level, BlockPos pos, RandomSource rand, CompoundTag filter) {
        ResourceLocation biomeID = filter.contains("BiomeID") ? ResourceLocation.tryParse(filter.getString("BiomeID")) : null;
        Registry<Biome> reg = level.registryAccess().registryOrThrow(Registries.BIOME);
        Holder<Biome> biomeHolder;

        if (filter.contains("SaveToBlock") && level.getBlockEntity(pos) instanceof CarminiteHeartBlockEntity entity && entity.storedBiome != null) {
            biomeHolder = entity.storedBiome;
            if (biomeID == null) {
                biomeID = TFBiomes.ENCHANTED_FOREST.location();
                biomeHolder = reg.getHolderOrThrow(TFBiomes.ENCHANTED_FOREST);
            }
        } else {
            if (biomeID == null) {
                biomeID = TFBiomes.ENCHANTED_FOREST.location();
                biomeHolder = reg.getHolderOrThrow(TFBiomes.ENCHANTED_FOREST);
            } else {
                Biome biome = reg.get(biomeID);
                if (biome == null) biomeHolder = reg.getHolderOrThrow(TFBiomes.ENCHANTED_FOREST);
                else biomeHolder = reg.wrapAsHolder(biome);
            }
        }

        int range = TFConfig.COMMON_CONFIG.MAGIC_TREES.transformationRange.get();
        for (int i = 0; i < 16; i++) {
            BlockPos dPos = WorldUtil.randomOffset(rand, pos, range, range, range);
            if (dPos.distSqr(pos) > 256.0) continue;
            if (level.getBiome(dPos).is(biomeID)) continue;

            int x = QuartPos.fromBlock(dPos.getX());
            int y = QuartPos.fromBlock(dPos.getY());
            int z = QuartPos.fromBlock(dPos.getZ());

            LevelChunk chunkAt = level.getChunk(dPos.getX() >> 4, dPos.getZ() >> 4);
            LevelChunkSection section = chunkAt.getSection(chunkAt.getSectionIndex(dPos.getY()));

            if (section.getBiomes().get(x & 3, y & 3, z & 3).equals(biomeHolder)) continue;

            if (section.getBiomes() instanceof PalettedContainer<Holder<Biome>> container) {
                container.set(x & 3, y & 3, z & 3, biomeHolder);
                spawnParticles(level, dPos);
            }

            if (!chunkAt.isUnsaved()) chunkAt.setUnsaved(true);
            level.getChunkSource().chunkMap.resendBiomesForChunks(List.of(chunkAt));
        }
    }

    protected void adapt(ServerLevel level, BlockPos pos, CompoundTag filter) {
        if (filter.contains("SaveToBlock") && level.getBlockEntity(pos) instanceof CarminiteHeartBlockEntity entity) {
            entity.storedBiome = level.getBiome(pos);
        } else {
            ResourceLocation location = level.registryAccess().registryOrThrow(Registries.BIOME).getKey(level.getBiome(pos).get());
            filter.putString("BiomeID", location != null ? location.toString() : TFBiomes.ENCHANTED_FOREST.location().toString());
        }
    }

    protected void revert(ServerLevel level, BlockPos pos, RandomSource rand) {
        int range = TFConfig.COMMON_CONFIG.MAGIC_TREES.transformationRange.get();
        for (int i = 0; i < 16; i++) {
            BlockPos dPos = WorldUtil.randomOffset(rand, pos, range, range, range);
            if (dPos.distSqr(pos) > 256.0) continue;
            int x = QuartPos.fromBlock(dPos.getX());
            int y = QuartPos.fromBlock(dPos.getY());
            int z = QuartPos.fromBlock(dPos.getZ());
            Holder<Biome> uncachedBiome = level.getUncachedNoiseBiome(x, y, z);

            if (level.getBiome(dPos).equals(uncachedBiome)) continue;

            LevelChunk chunkAt = level.getChunk(dPos.getX() >> 4, dPos.getZ() >> 4);
            LevelChunkSection section = chunkAt.getSection(chunkAt.getSectionIndex(dPos.getY()));

            if (section.getBiomes().get(x & 3, y & 3, z & 3).equals(uncachedBiome)) continue;

            if (section.getBiomes() instanceof PalettedContainer<Holder<Biome>> container) {
                container.set(x & 3, y & 3, z & 3, uncachedBiome);
                spawnParticles(level, dPos);
            }

            if (!chunkAt.isUnsaved()) chunkAt.setUnsaved(true);
            level.getChunkSource().chunkMap.resendBiomesForChunks(List.of(chunkAt));
        }
    }

    @Override
    public CompoundTag getFilter(CarminiteMagicLogBlockEntity blockEntity) {
        if (blockEntity instanceof CarminiteHeartBlockEntity entity) {
            CompoundTag tag = super.getFilter(entity);
            entity.heartMode.write(tag, false);
            tag.putBoolean("SaveToBlock", true);

            if (entity.storedBiome != null) {
                Level level = entity.getLevel();
                if (level != null) {
                    ResourceLocation location = level.registryAccess().registryOrThrow(Registries.BIOME).getKey(entity.storedBiome.value());
                    tag.putString("BiomeID", location != null ? location.toString() : TFBiomes.ENCHANTED_FOREST.location().toString());
                }
            } else tag.putString("BiomeID", TFBiomes.ENCHANTED_FOREST.location().toString());

            return tag;
        }
        return super.getFilter(blockEntity);
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
        return CCPartialBlockModels.TRANS_OFF;
    }

    @Override
    public PartialModel getFlywheelOverlay() {
        return CCPartialBlockModels.TRANS_OVERLAY;
    }

    @Override
    public RenderType getRenderType() {
        return RENDER_TYPE;
    }
}
