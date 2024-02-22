package com.cogsofcarminite.blocks.entities;

import com.cogsofcarminite.blocks.MechanicalRootPullerBlock;
import com.cogsofcarminite.data.CCTags;
import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MechanicalRootPullerBlockEntity extends BlockBreakingKineticBlockEntity {

    public ItemStackHandler inventory;
    private final LazyOptional<IItemHandler> invProvider;

    public MechanicalRootPullerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        this.inventory = new ItemStackHandler(1) {
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return stack.getItem() instanceof BlockItem blockItem && !blockItem.getBlock().defaultBlockState().is(CCTags.Blocks.ROOTS);
            }
        };
        this.invProvider = LazyOptional.of(() -> this.inventory);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        behaviours.add(new DirectBeltInputBehaviour(this).allowingBeltFunnels());
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.put("Inventory", this.inventory.serializeNBT());
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        this.inventory.deserializeNBT(compound.getCompound("Inventory"));
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(this.worldPosition).inflate(.125f);
    }

    @Override
    public void tick() {
        if (this.shouldRun() && this.ticksUntilNextProgress < 0) this.destroyNextTick();
        super.tick();
        if (this.getSpeed() == 0) return;
        if (this.level != null) this.level.updateNeighbourForOutputSignal(this.worldPosition, this.getBlockState().getBlock());
        this.sendData();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.invProvider.invalidate();
    }

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(this.level, this.worldPosition, this.inventory);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER && side == this.getBlockState().getValue(MechanicalRootPullerBlock.FACING).getOpposite())
            return this.invProvider.cast();
        return super.getCapability(cap, side);
    }

    @Override
    protected BlockPos getBreakingPos() {
        return this.getBlockPos().relative(this.getBlockState().getValue(MechanicalRootPullerBlock.FACING));
    }

    @Override
    public void onBlockBroken(BlockState stateToBreak) {
        if (this.level != null) {
            BlockPos.MutableBlockPos ps = this.breakingPos.mutable();
            for (int i = 0; i < 64; i++) {
                boolean flag = false;
                for (BlockPos pos : BlockPos.betweenClosed(new BlockPos(ps.getX() + 1, ps.getY() - 1, ps.getZ() + 1), new BlockPos(ps.getX() - 1, ps.getY() - 1, ps.getZ() - 1))) {
                    BlockState state = this.level.getBlockState(pos);
                    if (state.is(CCTags.Blocks.ROOTS)) {
                        ps.set(pos);
                        flag = true;
                        break;
                    }
                }
                if (flag) continue;
                BlockHelper.destroyBlock(this.level, ps, 1f, (stack) -> {
                    Vec3 vec = VecHelper.offsetRandomly(Vec3.atBottomCenterOf(this.breakingPos.above()), this.level.random, .125f);
                    if (stack.isEmpty()) return;
                    if (!this.level.getGameRules() .getBoolean(GameRules.RULE_DOBLOCKDROPS)) return;
                    if (this.level.restoringBlockSnapshots) return;

                    ItemEntity itementity = new ItemEntity(this.level, vec.x, vec.y, vec.z, stack);
                    itementity.setDefaultPickUpDelay();
                    itementity.setDeltaMovement(new Vec3(0.0D, 0.25D, 0.0D));
                    this.level.addFreshEntity(itementity);
                });

                ItemStack blockStack = this.inventory.extractItem(0, 1, false);
                if (!blockStack.isEmpty() && blockStack.getItem() instanceof BlockItem blockItem) {
                    this.level.setBlock(ps, blockItem.getBlock().defaultBlockState(), 3);
                }
                return;
            }
        }
    }

    @Override
    public boolean canBreak(BlockState stateToBreak, float blockHardness) {
        return super.canBreak(stateToBreak, blockHardness) && stateToBreak.is(CCTags.Blocks.ROOTS);
    }
}
