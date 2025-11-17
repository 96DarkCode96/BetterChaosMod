package eu.darkcode.networking;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ClientboundEventStartPayload(Event event) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundEventStartPayload> CODEC = StreamCodec.composite(
            EventRegistry.CODEC, ClientboundEventStartPayload::event,
            ClientboundEventStartPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return Payloads.S2C_EVENT_START;
    }
}
