package com.cogsofcarminite.util.attributes;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.extensions.IForgeBlockState;
import net.minecraftforge.common.util.TriPredicate;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public enum StandardTraits implements BlockAttribute {
    DUMMY(state -> false),
    REPLACEABLE(state -> state.canBeReplaced()),
    BLOCK_ENTITY(BlockState::hasBlockEntity),
    ANALOG_OUTPUT_SIGNAL(BlockBehaviour.BlockStateBase::hasAnalogOutputSignal),
    IGNITED_BY_LAVA(BlockBehaviour.BlockStateBase::ignitedByLava),
    OCCLUDE(BlockBehaviour.BlockStateBase::canOcclude),
    LARGE_COLLISION_SHAPE(BlockBehaviour.BlockStateBase::hasLargeCollisionShape),
    OFFSET_FUNCTION(BlockBehaviour.BlockStateBase::hasOffsetFunction),
    RANDOMLY_TICKING(BlockBehaviour.BlockStateBase::isRandomlyTicking),
    SIGNAL_SOURCE(BlockBehaviour.BlockStateBase::isSignalSource),
    REQUIRES_CORRECT_TOOL_FOR_DROPS(BlockBehaviour.BlockStateBase::requiresCorrectToolForDrops),
    STICKY_BLOCK(IForgeBlockState::isStickyBlock),
    WATERLOGGED(state -> state.hasProperty(BlockStateProperties.WATERLOGGED)),
    INFINIBURN((state, level) -> state.is(level.dimensionType().infiniburn())),
    BURNING(IForgeBlockState::isBurning),
    PROPAGATES_SKYLIGHT_DOWN(BlockBehaviour.BlockStateBase::propagatesSkylightDown),
    RAINED_UPON((state, level, pos) -> level.isRainingAt(pos) || level.isRainingAt(pos.above())),
    SEE_SKY((state, level, pos) -> level.canSeeSky(pos) || level.canSeeSky(pos.above()));

    private Predicate<BlockState> test;
    private BiPredicate<BlockState, Level> testWithWorld;
    private TriPredicate<BlockState, Level, BlockPos> testWithWorldAndPos;

    StandardTraits(Predicate<BlockState> test) {
        this.test = test;
    }

    StandardTraits(BiPredicate<BlockState, Level> test) {
        this.testWithWorld = test;
    }

    StandardTraits(TriPredicate<BlockState, Level, BlockPos> test) {
        this.testWithWorldAndPos = test;
    }

    @Override
    public boolean appliesTo(BlockState state, @Nullable Level world, @Nullable BlockPos pos) {
        if (this.testWithWorldAndPos != null && world != null && pos != null) return this.testWithWorldAndPos.test(state, world, pos);
        return this.appliesTo(state);
    }

    @Override
    public List<BlockAttribute> listAttributesOf(BlockState state, @Nullable Level world, BlockPos pos) {
        List<BlockAttribute> attributes = new ArrayList<>();
        for (StandardTraits trait : values()) if (trait.appliesTo(state, world, pos)) attributes.add(trait);
        return attributes;
    }

    @Override
    public boolean appliesTo(BlockState state, @Nullable Level world) {
        if (this.testWithWorld != null && world != null) return this.testWithWorld.test(state, world);
        return this.appliesTo(state);
    }

    @Override
    public List<BlockAttribute> listAttributesOf(BlockState state, @Nullable Level world) {
        List<BlockAttribute> attributes = new ArrayList<>();
        for (StandardTraits trait : values()) if (trait.appliesTo(state, world)) attributes.add(trait);
        return attributes;
    }

    @Override
    public boolean appliesTo(BlockState state) {
        return this.test != null && this.test.test(state);
    }

    @Override
    @Nullable
    public List<BlockAttribute> listAttributesOf(BlockState state) {
        return null;
    }

    @Override
    public String getTranslationKey() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public String getNBTKey() {
        return "standard_trait";
    }

    @Override
    public void writeNBT(CompoundTag nbt) {
        nbt.putBoolean(name(), true);
    }

    @Override
    @Nullable
    public BlockAttribute readNBT(CompoundTag nbt) {
        for (com.cogsofcarminite.util.attributes.StandardTraits trait : values())
            if (nbt.contains(trait.name()))
                return trait;
        return null;
    }
}
