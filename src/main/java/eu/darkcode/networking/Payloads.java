package eu.darkcode.networking;

import eu.darkcode.networking.configuration.ClientboundHandshake;
import eu.darkcode.networking.configuration.ClientboundIntegrationCheck;
import eu.darkcode.networking.configuration.ServerboundHandshakeAck;
import eu.darkcode.networking.configuration.ServerboundIntegrationPass;
import eu.darkcode.utils.Utils;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public final class Payloads {

    public static final CustomPacketPayload.Type<ClientboundHandshake> S2C_HANDSHAKE = regConfigS2C("handshake", ClientboundHandshake.CODEC);
    public static final CustomPacketPayload.Type<ServerboundHandshakeAck> C2S_HANDSHAKE_ACK = regConfigC2S("handshake_ack", ServerboundHandshakeAck.CODEC);
    public static final CustomPacketPayload.Type<ClientboundIntegrationCheck> S2C_INTEGRATION_CHECK = regConfigS2C("integration_check", ClientboundIntegrationCheck.STREAM_CODEC);
    public static final CustomPacketPayload.Type<ServerboundIntegrationPass> C2S_INTEGRATION_PASS = regConfigC2S("integration_pass", ServerboundIntegrationPass.CODEC);

    public static final CustomPacketPayload.Type<ClientboundTickPayload> S2C_TICK = regPlayS2C("tick", ClientboundTickPayload.CODEC);

    public static final CustomPacketPayload.Type<ClientboundEventStartPayload> S2C_EVENT_START = regPlayS2C("event_start", ClientboundEventStartPayload.CODEC);
    public static final CustomPacketPayload.Type<ClientboundPollStartPayload> S2C_POLL_START = regPlayS2C("poll_start", ClientboundPollStartPayload.CODEC);
    public static final CustomPacketPayload.Type<ClientboundPollUpdateVotesPayload> S2C_POLL_UPDATE = regPlayS2C("poll_update", ClientboundPollUpdateVotesPayload.STREAM_CODEC);

    public static final CustomPacketPayload.Type<ClientboundSetLockedSlotsPayload> S2C_SET_LOCKED_SLOTS = regPlayS2C("set_locked_slots", ClientboundSetLockedSlotsPayload.CODEC);

    public static final CustomPacketPayload.Type<ServerboundSendVotesPayload> C2S_SEND_VOTES = regPlayC2S("send_votes", ServerboundSendVotesPayload.CODEC);

    private static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> regConfigS2C(@NotNull String packetId, @NotNull StreamCodec<FriendlyByteBuf, T> codec) {
        CustomPacketPayload.Type<T> reg = reg(packetId);
        PayloadTypeRegistry.clientboundConfiguration().register(reg, codec);
        return reg;
    }

    private static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> regConfigC2S(@NotNull String packetId, @NotNull StreamCodec<FriendlyByteBuf, T> codec) {
        CustomPacketPayload.Type<T> reg = reg(packetId);
        PayloadTypeRegistry.serverboundConfiguration().register(reg, codec);
        return reg;
    }

    private static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> regPlayS2C(@NotNull String packetId, @NotNull StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        CustomPacketPayload.Type<T> reg = reg(packetId);
        PayloadTypeRegistry.clientboundPlay().register(reg, codec);
        return reg;
    }

    private static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> regPlayC2S(@NotNull String packetId, @NotNull StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        CustomPacketPayload.Type<T> reg = reg(packetId);
        PayloadTypeRegistry.serverboundPlay().register(reg, codec);
        return reg;
    }

    private static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> reg(@NotNull String packetId) {
        return new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Utils.MOD_ID, packetId));
    }

}