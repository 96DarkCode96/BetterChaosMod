package eu.darkcode.networking;

import eu.darkcode.networking.handshake.ClientboundHandshakeConfirmPayload;
import eu.darkcode.networking.handshake.ServerboundHandshakePayload;
import eu.darkcode.utils.Utils;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public final class Payloads {

    public static final CustomPacketPayload.Type<ServerboundHandshakePayload> C2S_HANDSHAKE = regC2S("handshake", ServerboundHandshakePayload.CODEC);
    public static final CustomPacketPayload.Type<ClientboundTickPayload> S2C_TICK = regS2C("tick", ClientboundTickPayload.CODEC);
    public static final CustomPacketPayload.Type<ClientboundHandshakeConfirmPayload> S2C_HANDSHAKE_CONFIRM = regS2C("handshake_confirm", ClientboundHandshakeConfirmPayload.CODEC);
    public static final CustomPacketPayload.Type<ClientboundEventStartPayload> S2C_EVENT_START = regS2C("event_start", ClientboundEventStartPayload.CODEC);
    public static final CustomPacketPayload.Type<ClientboundPollStartPayload> S2C_POLL_START = regS2C("poll_start", ClientboundPollStartPayload.CODEC);
    public static final CustomPacketPayload.Type<ClientboundPollUpdateVotesPayload> S2C_POLL_UPDATE = regS2C("poll_update", ClientboundPollUpdateVotesPayload.CODEC);

    private static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> regS2C(@NotNull String packetId, @NotNull StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        CustomPacketPayload.Type<T> reg = reg(packetId);
        PayloadTypeRegistry.playS2C().register(reg, codec);
        return reg;
    }

    private static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> regC2S(@NotNull String packetId, @NotNull StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        CustomPacketPayload.Type<T> reg = reg(packetId);
        PayloadTypeRegistry.playC2S().register(reg, codec);
        return reg;
    }

    private static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> reg(@NotNull String packetId) {
        return new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Utils.MOD_ID, packetId));
    }

}