package eu.darkcode.event;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public abstract class InstantEvent implements Event {
    @Override
    public void end() {

    }

    @Override
    public void endPlayer(@NotNull ServerPlayer player) {

    }

    @Override
    public void endClient() {

    }

    @Override
    public void startClient() {

    }

    @Override
    public void tick() {

    }

    @Override
    public void tickClient() {

    }

    @Override
    public int duration() {
        return 0;
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
    public boolean ended() {
        return true;
    }

    @Override
    public void ended(boolean ended) {
        // ignored
    }
}