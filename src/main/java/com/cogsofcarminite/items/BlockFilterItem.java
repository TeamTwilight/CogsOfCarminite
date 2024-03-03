package com.cogsofcarminite.items;

import com.cogsofcarminite.client.menus.BlockAttributeFilterMenu;
import com.cogsofcarminite.util.attributes.BlockAttribute;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllKeys;
import com.simibubi.create.content.logistics.filter.AttributeFilterMenu;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockFilterItem extends Item implements MenuProvider {

    public BlockFilterItem(Properties properties) {
        super(properties);
    }

    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        InteractionHand hand = context.getHand();

        if (!player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
            ItemStack heldItem = context.getItemInHand();
            if (!context.getLevel().isClientSide && player instanceof ServerPlayer serverPlayer) {
                CompoundTag tag = heldItem.getOrCreateTag();
                tag.putLong("last_clicked", context.getClickedPos().asLong());
                NetworkHooks.openScreen(serverPlayer, this, buf -> buf.writeItem(heldItem));
            }

            return InteractionResult.SUCCESS;
        }

        return use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);

        if (!player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
            if (!world.isClientSide && player instanceof ServerPlayer)
                NetworkHooks.openScreen((ServerPlayer) player, this, buf -> buf.writeItem(heldItem));
            return InteractionResultHolder.success(heldItem);
        }
        return InteractionResultHolder.pass(heldItem);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (!AllKeys.shiftDown()) {
            List<Component> makeSummary = makeSummary(stack);
            if (makeSummary.isEmpty()) return;
            tooltip.add(Components.literal(" "));
            tooltip.addAll(makeSummary);
        }
    }

    protected List<Component> makeSummary(ItemStack filter) {
        List<Component> list = new ArrayList<>();
        if (!filter.hasTag()) return list;

        AttributeFilterMenu.WhitelistMode whitelistMode = AttributeFilterMenu.WhitelistMode.values()[filter.getOrCreateTag()
                .getInt("WhitelistMode")];
        list.add((whitelistMode == AttributeFilterMenu.WhitelistMode.WHITELIST_CONJ
                ? Lang.translateDirect("gui.attribute_filter.allow_list_conjunctive")
                : whitelistMode == AttributeFilterMenu.WhitelistMode.WHITELIST_DISJ
                ? Lang.translateDirect("gui.attribute_filter.allow_list_disjunctive")
                : Lang.translateDirect("gui.attribute_filter.deny_list")).withStyle(ChatFormatting.GOLD));

        int count = 0;
        ListTag attributes = filter.getOrCreateTag()
                .getList("MatchedAttributes", Tag.TAG_COMPOUND);
        for (Tag inbt : attributes) {
            CompoundTag compound = (CompoundTag) inbt;
            BlockAttribute attribute = BlockAttribute.fromNBT(compound);
            if (attribute == null)
                continue;
            boolean inverted = compound.getBoolean("Inverted");
            if (count > 3) {
                list.add(Components.literal("- ...")
                        .withStyle(ChatFormatting.DARK_GRAY));
                break;
            }
            list.add(Components.literal("- ")
                    .append(attribute.format(inverted)));
            count++;
        }

        if (count == 0)
            return Collections.emptyList();

        return list;
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return BlockAttributeFilterMenu.create(id, inv, player.getMainHandItem());
    }

    @Override
    public Component getDisplayName() {
        return this.getDescription();
    }

    public static ItemStackHandler getFilterItems(ItemStack stack) {
        ItemStackHandler newInv = new ItemStackHandler(18);
        if (AllItems.FILTER.get() != stack.getItem())
            throw new IllegalArgumentException("Cannot get filter items from non-filter: " + stack);
        if (!stack.hasTag())
            return newInv;
        CompoundTag invNBT = stack.getOrCreateTagElement("Items");
        if (!invNBT.isEmpty())
            newInv.deserializeNBT(invNBT);
        return newInv;
    }

    public static boolean testDirect(ItemStack filter, ItemStack stack, boolean matchNBT) {
        if (matchNBT) {
            return ItemHandlerHelper.canItemStacksStack(filter, stack);
        } else {
            return ItemHelper.sameItem(filter, stack);
        }
    }

}
