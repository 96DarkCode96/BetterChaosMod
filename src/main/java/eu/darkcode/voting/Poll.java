package eu.darkcode.voting;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Poll(int pollId, @NotNull List<Component> entries) {

    public static final StreamCodec<RegistryFriendlyByteBuf, Poll> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, Poll::pollId,
            ComponentSerialization.STREAM_CODEC.apply(ByteBufCodecs.list()), Poll::entries,
            Poll::new
    );

}