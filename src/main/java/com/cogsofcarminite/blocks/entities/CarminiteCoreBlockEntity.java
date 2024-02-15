package com.cogsofcarminite.blocks.entities;

import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import twilightforest.data.tags.BlockTagGenerator;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CarminiteCoreBlockEntity extends CarminiteMagicLogBlockEntity {

    public FilteringBehaviour filtering;

    public CarminiteCoreBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.filtering = new OreFilteringBehaviour(this, new MagicLogSlot());
        behaviours.add(this.filtering);
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
