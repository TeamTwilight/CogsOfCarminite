package com.cogsofcarminite.blocks.entities;

import com.cogsofcarminite.CogsOfCarminite;
import com.cogsofcarminite.reg.CCPartialBlockModels;
import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
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
import java.util.List;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CarminiteCoreBlockEntity extends CarminiteMagicLogBlockEntity {
    private static final ResourceLocation TEXTURE_LOCATION = CogsOfCarminite.prefix("textures/block/mining_log_core_on.png");
    private static final RenderType RENDER_TYPE = RenderType.armorCutoutNoCull(TEXTURE_LOCATION);

    private FilteringBehaviour filtering;

    public static Field REFLECTED_ORE_TO_BLOCK_REPLACEMENTS = null;
    private static boolean REFLECTED_CACHE_NEEDS_BUILD = true;

    public CarminiteCoreBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.filtering = new OreFilteringBehaviour(this, new MagicLogSlot());
        behaviours.add(this.filtering);
    }

    @Override
    public PartialModel getFlywheelModel() {
        return CCPartialBlockModels.CORE_FLYWHEEL;
    }

    @Override
    public RenderType getRenderType() {
        return RENDER_TYPE;
    }

    @Override
    public boolean doesCoreFunction() {
        return !(Boolean) TFConfig.COMMON_CONFIG.MAGIC_TREES.disableMining.get();
    }

    @Override
    protected void performTreeEffect(ServerLevel level, BlockPos usePos, RandomSource rand) {
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
            } else if (foundPos == null && searchState.getBlock() != Blocks.AIR && this.isOre(searchState.getBlock()) && level.getBlockEntity(coord) == null) {
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

    private boolean isOre(Block ore) {
        if (!this.filtering.test(ore.asItem().getDefaultInstance())) return false;
        HashMap<Block, Block> map = getReplacements();
        if (map != null) return map.containsKey(ore);
        return false;
    }

    @SuppressWarnings("unchecked")
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

    public static class OreFilteringBehaviour extends FilteringBehaviour {
        public OreFilteringBehaviour(SmartBlockEntity be, ValueBoxTransform slot) {
            super(be, slot);
        }

        @Override
        public boolean setFilter(ItemStack stack) {
            if (stack.isEmpty() || stack.getItem() instanceof FilterItem) return super.setFilter(stack);
            if (stack.getItem() instanceof BlockItem blockItem) {
                BlockState state = blockItem.getBlock().defaultBlockState();
                if (state.is(Tags.Blocks.ORES) && !state.is(BlockTagGenerator.ORE_MAGNET_IGNORE)) return super.setFilter(stack);
            }
            return false;
        }
    }
}
