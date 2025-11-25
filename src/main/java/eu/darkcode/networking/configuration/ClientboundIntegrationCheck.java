package eu.darkcode.networking.configuration;

import eu.darkcode.networking.Payloads;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public final class ClientboundIntegrationCheck implements CustomPacketPayload {

    public static final @NotNull ClientboundIntegrationCheck INSTANCE = new ClientboundIntegrationCheck();

    public static final StreamCodec<FriendlyByteBuf, ClientboundIntegrationCheck> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private ClientboundIntegrationCheck() {
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return Payloads.S2C_INTEGRATION_CHECK;
    }

}
