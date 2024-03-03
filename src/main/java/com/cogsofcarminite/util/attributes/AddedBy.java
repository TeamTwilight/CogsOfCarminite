package com.cogsofcarminite.util.attributes;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AddedBy implements BlockAttribute {
    private final String modId;

    public AddedBy(String modId) {
        this.modId = modId;
    }

    @Override
    public boolean appliesTo(BlockState stack) {
        ResourceLocation registryName = ForgeRegistries.BLOCKS.getKey(stack.getBlock());
        String modId = registryName == null ? null : registryName.getNamespace();
        return this.modId.equals(modId);
    }

    @Override
    @Nullable
    public List<BlockAttribute> listAttributesOf(BlockState stack) {
        ResourceLocation registryName = ForgeRegistries.BLOCKS.getKey(stack.getBlock());
        String modId = registryName == null ? null : registryName.getNamespace();
        return modId == null ? Collections.emptyList() : List.of(new com.cogsofcarminite.util.attributes.AddedBy(modId));
    }

    @Override
    public String getTranslationKey() {
        return "added_by";
    }

    @Override
    public Object[] getTranslationParameters() {
        Optional<? extends ModContainer> modContainerById = ModList.get().getModContainerById(this.modId);
        String name = modContainerById.map(ModContainer::getModInfo)
                .map(IModInfo::getDisplayName)
                .orElse(StringUtils.capitalize(this.modId));
        return new Object[]{name};
    }

    @Override
    public void writeNBT(CompoundTag nbt) {
        nbt.putString("id", this.modId);
    }

    @Override
    @Nullable
    public BlockAttribute readNBT(CompoundTag nbt) {
        return new com.cogsofcarminite.util.attributes.AddedBy(nbt.getString("id"));
    }
}
