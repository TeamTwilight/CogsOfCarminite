package com.cogsofcarminite.items;

import com.cogsofcarminite.reg.CCBlockEntities;
import com.cogsofcarminite.reg.CCPartialBlockModels;
import com.jozufozu.flywheel.core.PartialModel;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import twilightforest.init.TFBiomes;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class HeartOfTransformationBlockItem extends CarminiteMagicLogBlockItem {
    public HeartOfTransformationBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, level, list, flag);
        list.add(Component.translatable("cogsofcarminite.tooltip.mechanical_heart.set_to").withStyle(ChatFormatting.GOLD));
        CompoundTag tag = BlockItem.getBlockEntityData(stack);
        String[] id = tag != null && tag.contains("BiomeID") ? tag.getString("BiomeID").split(":") : TFBiomes.ENCHANTED_FOREST.location().toString().split(":");
        list.add(Component.translatable("biome." + id[0] + "." + id[1]).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
        ResourceLocation location = level.registryAccess().registryOrThrow(Registries.BIOME).getKey(level.getBiome(living.blockPosition()).get());
        if (location != null) {
            CompoundTag tag = new CompoundTag();
            tag.putString("BiomeID", location.toString());
            BlockItem.setBlockEntityData(stack, CCBlockEntities.CARMINITE_HEART.get(), tag);
        }
        return stack;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int useRemaining) {
        super.releaseUsing(stack, level, entity, useRemaining);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 30;
    }

    @Override
    public PartialModel getPartialModel() {
        return CCPartialBlockModels.TRANS_OFF;
    }
}
