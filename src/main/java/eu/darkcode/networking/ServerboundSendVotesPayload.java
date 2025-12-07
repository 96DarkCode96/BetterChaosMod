package eu.darkcode.networking;

import eu.darkcode.voting.VoteEntry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ServerboundSendVotesPayload(int pollId, @NotNull List<VoteEntry> votes) implements CustomPacketPayload {

    public static final @NotNull StreamCodec<RegistryFriendlyByteBuf, ServerboundSendVotesPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ServerboundSendVotesPayload::pollId,
            ByteBufCodecs.<RegistryFriendlyByteBuf, VoteEntry>list().apply(VoteEntry.CODEC), ServerboundSendVotesPayload::votes,
            ServerboundSendVotesPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return Payloads.C2S_SEND_VOTES;
    }
}
