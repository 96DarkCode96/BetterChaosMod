package eu.darkcode.event;

import eu.darkcode.server.EventManager;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public abstract class InstantEvent implements Event {
    @Override
    public void startClient() {

    }

    @Override
    public void tick(@NotNull EventManager manager) {

    }

    @Override
    public void tickClient() {

    }

    @Override
    public void end(@NotNull EventManager manager) {

    }

    @Override
    public void endPlayer(@NotNull EventManager manager, @NotNull ServerPlayer player) {

    }

    @Override
    public void endClient() {

    }

    @Override
    public int currentTick() {
        return 0;
    }

    @Override
    public void currentTick(int tick) {
        // ignored
    }

    @Override
    public int duration() {
        return 0;
    }

    @Override
    public boolean ended() {
        return true;
    }

    @Override
    public void ended(boolean ended) {
        // ignored
    }
}