package com.cogsofcarminite.blocks;

import com.cogsofcarminite.CogsOfCarminite;
import com.cogsofcarminite.blocks.entities.CarminiteClockBlockEntity;
import com.cogsofcarminite.blocks.entities.CarminiteMagicLogBlockEntity;
import com.cogsofcarminite.reg.CCBlockEntities;
import com.cogsofcarminite.reg.CCPartialBlockModels;
import com.cogsofcarminite.util.BlockFilterItemStack;
import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import twilightforest.TFConfig;
import twilightforest.data.tags.BlockTagGenerator;
import twilightforest.init.TFSounds;
import twilightforest.util.WorldUtil;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MechanicalTimewoodClock extends CarminiteMagicLogBlock implements IBE<CarminiteClockBlockEntity> {
    private static final ResourceLocation TEXTURE_LOCATION = CogsOfCarminite.prefix("textures/block/time_log_core_on.png");
    private static final RenderType RENDER_TYPE = RenderType.armorCutoutNoCull(TEXTURE_LOCATION);

    public MechanicalTimewoodClock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<CarminiteClockBlockEntity> getBlockEntityClass() {
        return CarminiteClockBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CarminiteClockBlockEntity> getBlockEntityType() {
        return CCBlockEntities.CARMINITE_CLOCK.get();
    }

    @Override
    public boolean doesCoreFunction() {
        return !(Boolean) TFConfig.COMMON_CONFIG.MAGIC_TREES.disableTime.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    // Vanilla also makes this dirty cast on block entity tickers, poor mojank design.
    public void performTreeEffect(ServerLevel level, BlockPos pos, RandomSource rand, CompoundTag filter) {
        FilterItemStack filterStack = BlockFilterItemStack.of(filter);
        int numticks = 8 * 3 * 20;

        for (int i = 0; i < numticks; i++) {
            BlockPos dPos = WorldUtil.randomOffset(rand, pos, TFConfig.COMMON_CONFIG.MAGIC_TREES.timeRange.get());
            BlockState state = level.getBlockState(dPos);

            if (!state.is(BlockTagGenerator.TIME_CORE_EXCLUDED) && this.test(filterStack, level, state, dPos)) {
                boolean flag = false;

                if (state.isRandomlyTicking()) {
                    state.randomTick(level, dPos, rand);
                    flag = true;
                }

                BlockEntity entity = level.getBlockEntity(dPos);
                if (entity != null) {
                    BlockEntityTicker<BlockEntity> ticker = state.getTicker(level, (BlockEntityType<BlockEntity>) entity.getType());
                    if (ticker != null) {
                        ticker.tick(level, dPos, state, entity);
                        flag = true;
                    }
                }

                if (flag) spawnParticles(level, dPos);
            }
        }
    }

    private boolean test(FilterItemStack filter, ServerLevel level, BlockState state, BlockPos pos) {
        if (filter instanceof BlockFilterItemStack blockFilterItemStack) {
            return blockFilterItemStack.test(level, state, pos);
        }
        return filter.test(level, state.getBlock().asItem().getDefaultInstance());
    }

    @Override
    public void playSound(Level level, BlockPos pos, RandomSource rand) {
        level.playSound(null, pos, TFSounds.TIME_CORE.get(), SoundSource.BLOCKS, 0.35F, 0.5F);
    }

    @Override
    public CompoundTag getFilter(CarminiteMagicLogBlockEntity blockEntity) {
        if (blockEntity instanceof CarminiteClockBlockEntity entity) {
            return entity.filtering.getFilter().save(new CompoundTag());
        }
        return super.getFilter(blockEntity);
    }

    @Override
    public PartialModel getFlywheelModel() {
        return CCPartialBlockModels.CLOCK_FLYWHEEL;
    }

    @Override
    public RenderType getRenderType() {
        return RENDER_TYPE;
    }
}
