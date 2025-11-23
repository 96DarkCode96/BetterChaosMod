package eu.darkcode.networking;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ClientboundPollUpdateVotesPayload(int pollId) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPollUpdateVotesPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClientboundPollUpdateVotesPayload::pollId,
            ClientboundPollUpdateVotesPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return Payloads.S2C_POLL_UPDATE;
    }
}
