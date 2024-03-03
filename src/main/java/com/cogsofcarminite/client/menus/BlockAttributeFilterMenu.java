package com.cogsofcarminite.client.menus;

import com.cogsofcarminite.behaviour.BlockFilteringBehaviour;
import com.cogsofcarminite.reg.CCMenus;
import com.cogsofcarminite.util.attributes.BlockAttribute;
import com.simibubi.create.content.logistics.filter.AbstractFilterMenu;
import com.simibubi.create.content.logistics.filter.AttributeFilterMenu;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockAttributeFilterMenu extends AbstractFilterMenu {
    public AttributeFilterMenu.WhitelistMode whitelistMode;
    public List<Pair<BlockAttribute, Boolean>> selectedAttributes;
    public BlockPos clickedPos;
    public BlockState clickedState;
    public Block clickedBlock;
    public boolean firstSet = true;

    public BlockAttributeFilterMenu(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public BlockAttributeFilterMenu(MenuType<?> type, int id, Inventory inv, ItemStack stack) {
        super(type, id, inv, stack);
    }

    public static BlockAttributeFilterMenu create(int id, Inventory inv, ItemStack stack) {
        return new BlockAttributeFilterMenu(CCMenus.BLOCK_ATTRIBUTE_FILTER.get(), id, inv, stack);
    }

    public void appendSelectedAttribute(BlockAttribute blockAttribute, boolean inverted) {
        this.selectedAttributes.add(Pair.of(blockAttribute, inverted));
    }

    @Override
    protected void init(Inventory inv, ItemStack contentHolder) {
        super.init(inv, contentHolder);
        ItemStack stack = new ItemStack(Items.NAME_TAG);
        stack.setHoverName(Components.literal("Selected Tags").withStyle(ChatFormatting.RESET, ChatFormatting.BLUE));
        this.ghostInventory.setStackInSlot(1, stack);
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && this.clickedPos != null) {
            this.setReferencedItem(mc.level.getBlockState(this.clickedPos));
        } else {
            this.firstSet = false;
        }
    }

    public boolean noBlock() {
        return this.clickedBlock == null || this.clickedBlock == Blocks.AIR;
    }

    public void setReferencedItem(BlockState state) {
        this.setReferencedItem(state, state.getBlock(), state.getBlock().asItem().getDefaultInstance());
    }

    public void setReferencedItem(ItemStack stack) {
        this.ghostInventory.setStackInSlot(0, stack);
        this.getSlot(36).setChanged();
    }

    public void setReferencedItem(BlockState state, Block block, ItemStack stack) {
        this.ghostInventory.setStackInSlot(0, stack);
        this.clickedState = state;
        this.clickedBlock = block;
    }
    
    @Override
    protected int getPlayerInventoryXOffset() {
        return 51;
    }

    @Override
    protected int getPlayerInventoryYOffset() {
        return 107;
    }

    @Override
    protected void addFilterSlots() {
        this.addSlot(new BlockSlotHandler(this.ghostInventory, 0, 16, 24, this));
        this.addSlot(new SlotItemHandler(this.ghostInventory, 1, 22, 59) {
            @Override
            public boolean mayPickup(Player playerIn) {
                return false;
            }
        });
    }

    @Override
    protected ItemStackHandler createGhostInventory() {
        return new ItemStackHandler(2);
    }

    @Override
    public void clearContents() {
        this.selectedAttributes.clear();
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
        if (slotId == 37 || (slotId == 36 && !BlockFilteringBehaviour.emptyOrBlock(this.getCarried()))) return;
        super.clicked(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public boolean canDragTo(Slot slotIn) {
        if (slotIn.index == 37 || (slotIn.index == 36 && !BlockFilteringBehaviour.emptyOrBlock(this.getCarried()))) return false;
        return super.canDragTo(slotIn);
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slotIn) {
        if (slotIn.index == 37 || (slotIn.index == 36 && !BlockFilteringBehaviour.emptyOrBlock(this.getCarried()))) return false;
        return super.canTakeItemForPickAll(stack, slotIn);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        if (index == 37 || (index == 36 && !BlockFilteringBehaviour.emptyOrBlock(this.getCarried()))) return ItemStack.EMPTY;
        if (index == 36) {
            this.ghostInventory.setStackInSlot(37, ItemStack.EMPTY);
            return ItemStack.EMPTY;
        }
        if (index < 36) {
            ItemStack stackToInsert = this.playerInventory.getItem(index);
            if (BlockFilteringBehaviour.emptyOrBlock(stackToInsert)) {
                ItemStack copy = stackToInsert.copy();
                copy.setCount(1);
                this.setReferencedItem(copy);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected void initAndReadInventory(ItemStack filterItem) {
        super.initAndReadInventory(filterItem);
        CompoundTag tag = filterItem.getOrCreateTag();
        if (tag.contains("last_clicked")) this.clickedPos = BlockPos.of(tag.getLong("last_clicked"));
        this.selectedAttributes = new ArrayList<>();
        this.whitelistMode = AttributeFilterMenu.WhitelistMode.values()[tag.getInt("WhitelistMode")];
        ListTag attributes = tag.getList("MatchedAttributes", Tag.TAG_COMPOUND);
        attributes.forEach(inbt -> {
            CompoundTag compound = (CompoundTag) inbt;
            this.selectedAttributes.add(Pair.of(BlockAttribute.fromNBT(compound), compound.getBoolean("Inverted")));
        });
    }

    @Override
    protected void saveData(ItemStack filterItem) {
        CompoundTag tag = filterItem.getOrCreateTag();
        tag.putInt("WhitelistMode", this.whitelistMode.ordinal());
        ListTag attributes = new ListTag();
        this.selectedAttributes.forEach(at -> {
            if (at == null)
                return;
            CompoundTag compoundNBT = new CompoundTag();
            at.getFirst()
                    .serializeNBT(compoundNBT);
            compoundNBT.putBoolean("Inverted", at.getSecond());
            attributes.add(compoundNBT);
        });
        tag.put("MatchedAttributes", attributes);

        if (attributes.isEmpty() && this.whitelistMode == AttributeFilterMenu.WhitelistMode.WHITELIST_DISJ) {
            filterItem.setTag(null);
        }

        tag.remove("last_clicked");
    }

    public static class BlockSlotHandler extends SlotItemHandler {
        private final BlockAttributeFilterMenu menu;

        public BlockSlotHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition, BlockAttributeFilterMenu menu) {
            super(itemHandler, index, xPosition, yPosition);
            this.menu = menu;
        }

        @Override
        public void setChanged() {
            super.setChanged();
            if (this.menu.firstSet) this.menu.firstSet = false;
            else {
                if (this.hasItem() && this.getItem().getItem() instanceof BlockItem blockItem) {
                    this.menu.clickedBlock = blockItem.getBlock();
                } else {
                    this.menu.clickedBlock = Blocks.AIR;
                }
                this.menu.clickedState = this.menu.clickedBlock.defaultBlockState();
            }
        }
    }
}