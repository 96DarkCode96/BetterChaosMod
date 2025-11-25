package eu.darkcode.networking.configuration;

import eu.darkcode.networking.Payloads;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ServerboundHandshakeAck(@NotNull String clientModVersion) implements CustomPacketPayload {

    public static final StreamCodec<FriendlyByteBuf, ServerboundHandshakeAck> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ServerboundHandshakeAck::clientModVersion,
            ServerboundHandshakeAck::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return Payloads.C2S_HANDSHAKE_ACK;
    }
}
