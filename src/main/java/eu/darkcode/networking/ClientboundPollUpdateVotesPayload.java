package eu.darkcode.networking;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ClientboundPollUpdateVotesPayload(int pollId, int totalVotes,
                                                int[] votes) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPollUpdateVotesPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClientboundPollUpdateVotesPayload::pollId,
            ByteBufCodecs.INT, ClientboundPollUpdateVotesPayload::totalVotes,
            new StreamCodec<>() {
                @Override
                public int @NotNull [] decode(RegistryFriendlyByteBuf object) {
                    int length = object.readInt();
                    int[] array = new int[length];
                    for (int i = 0; i < length; i++)
                        array[i] = object.readInt();
                    return array;
                }

                @Override
                public void encode(RegistryFriendlyByteBuf object, int[] object2) {
                    object.writeInt(object2.length);
                    for (int value : object2)
                        object.writeInt(value);
                }
            }, ClientboundPollUpdateVotesPayload::votes,
            ClientboundPollUpdateVotesPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return Payloads.S2C_POLL_UPDATE;
    }
}
