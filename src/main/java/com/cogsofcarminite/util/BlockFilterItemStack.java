package com.cogsofcarminite.util;

import com.cogsofcarminite.reg.CCItems;
import com.cogsofcarminite.util.attributes.BlockAttribute;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockFilterItemStack extends FilterItemStack {

    public FilterItemStack.AttributeFilterItemStack.WhitelistMode whitelistMode;
    public List<Pair<BlockAttribute, Boolean>> attributeTests;

    public BlockFilterItemStack(ItemStack filter) {
        super(filter);
        boolean defaults = !filter.hasTag();

        this.attributeTests = new ArrayList<>();
        this.whitelistMode = FilterItemStack.AttributeFilterItemStack.WhitelistMode.values()[defaults ? 0
                : Objects.requireNonNull(filter.getTag())
                .getInt("WhitelistMode")];

        ListTag attributes = defaults ? new ListTag()
                : filter.getTag()
                .getList("MatchedAttributes", Tag.TAG_COMPOUND);

        for (Tag tag : attributes) {
            CompoundTag compound = (CompoundTag) tag;
            BlockAttribute attribute = BlockAttribute.fromNBT(compound);
            if (attribute != null) this.attributeTests.add(Pair.of(attribute, compound.getBoolean("Inverted")));
        }
    }
    
    public static FilterItemStack od(ItemStack filter) {
        if (filter.is(CCItems.BLOCK_ATTRIBUTE_FILTER.asItem())) return new BlockFilterItemStack(filter);
        return of(filter);
    }
    
    public static FilterItemStack od(CompoundTag tag) {
        return od(ItemStack.of(tag));
    }

    @Override
    public boolean test(Level world, FluidStack stack, boolean matchNBT) {
        return false;
    }

    @Override
    public boolean test(Level world, ItemStack stack, boolean matchNBT) {
        if (stack.getItem() instanceof BlockItem blockItem) return test(world, blockItem.getBlock().defaultBlockState());
        return super.test(world, stack, matchNBT);
    }

    public boolean test(Level world, BlockState state) {
        return test(world, state, null);
    }

    public boolean test(Level world, BlockState state, @Nullable BlockPos pos) {
        if (!this.attributeTests.isEmpty()) {
            for (Pair<BlockAttribute, Boolean> test : this.attributeTests) {
                BlockAttribute attribute = test.getFirst();
                boolean inverted = test.getSecond();
                boolean matches = pos != null ? (attribute.appliesTo(state, world, pos) != inverted) : (attribute.appliesTo(state, world) != inverted);

                if (matches) {
                    switch (whitelistMode) {
                        case BLACKLIST:
                            return false;
                        case WHITELIST_CONJ:
                            continue;
                        case WHITELIST_DISJ:
                            return true;
                    }
                } else {
                    switch (whitelistMode) {
                        case BLACKLIST, WHITELIST_DISJ:
                            continue;
                        case WHITELIST_CONJ:
                            return false;
                    }
                }
            }
        } else return false;

        return switch (whitelistMode) {
            case BLACKLIST, WHITELIST_CONJ -> true;
            case WHITELIST_DISJ -> false;
        };
    }

    @Override
    public boolean isFilterItem() {
        return super.isFilterItem() || this.item().is(CCItems.BLOCK_ATTRIBUTE_FILTER.asItem());
    }
}
