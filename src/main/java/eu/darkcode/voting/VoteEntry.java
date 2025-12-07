package eu.darkcode.voting;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record VoteEntry(long uid, @NotNull String voterName, int voteOption, int voteWeight) {
    public static final @NotNull StreamCodec<RegistryFriendlyByteBuf, VoteEntry> CODEC = StreamCodec.composite(
            ByteBufCodecs.LONG, VoteEntry::uid,
            ByteBufCodecs.STRING_UTF8, VoteEntry::voterName,
            ByteBufCodecs.INT, VoteEntry::voteOption,
            ByteBufCodecs.INT, VoteEntry::voteWeight,
            VoteEntry::new
    );
}
