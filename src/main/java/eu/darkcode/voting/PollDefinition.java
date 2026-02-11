package eu.darkcode.voting;

import eu.darkcode.utils.Utils;
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

    @NotNull
    public String toMessage() {
        // Format: [1] Entry1 | [2] Entry2 | ... or 5, 6, 7, 8 based on poll id
        StringBuilder message = new StringBuilder("New poll started! ");
        for (int i = 0; i < entries.size(); i++) {
            message.append("[").append(Utils.getVoteOptionIndex(pollId, i)).append("] ").append(entries.get(i).getString());
            if (i < entries.size() - 1)
                message.append(" | ");
        }
        return message.toString();
    }

}