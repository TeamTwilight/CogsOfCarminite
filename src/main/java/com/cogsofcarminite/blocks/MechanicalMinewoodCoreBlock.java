package com.cogsofcarminite.blocks;

import com.cogsofcarminite.CogsOfCarminite;
import com.cogsofcarminite.blocks.entities.CarminiteCoreBlockEntity;
import com.cogsofcarminite.blocks.entities.CarminiteMagicLogBlockEntity;
import com.cogsofcarminite.mixin.OreMagnetItemAccessor;
import com.cogsofcarminite.reg.CCBlockEntities;
import com.cogsofcarminite.reg.CCPartialBlockModels;
import com.cogsofcarminite.util.BlockFilterItemStack;
import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.common.Mod;
import twilightforest.TFConfig;
import twilightforest.data.tags.BlockTagGenerator;
import twilightforest.init.TFItems;
import twilightforest.init.TFSounds;
import twilightforest.util.VoxelBresenhamIterator;
import twilightforest.util.WorldUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Mod.EventBusSubscriber(modid = CogsOfCarminite.MODID)
public class MechanicalMinewoodCoreBlock extends CarminiteMagicLogBlock implements IBE<CarminiteCoreBlockEntity> {

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
    public void performTreeEffect(ServerLevel level, BlockPos usePos, RandomSource rand, CompoundTag filter) {
        FilterItemStack filterStack = BlockFilterItemStack.of(filter);

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
            } else if (foundPos == null && searchState.getBlock() != Blocks.AIR && this.test(filterStack, level, searchState, coord) && level.getBlockEntity(coord) == null) {
                attactedOreBlock = searchState;
                replacementBlock = getReplacements().getOrDefault(attactedOreBlock.getBlock(), Blocks.STONE).defaultBlockState();
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

    private boolean test(FilterItemStack filter, ServerLevel level, BlockState state, BlockPos pos) {
        boolean flag;
        if (filter instanceof BlockFilterItemStack blockFilterItemStack) flag = blockFilterItemStack.test(level, state, pos);
        else flag = filter.test(level, state.getBlock().asItem().getDefaultInstance());
        if (!flag) return false;
        return getReplacements().containsKey(state.getBlock());
    }

    public static HashMap<Block, Block> getReplacements() {
        return ((OreMagnetItemAccessor) TFItems.ORE_MAGNET.get()).getOreToBlockReplacements();
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
        return CCPartialBlockModels.MINE_OFF;
    }

    @Override
    public PartialModel getFlywheelOverlay() {
        return CCPartialBlockModels.MINE_OVERLAY;
    }
}
