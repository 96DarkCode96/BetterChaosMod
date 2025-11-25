package eu.darkcode.networking.configuration;

import eu.darkcode.networking.Payloads;
import eu.darkcode.voting.IntegrationType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public record ServerboundIntegrationPass(@NotNull EnumSet<IntegrationType> integrations) implements CustomPacketPayload {

    public static final StreamCodec<FriendlyByteBuf, ServerboundIntegrationPass> CODEC = StreamCodec.composite(
            IntegrationType.CODEC, ServerboundIntegrationPass::integrations,
            ServerboundIntegrationPass::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return Payloads.C2S_INTEGRATION_PASS;
    }
}
