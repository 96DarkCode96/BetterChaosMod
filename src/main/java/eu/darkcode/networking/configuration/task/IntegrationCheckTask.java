package eu.darkcode.networking.configuration.task;

import eu.darkcode.networking.configuration.ClientboundIntegrationCheck;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ConfigurationTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record IntegrationCheckTask() implements ConfigurationTask {

    public static final Type TYPE = new Type("betterchaosmod_task_integration_check");

    @Override
    public void start(Consumer<Packet<?>> consumer) {
        consumer.accept(ServerConfigurationNetworking.createClientboundPacket(ClientboundIntegrationCheck.INSTANCE));
    }

    @Override
    public @NotNull Type type() {
        return TYPE;
    }
}
