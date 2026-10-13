package eu.darkcode.networking;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * @author _dark_code_
 * @date 13.10.2026
 **/
public record ClientboundSetLockedSlotsPayload(int lockedSlots) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSetLockedSlotsPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientboundSetLockedSlotsPayload::lockedSlots,
            ClientboundSetLockedSlotsPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return Payloads.S2C_SET_LOCKED_SLOTS;
    }
}
