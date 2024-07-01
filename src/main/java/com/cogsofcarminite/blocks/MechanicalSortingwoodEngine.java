package com.cogsofcarminite.blocks;

import com.cogsofcarminite.blocks.entities.CarminiteEngineBlockEntity;
import com.cogsofcarminite.blocks.entities.CarminiteMagicLogBlockEntity;
import com.cogsofcarminite.reg.CCBlockEntities;
import com.cogsofcarminite.reg.CCPartialBlockModels;
import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.PacketDistributor;
import twilightforest.TFConfig;
import twilightforest.data.tags.EntityTagGenerator;
import twilightforest.init.TFParticleType;
import twilightforest.network.ParticlePacket;
import twilightforest.network.TFPacketHandler;
import twilightforest.util.WorldUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MechanicalSortingwoodEngine extends CarminiteMagicLogBlock implements IBE<CarminiteEngineBlockEntity> {

    public MechanicalSortingwoodEngine(Properties properties) {
        super(properties);
    }

    @Override
    public Class<CarminiteEngineBlockEntity> getBlockEntityClass() {
        return CarminiteEngineBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CarminiteEngineBlockEntity> getBlockEntityType() {
        return CCBlockEntities.CARMINITE_ENGINE.get();
    }

    @Override
    public boolean doesCoreFunction() {
        return !(Boolean) TFConfig.COMMON_CONFIG.MAGIC_TREES.disableSorting.get();
    }

    @Override
    public void performTreeEffect(ServerLevel level, BlockPos pos, RandomSource rand, CompoundTag filter) {
        FilterItemStack filterStack = FilterItemStack.of(filter);
        Map<List<IItemHandler>, Vec3> inputMap = new HashMap<>();
        Map<IItemHandler, Vec3> outputMap = new HashMap<>();

        for (BlockPos blockPos : WorldUtil.getAllAround(pos, TFConfig.COMMON_CONFIG.MAGIC_TREES.sortingRange.get())) { // Get every itemHandler from every block in the area
            if (!blockPos.equals(pos)) {
                BlockEntity blockEntity = level.getBlockEntity(blockPos);
                if (blockEntity != null) {
                    // Put it in the input if its within 2 blocks
                    if (Math.abs(blockPos.getX() - pos.getX()) <= 2 && Math.abs(blockPos.getY() - pos.getY()) <= 2 && Math.abs(blockPos.getZ() - pos.getZ()) <= 2) {
                        List<IItemHandler> handlers = new ArrayList<>();
                        for (Direction side : Direction.values()) {
                            blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, side).ifPresent(handlers::add);
                        }
                        if (!handlers.isEmpty()) {
                            inputMap.put(handlers, Vec3.upFromBottomCenterOf(blockPos, 1.9D));
                        }
                    } else { // Output if its outside that range
                        for (Direction side : Direction.values()) {
                            blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, side).ifPresent(iItemHandler -> outputMap.put(iItemHandler, Vec3.upFromBottomCenterOf(blockPos, 1.9D)));
                        }
                    }
                }
            }
        }

        List<Entity> alreadyUsedForInput = new ArrayList<>(); // Keep track of entities we already have for inputs, so we can skip over them when looking for outputs

        level.getEntities((Entity) null, new AABB(pos).inflate(2), entity -> entity.isAlive() && entity.getType().is(EntityTagGenerator.SORTABLE_ENTITIES)).forEach(entity -> {
            List<IItemHandler> handlers = new ArrayList<>();
            for (Direction side : Direction.values()) {
                entity.getCapability(ForgeCapabilities.ITEM_HANDLER, side).ifPresent(handlers::add);
            }
            if (!handlers.isEmpty()) {
                inputMap.put(handlers, entity.position().add(0D, entity.getBbHeight() + 0.9D, 0D));
                alreadyUsedForInput.add(entity);
            }
        });

        if (inputMap.isEmpty()) return; // No input

        level.getEntities((Entity) null, new AABB(pos).inflate(16), entity -> entity.isAlive() && !alreadyUsedForInput.contains(entity) && entity.getType().is(EntityTagGenerator.SORTABLE_ENTITIES)).forEach(entity -> {
            for (Direction side : Direction.values()) {
                entity.getCapability(ForgeCapabilities.ITEM_HANDLER, side).ifPresent(iItemHandler -> outputMap.put(iItemHandler, entity.position().add(0D, entity.getBbHeight() + 0.9D, 0D)));
            }
        });

        if (outputMap.isEmpty()) return; // No output

        for (Map.Entry<List<IItemHandler>, Vec3> inputHandlers : inputMap.entrySet()) {
            boolean transferred = false;
            for (IItemHandler inputIItemHandler : inputHandlers.getKey()) {
                for (int i = 0; i < inputIItemHandler.getSlots(); i++) {
                    ItemStack inputStack = inputIItemHandler.extractItem(i, 1, true);
                    if (!inputStack.isEmpty() && filterStack.test(level, inputStack)) {
                        Map<Integer, IItemHandler> outputsByCount = new HashMap<>();

                        for (IItemHandler outputIItemHandler : outputMap.keySet()) {
                            int count = 0;
                            for (int j = 0; j < outputIItemHandler.getSlots(); j++) {
                                ItemStack stack = outputIItemHandler.getStackInSlot(j);
                                if (stack.is(inputStack.getItem())) count += stack.getCount();
                            }
                            if (count > 0) outputsByCount.put(count, outputIItemHandler);
                        }

                        for (Integer count : outputsByCount.keySet().stream().sorted(Comparator.comparingInt(Integer::intValue).reversed()).toList()) {
                            IItemHandler outputIItemHandler = outputsByCount.get(count);
                            int firstProperStack = -1;
                            for (int j = 0; j < outputIItemHandler.getSlots(); j++) {
                                if (outputIItemHandler.isItemValid(j, inputStack)) {
                                    ItemStack outputStack = outputIItemHandler.getStackInSlot(j);

                                    if (firstProperStack == -1 && outputStack.isEmpty()) {
                                        firstProperStack = j; //We reference the index of the first empty slot, in case there is no stacks that aren't at max size
                                    } else if (ItemStack.isSameItemSameTags(inputStack, outputStack)
                                            && outputStack.getCount() < outputStack.getMaxStackSize()
                                            && outputStack.getCount() < outputIItemHandler.getSlotLimit(j)) {
                                        firstProperStack = j;
                                        break;
                                    }
                                }
                            }
                            if (firstProperStack != -1) { // If there weren't any non-full stacks, we transfer to an empty space instead
                                ItemStack newStack = inputIItemHandler.extractItem(i, 1, false);
                                if (!newStack.isEmpty() && outputIItemHandler.insertItem(firstProperStack, newStack, true).isEmpty()) {
                                    outputIItemHandler.insertItem(firstProperStack, newStack, false);
                                    transferred = true;

                                    Vec3 xyz = outputMap.get(outputIItemHandler);
                                    Vec3 diff = inputHandlers.getValue().subtract(xyz);

                                    for (ServerPlayer serverplayer : level.players()) { // This is just particle math, we send a particle packet to every player in range
                                        if (serverplayer.distanceToSqr(xyz) < 4096.0D) {
                                            ParticlePacket particlePacket = new ParticlePacket();
                                            double x = diff.x - 0.25D + rand.nextDouble() * 0.5D;
                                            double y = diff.y - 1.75D + rand.nextDouble() * 0.5D;
                                            double z = diff.z - 0.25D + rand.nextDouble() * 0.5D;
                                            particlePacket.queueParticle(TFParticleType.SORTING_PARTICLE.get(), false, xyz, new Vec3(x, y, z).scale(1D / diff.length()));
                                            TFPacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverplayer), particlePacket);
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    if (transferred) break;// If we transferred the item from this Entry already, we break, since all IItemHandlers in one entry come from the same source
                }
                if (transferred) break; // Again, since we only transfer once per source, break
            }
        }
    }

    @Override
    public CompoundTag getFilter(CarminiteMagicLogBlockEntity blockEntity) {
        if (blockEntity instanceof CarminiteEngineBlockEntity entity) {
            return entity.filtering.getFilter().save(new CompoundTag());
        }
        return super.getFilter(blockEntity);
    }

    @Override
    public PartialModel getFlywheelModel() {
        return CCPartialBlockModels.SORTING_OFF;
    }

    @Override
    public PartialModel getFlywheelOverlay() {
        return CCPartialBlockModels.SORTING_OVERLAY;
    }
}
