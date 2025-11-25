package eu.darkcode.networking.configuration.task;

import eu.darkcode.Settings;
import eu.darkcode.networking.configuration.ClientboundHandshake;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ConfigurationTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record HandshakeTask(@NotNull Settings settings) implements ConfigurationTask {

    public static final Type TYPE = new Type("betterchaosmod_task_handshake");

    @Override
    public void start(Consumer<Packet<?>> consumer) {
        consumer.accept(ServerConfigurationNetworking.createS2CPacket(new ClientboundHandshake(settings)));
    }

    @Override
    public @NotNull Type type() {
        return TYPE;
    }
}
