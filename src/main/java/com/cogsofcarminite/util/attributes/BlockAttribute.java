package com.cogsofcarminite.util.attributes;

import com.cogsofcarminite.data.CCLangGenerator;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface BlockAttribute {
    List<BlockAttribute> types = new ArrayList<>();

    BlockAttribute standard = register(StandardTraits.DUMMY);
    BlockAttribute inTag = register(new InTag(BlockTags.LOGS));
    BlockAttribute addedBy = register(new AddedBy("dummy"));

    static BlockAttribute register(BlockAttribute attributeType) {
        types.add(attributeType);
        return attributeType;
    }

    @Nullable
    static BlockAttribute fromNBT(CompoundTag nbt) {
        for (BlockAttribute BlockAttribute : types)
            if (BlockAttribute.canRead(nbt))
                return BlockAttribute.readNBT(nbt.getCompound(BlockAttribute.getNBTKey()));
        return null;
    }

    default boolean appliesTo(BlockState state, Level world, BlockPos pos) {
        return this.appliesTo(state);
    }

    default boolean appliesTo(BlockState state, Level world) {
        return this.appliesTo(state);
    }

    boolean appliesTo(BlockState state);

    default List<BlockAttribute> listAttributesOf(BlockState state, @Nullable Level world, BlockPos pos) {
        return this.listAttributesOf(state, world);
    }

    default List<BlockAttribute> listAttributesOf(BlockState state, @Nullable Level world) {
        List<BlockAttribute> list = this.listAttributesOf(state);
        return list == null ? List.of() : list;
    }

    @Nullable
    List<BlockAttribute> listAttributesOf(BlockState state);

    String getTranslationKey();

    void writeNBT(CompoundTag nbt);

    @Nullable
    BlockAttribute readNBT(CompoundTag nbt);

    default void serializeNBT(CompoundTag nbt) {
        CompoundTag compound = new CompoundTag();
        writeNBT(compound);
        nbt.put(getNBTKey(), compound);
    }

    default Object[] getTranslationParameters() {
        return new String[0];
    }

    default boolean canRead(CompoundTag nbt) {
        return nbt.contains(getNBTKey());
    }

    default String getNBTKey() {
        return getTranslationKey();
    }

    @OnlyIn(value = Dist.CLIENT)
    default MutableComponent format(boolean inverted) {
        if (this instanceof AddedBy || this instanceof InTag) {
            return Lang.translateDirect("item_attributes." + getTranslationKey() + (inverted ? ".inverted" : ""),
                    getTranslationParameters());
        }

        return CCLangGenerator.translate("block_attributes." + getTranslationKey() + (inverted ? ".inverted" : ""),
                getTranslationParameters()).component();
    }

}
