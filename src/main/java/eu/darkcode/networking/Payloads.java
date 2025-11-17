package eu.darkcode.networking;

import eu.darkcode.utils.Utils;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class Payloads {
    public static final CustomPacketPayload.Type<ServerboundHandshakePayload> C2S_HANDSHAKE = reg("handshake");
    public static final CustomPacketPayload.Type<ClientboundTickPayload> S2C_TICK = reg("tick");
    public static final CustomPacketPayload.Type<ClientboundHandshakeConfirmPayload> S2C_HANDSHAKE_CONFIRM = reg("handshake_confirm");
    public static final CustomPacketPayload.Type<ClientboundEventStartPayload> S2C_EVENT_START = reg("event_start");

    static {
        PayloadTypeRegistry.playC2S().register(C2S_HANDSHAKE, ServerboundHandshakePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(S2C_TICK, ClientboundTickPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(S2C_HANDSHAKE_CONFIRM, ClientboundHandshakeConfirmPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(S2C_EVENT_START, ClientboundEventStartPayload.CODEC);
    }

    private static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> reg(@NotNull String packetId) {
        return new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Utils.MOD_ID, packetId));
    }

}