package com.cogsofcarminite.blocks;

import com.cogsofcarminite.CogsOfCarminite;
import com.cogsofcarminite.blocks.entities.CarminiteCoreBlockEntity;
import com.cogsofcarminite.blocks.entities.CarminiteMagicLogBlockEntity;
import com.cogsofcarminite.reg.CCBlockEntities;
import com.cogsofcarminite.reg.CCPartialBlockModels;
import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import twilightforest.TFConfig;
import twilightforest.data.tags.BlockTagGenerator;
import twilightforest.init.TFSounds;
import twilightforest.item.OreMagnetItem;
import twilightforest.util.VoxelBresenhamIterator;
import twilightforest.util.WorldUtil;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MechanicalMinewoodCoreBlock extends CarminiteMagicLogBlock implements IBE<CarminiteCoreBlockEntity> {
    private static final ResourceLocation TEXTURE_LOCATION = CogsOfCarminite.prefix("textures/block/mining_log_core_on.png");
    private static final RenderType RENDER_TYPE = RenderType.armorCutoutNoCull(TEXTURE_LOCATION);

    public static Field REFLECTED_ORE_TO_BLOCK_REPLACEMENTS = null;
    private static boolean REFLECTED_CACHE_NEEDS_BUILD = true;

    public MechanicalMinewoodCoreBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<CarminiteCoreBlockEntity> getBlockEntityClass() {
        return CarminiteCoreBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CarminiteCoreBlockEntity> getBlockEntityType() {
        return CCBlockEntities.CARMINITE_CORE.get();
    }

    @Override
    public boolean doesCoreFunction() {
        return !(Boolean) TFConfig.COMMON_CONFIG.MAGIC_TREES.disableMining.get();
    }

    @Override
    @SuppressWarnings({"CallToPrintStackTrace", "ConfusingArgumentToVarargsMethod"})
    public void performTreeEffect(ServerLevel level, BlockPos usePos, RandomSource rand, CompoundTag filter) {
        FilterItemStack filterStack = FilterItemStack.of(filter);
        if (REFLECTED_CACHE_NEEDS_BUILD) {
            try {
                Method method = OreMagnetItem.class.getDeclaredMethod("initOre2BlockMap", null);
                method.invoke(null, null);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
            REFLECTED_CACHE_NEEDS_BUILD = false;
        }

        int blocksMoved = 0;

        // find some ore?
        BlockState attactedOreBlock = Blocks.AIR.defaultBlockState();
        BlockState replacementBlock = Blocks.AIR.defaultBlockState();
        BlockPos foundPos = null;
        BlockPos basePos = null;

        BlockPos destPos = WorldUtil.randomOffset(rand, usePos, TFConfig.COMMON_CONFIG.MAGIC_TREES.miningRange.get());
        for (BlockPos coord : new VoxelBresenhamIterator(usePos, destPos)) {
            BlockState searchState = level.getBlockState(coord);

            // keep track of where the dirt/stone we first find is.
            if (basePos == null) {
                if (isReplaceable(searchState)) {
                    basePos = coord;
                }
                // This ordering is so that the base pos is found first before we pull ores - pushing ores away is a baaaaad idea!
            } else if (foundPos == null && searchState.getBlock() != Blocks.AIR && this.isOre(level, filterStack, searchState.getBlock()) && level.getBlockEntity(coord) == null) {
                attactedOreBlock = searchState;
                HashMap<Block, Block> map = getReplacements();
                if (map != null) {
                    replacementBlock = map.getOrDefault(attactedOreBlock.getBlock(), Blocks.STONE).defaultBlockState();
                }
                foundPos = coord;
            }
        }

        if (basePos != null && foundPos != null && attactedOreBlock.getBlock() != Blocks.AIR) {
            // find the whole vein
            Set<BlockPos> veinBlocks = new HashSet<>();
            findVein(level, foundPos, attactedOreBlock, veinBlocks);

            // move it up into minable blocks or dirt
            int offX = basePos.getX() - foundPos.getX();
            int offY = basePos.getY() - foundPos.getY();
            int offZ = basePos.getZ() - foundPos.getZ();

            for (BlockPos coord : veinBlocks) {
                BlockPos replacePos = coord.offset(offX, offY, offZ);
                BlockState replaceState = level.getBlockState(replacePos);

                if (isReplaceable(replaceState) || replaceState.canBeReplaced() || replaceState.isAir()) {
                    level.setBlock(coord, replacementBlock, 2);
                    spawnParticles(level, coord);
                    level.setBlock(replacePos, attactedOreBlock, 2);
                    blocksMoved++;
                }
            }
        }

        if (blocksMoved > 0) {
            level.playSound(null, usePos, TFSounds.MAGNET_GRAB.get(), SoundSource.BLOCKS, 0.1F, 1.0F);
        }
    }

    private static void findVein(Level level, BlockPos here, BlockState oreState, Set<BlockPos> veinBlocks) {
        if (!veinBlocks.contains(here) && veinBlocks.size() < 24 && level.getBlockState(here) == oreState) {
            veinBlocks.add(here);
            for (Direction direction : Direction.values()) {
                findVein(level, here.relative(direction), oreState, veinBlocks);
            }
        }
    }

    private boolean isOre(Level level, FilterItemStack filter, Block ore) {
        if (! filter.test(level, ore.asItem().getDefaultInstance())) return false;
        HashMap<Block, Block> map = getReplacements();
        if (map != null) return map.containsKey(ore);
        return false;
    }

    @SuppressWarnings({"unchecked", "CallToPrintStackTrace"})
    public static @Nullable HashMap<Block, Block> getReplacements() {
        if (REFLECTED_ORE_TO_BLOCK_REPLACEMENTS == null) {
            try {
                REFLECTED_ORE_TO_BLOCK_REPLACEMENTS = OreMagnetItem.class.getDeclaredField("ORE_TO_BLOCK_REPLACEMENTS");
                REFLECTED_ORE_TO_BLOCK_REPLACEMENTS.setAccessible(true);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                return null;
            }
        }

        try {
            return (HashMap<Block, Block>)REFLECTED_ORE_TO_BLOCK_REPLACEMENTS.get(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static boolean isReplaceable(BlockState state) {
        return state.is(BlockTagGenerator.ORE_MAGNET_SAFE_REPLACE_BLOCK);
    }

    @Override
    public CompoundTag getFilter(CarminiteMagicLogBlockEntity blockEntity) {
        if (blockEntity instanceof CarminiteCoreBlockEntity entity) {
            return entity.filtering.getFilter().save(new CompoundTag());
        }
        return super.getFilter(blockEntity);
    }

    @Override
    public PartialModel getFlywheelModel() {
        return CCPartialBlockModels.CORE_FLYWHEEL;
    }

    @Override
    public RenderType getRenderType() {
        return RENDER_TYPE;
    }
}
