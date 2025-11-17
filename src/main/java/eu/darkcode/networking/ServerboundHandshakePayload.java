package eu.darkcode.networking;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ServerboundHandshakePayload(String version) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundHandshakePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.stringUtf8(32767), ServerboundHandshakePayload::version,
            ServerboundHandshakePayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return Payloads.C2S_HANDSHAKE;
    }
}
