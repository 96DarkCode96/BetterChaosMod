package eu.darkcode.voting;

import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record PollDefinition(int pollId, @NotNull List<Component> entries, @NotNull TriState visibility) {

    public static final @NotNull StreamCodec<RegistryFriendlyByteBuf, PollDefinition> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, PollDefinition::pollId,
            ComponentSerialization.STREAM_CODEC.apply(ByteBufCodecs.list()), PollDefinition::entries,
            new StreamCodec<>() {
                @Override
                public @NotNull TriState decode(RegistryFriendlyByteBuf buf) {
                    return buf.readEnum(TriState.class);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, TriState obj) {
                    buf.writeEnum(obj);
                }
            }, PollDefinition::visibility,
            PollDefinition::new
    );

}