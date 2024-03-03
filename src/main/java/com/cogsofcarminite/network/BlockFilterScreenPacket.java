package com.cogsofcarminite.network;

import com.cogsofcarminite.client.menus.BlockAttributeFilterMenu;
import com.cogsofcarminite.util.attributes.BlockAttribute;
import com.simibubi.create.content.logistics.filter.AttributeFilterMenu;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockFilterScreenPacket extends SimplePacketBase {

    public enum Option {
        WHITELIST, WHITELIST2, BLACKLIST, RESPECT_DATA, IGNORE_DATA, UPDATE_FILTER_ITEM, ADD_TAG, ADD_INVERTED_TAG;
    }

    private final BlockFilterScreenPacket.Option option;
    private final CompoundTag data;

    public BlockFilterScreenPacket(BlockFilterScreenPacket.Option option) {
        this(option, new CompoundTag());
    }

    public BlockFilterScreenPacket(BlockFilterScreenPacket.Option option, CompoundTag data) {
        this.option = option;
        this.data = data;
    }

    public BlockFilterScreenPacket(FriendlyByteBuf buffer) {
        this.option = BlockFilterScreenPacket.Option.values()[buffer.readInt()];
        this.data = buffer.readNbt();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(this.option.ordinal());
        buffer.writeNbt(this.data);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            if (player.containerMenu instanceof BlockAttributeFilterMenu c) {
                if (this.option == BlockFilterScreenPacket.Option.WHITELIST)
                    c.whitelistMode = AttributeFilterMenu.WhitelistMode.WHITELIST_DISJ;
                if (this.option == BlockFilterScreenPacket.Option.WHITELIST2)
                    c.whitelistMode = AttributeFilterMenu.WhitelistMode.WHITELIST_CONJ;
                if (this.option == BlockFilterScreenPacket.Option.BLACKLIST)
                    c.whitelistMode = AttributeFilterMenu.WhitelistMode.BLACKLIST;
                if (this.option == BlockFilterScreenPacket.Option.ADD_TAG) {
                    BlockAttribute attribute = BlockAttribute.fromNBT(this.data);
                    if (attribute != null) c.appendSelectedAttribute(attribute, false);
                }
                if (this.option == BlockFilterScreenPacket.Option.ADD_INVERTED_TAG) {
                    BlockAttribute attribute = BlockAttribute.fromNBT(this.data);
                    if (attribute != null) c.appendSelectedAttribute(attribute, true);
                }
            }

        });
        return true;
    }

}
