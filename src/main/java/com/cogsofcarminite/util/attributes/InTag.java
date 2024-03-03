package com.cogsofcarminite.util.attributes;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class InTag implements BlockAttribute {
    public TagKey<Block> tag;

    public InTag(TagKey<Block> tag) {
        this.tag = tag;
    }

    @Override
    public boolean appliesTo(BlockState state) {
        return state.is(this.tag);
    }

    @Override
    @Nullable
    public List<BlockAttribute> listAttributesOf(BlockState stack) {
        return stack.getTags()
                .map(com.cogsofcarminite.util.attributes.InTag::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getTranslationKey() {
        return "in_tag";
    }

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{"#" + this.tag.location()};
    }

    @Override
    public void writeNBT(CompoundTag nbt) {
        nbt.putString("space", this.tag.location().getNamespace());
        nbt.putString("path", this.tag.location().getPath());
    }

    @Override
    @Nullable
    public BlockAttribute readNBT(CompoundTag nbt) {
        return new com.cogsofcarminite.util.attributes.InTag(BlockTags.create(new ResourceLocation(nbt.getString("space"), nbt.getString("path"))));
    }
}
