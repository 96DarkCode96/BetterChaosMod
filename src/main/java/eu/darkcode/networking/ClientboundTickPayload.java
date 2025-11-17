package eu.darkcode.networking;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ClientboundTickPayload(int timerCount) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundTickPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClientboundTickPayload::timerCount,
            ClientboundTickPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return Payloads.S2C_TICK;
    }
}
