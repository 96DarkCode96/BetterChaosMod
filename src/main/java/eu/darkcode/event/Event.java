package eu.darkcode.event;

import eu.darkcode.server.EventManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public interface Event {

    void start(@NotNull EventManager manager);

    @Environment(EnvType.CLIENT)
    void startClient();

    void tick(@NotNull EventManager manager);

    @Environment(EnvType.CLIENT)
    void tickClient();

    void end(@NotNull EventManager manager);

    void endPlayer(@NotNull EventManager manager, @NotNull ServerPlayer player);

    @Environment(EnvType.CLIENT)
    void endClient();

    int currentTick();

    int duration();

    boolean ended();

    void ended(boolean ended);

    @NotNull
    EventRegistry.Entry<? extends Event> entry();

    @NotNull
    default Component getName() {
        return Component.translatable("betterchaosmod.event." + entry().key());
    }
}
