package com.cogsofcarminite.reg;

import com.cogsofcarminite.CogsOfCarminite;
import com.cogsofcarminite.network.BlockFilterScreenPacket;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public enum CCPackets {

    // Client to Server
    CONFIGURE_FILTER(BlockFilterScreenPacket.class, BlockFilterScreenPacket::new, PLAY_TO_SERVER);

    public static final ResourceLocation CHANNEL_NAME = CogsOfCarminite.prefix("main");
    public static final int NETWORK_VERSION = 3;
    public static final String NETWORK_VERSION_STR = String.valueOf(NETWORK_VERSION);
    private static SimpleChannel channel;

    private final CCPackets.PacketType<?> packetType;

    <T extends SimplePacketBase> CCPackets(Class<T> type, Function<FriendlyByteBuf, T> factory, NetworkDirection direction) {
        this.packetType = new CCPackets.PacketType<>(type, factory, direction);
    }

    public static void register() {
        channel = NetworkRegistry.ChannelBuilder.named(CHANNEL_NAME)
                .serverAcceptedVersions(NETWORK_VERSION_STR::equals)
                .clientAcceptedVersions(NETWORK_VERSION_STR::equals)
                .networkProtocolVersion(() -> NETWORK_VERSION_STR)
                .simpleChannel();

        for (CCPackets packet : values())
            packet.packetType.register();
    }

    public static SimpleChannel getChannel() {
        return channel;
    }

    public static void sendToNear(Level world, BlockPos pos, int range, Object message) {
        getChannel().send(
                PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(pos.getX(), pos.getY(), pos.getZ(), range, world.dimension())),
                message);
    }

    private static class PacketType<T extends SimplePacketBase> {
        private static int index = 0;

        private final BiConsumer<T, FriendlyByteBuf> encoder;
        private final Function<FriendlyByteBuf, T> decoder;
        private final BiConsumer<T, Supplier<NetworkEvent.Context>> handler;
        private final Class<T> type;
        private final NetworkDirection direction;

        private PacketType(Class<T> type, Function<FriendlyByteBuf, T> factory, NetworkDirection direction) {
            this.encoder = T::write;
            this.decoder = factory;
            this.handler = (packet, contextSupplier) -> {
                NetworkEvent.Context context = contextSupplier.get();
                if (packet.handle(context)) {
                    context.setPacketHandled(true);
                }
            };
            this.type = type;
            this.direction = direction;
        }

        private void register() {
            getChannel().messageBuilder(this.type, index++, this.direction)
                    .encoder(this.encoder)
                    .decoder(this.decoder)
                    .consumerNetworkThread(this.handler)
                    .add();
        }
    }
}
