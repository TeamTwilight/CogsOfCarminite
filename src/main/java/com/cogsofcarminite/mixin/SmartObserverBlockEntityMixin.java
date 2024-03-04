package com.cogsofcarminite.mixin;

import com.cogsofcarminite.behaviour.BlockOrItemFilteringBehaviour;
import com.cogsofcarminite.util.BlockFilterItemStack;
import com.simibubi.create.content.redstone.smartObserver.SmartObserverBlock;
import com.simibubi.create.content.redstone.smartObserver.SmartObserverBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryTrackerBehaviour;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Mixin(SmartObserverBlockEntity.class)
public abstract class SmartObserverBlockEntityMixin extends SmartBlockEntity {
    @Shadow private FilteringBehaviour filtering;
    @Shadow private VersionedInventoryTrackerBehaviour invVersionTracker;

    public SmartObserverBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "addBehaviours", at = @At(value = "TAIL"), remap = false)
    private void addBlockFiltering(List<BlockEntityBehaviour> behaviours, CallbackInfo ci) {
        for (BlockEntityBehaviour behaviour : behaviours) {
            if (behaviour instanceof FilteringBehaviour filteringBehaviour) {
                behaviours.remove(behaviour);
                behaviours.add(this.filtering = new BlockOrItemFilteringBehaviour(this, filteringBehaviour.getSlotPositioning())
                        .withCallback($ -> this.invVersionTracker.reset()));
                return;
            }
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/blockEntity/behaviour/filtering/FilteringBehaviour;test(Lnet/minecraft/world/item/ItemStack;)Z"), remap = false)
    private boolean testForBlockFiltering(FilteringBehaviour instance, ItemStack stack) {
        Level level = instance.blockEntity.getLevel();
        if (level != null && instance instanceof BlockOrItemFilteringBehaviour behaviour && behaviour.getFilterStack() instanceof BlockFilterItemStack blockFilterItemStack) {
            BlockPos targetPos = instance.blockEntity.getBlockPos().relative(SmartObserverBlock.getTargetDirection(instance.blockEntity.getBlockState()));
            return blockFilterItemStack.test(level, level.getBlockState(targetPos), targetPos);
        }
        return instance.test(stack);
    }
}
