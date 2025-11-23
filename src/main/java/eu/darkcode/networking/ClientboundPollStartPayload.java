package eu.darkcode.networking;

import eu.darkcode.voting.Poll;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ClientboundPollStartPayload(@NotNull Poll poll) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPollStartPayload> CODEC = StreamCodec.composite(
            Poll.CODEC, ClientboundPollStartPayload::poll,
            ClientboundPollStartPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return Payloads.S2C_POLL_START;
    }
}
