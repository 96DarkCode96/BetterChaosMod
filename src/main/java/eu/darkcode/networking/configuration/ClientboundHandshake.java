package eu.darkcode.networking.configuration;

import eu.darkcode.Settings;
import eu.darkcode.networking.Payloads;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ClientboundHandshake(@NotNull Settings serverSettings) implements CustomPacketPayload {

    public static final StreamCodec<FriendlyByteBuf, ClientboundHandshake> CODEC = StreamCodec.composite(
            Settings.CODEC, ClientboundHandshake::serverSettings,
            ClientboundHandshake::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return Payloads.S2C_HANDSHAKE;
    }
}
