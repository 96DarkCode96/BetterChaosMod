package eu.darkcode.networking;

import eu.darkcode.Settings;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ClientboundHandshakeConfirmPayload(@NotNull Settings settings) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundHandshakeConfirmPayload> CODEC = StreamCodec.composite(
            Settings.CODEC, ClientboundHandshakeConfirmPayload::settings,
            ClientboundHandshakeConfirmPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return Payloads.S2C_HANDSHAKE_CONFIRM;
    }
}
