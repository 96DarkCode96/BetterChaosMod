package eu.darkcode.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public interface Event {

    void start();

    @Environment(EnvType.CLIENT)
    void startClient();

    void tick();

    @Environment(EnvType.CLIENT)
    void tickClient();

    void end();

    void endPlayer(@NotNull ServerPlayer player);

    @Environment(EnvType.CLIENT)
    void endClient();

    int currentTick();

    void currentTick(int tick);

    int duration();

    boolean ended();

    void ended(boolean ended);
}
