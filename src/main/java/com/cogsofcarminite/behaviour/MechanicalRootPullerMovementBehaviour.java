package com.cogsofcarminite.behaviour;

import com.cogsofcarminite.CCUtil;
import com.cogsofcarminite.blocks.MechanicalRootPullerBlock;
import com.cogsofcarminite.client.renderers.blocks.MechanicalRootPullerRenderer;
import com.cogsofcarminite.data.CCTags;
import com.cogsofcarminite.util.BlockFilterItemStack;
import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.kinetics.base.BlockBreakingMovementBehaviour;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MechanicalRootPullerMovementBehaviour extends BlockBreakingMovementBehaviour {

    @Override
    public boolean isActive(MovementContext context) {
        return super.isActive(context) && !VecHelper.isVecPointingTowards(context.relativeMotion, context.state.getValue(MechanicalRootPullerBlock.FACING).getOpposite());
    }

    @Override
    public Vec3 getActiveAreaOffset(MovementContext context) {
        return Vec3.atLowerCornerOf(context.state.getValue(MechanicalRootPullerBlock.FACING).getNormal()).scale(.65f);
    }

    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        super.visitNewPosition(context, pos);
        Vec3 facingVec = Vec3.atLowerCornerOf(context.state.getValue(MechanicalRootPullerBlock.FACING).getNormal());
        facingVec = context.rotation.apply(facingVec);

        Direction closestToFacing = Direction.getNearest(facingVec.x, facingVec.y, facingVec.z);
        if (closestToFacing.getAxis().isVertical() && context.data.contains("BreakingPos")) {
            context.data.remove("BreakingPos");
            context.stall = false;
        }
    }

    @Override
    public boolean canBreak(Level world, BlockPos breakingPos, BlockState state) {
        return super.canBreak(world, breakingPos, state) && state.is(CCTags.Blocks.ROOTS);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void destroyBlock(MovementContext context, BlockPos breakingPos) {
        if (context.world != null) {
            BlockPos.MutableBlockPos ps = breakingPos.mutable();
            for (int i = 0; i < 64; i++) {
                boolean flag = false;
                for (BlockPos pos : BlockPos.betweenClosed(new BlockPos(ps.getX() + 1, ps.getY() - 1, ps.getZ() + 1), new BlockPos(ps.getX() - 1, ps.getY() - 1, ps.getZ() - 1))) {
                    BlockState state = context.world.getBlockState(pos);
                    if (state.is(CCTags.Blocks.ROOTS)) {
                        ps.set(pos);
                        flag = true;
                        break;
                    }
                }
                if (flag) continue;

                IItemHandlerModifiable inventory = context.contraption.getSharedInventory();

                BlockHelper.destroyBlock(context.world, ps, 1f, (stack) -> {
                    ItemStack remainder;
                    if (AllConfigs.server().kinetics.moveItemsToStorage.get()) remainder = ItemHandlerHelper.insertItem(inventory, stack, false);
                    else remainder = stack;
                    if (remainder.isEmpty()) return;

                    // Actors might void items if their positions is undefined
                    Vec3 vec = context.position;
                    if (vec == null) return;

                    ItemEntity itemEntity = new ItemEntity(context.world, vec.x, vec.y + 0.6D, vec.z, remainder);
                    itemEntity.setDeltaMovement(context.motion.add(0, 0.5f, 0).scale(context.world.random.nextFloat() * .3f));
                    context.world.addFreshEntity(itemEntity);
                });

                for (int j = 0; j < inventory.getSlots(); j++) {
                    ItemStack stack = inventory.extractItem(j, 1, true);
                    BlockItem blockItem = BlockFilteringBehaviour.unfiltered(stack).orElse(null);
                    if (blockItem != null) {
                        Block block = blockItem.getBlock();
                        BlockState itemState = block.defaultBlockState();
                        if (block.canSurvive(itemState, context.world, ps) && this.getFilterFromBE(context).test(context.world, stack)) {
                            inventory.extractItem(j, 1, false);
                            context.world.setBlock(ps, itemState, 3);
                            CompoundTag data = context.data;
                            data.putInt("WaitingTicks", 10);
                            data.put("LastPos", NbtUtils.writeBlockPos(ps));
                            context.stall = true;
                            break;
                        }
                    }
                }

                return;
            }
        }
    }

    public FilterItemStack getFilterFromBE(MovementContext context) {
        FilterItemStack filter = CCUtil.reflectAndGet(CCUtil.FILTER_IN_BE, context);
        if (filter != null) return filter;
        filter = BlockFilterItemStack.od(context.blockEntityData.getCompound("Filter"));
        CCUtil.reflectAndSet(CCUtil.FILTER_IN_BE, context, filter);
        return filter;
    }

    @Override
    protected void onBlockBroken(MovementContext context, BlockPos pos, BlockState brokenState) {
        if (this.canBreak(context.world, pos,context.world.getBlockState(pos)) || brokenState.getBlock() instanceof FallingBlock) {
            CompoundTag data = context.data;
            data.putInt("WaitingTicks", 10);
            data.put("LastPos", NbtUtils.writeBlockPos(pos));
            context.stall = true;
        }
    }

    @Override
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        MechanicalRootPullerRenderer.renderInContraption(context, renderWorld, matrices, buffer);
    }
}
