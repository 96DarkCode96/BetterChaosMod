package eu.darkcode.event;

import eu.darkcode.BetterChaosMod;
import eu.darkcode.server.EventManager;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public abstract class TimedEvent implements Event {
    private int tick = 0;
    private boolean ended = false;

    @Override
    public void startClient() {

    }

    @Override
    public void tickClient() {

    }

    @Override
    public void endPlayer(@NotNull EventManager manager, @NotNull ServerPlayer player) {

    }

    @Override
    public void endClient() {

    }

    @Override
    public int currentTick() {
        return tick;
    }

    @Override
    public void currentTick(int tick) {
        this.tick = tick;
    }

    @Override
    public int duration() {
        return BetterChaosMod.instance().settings().eventDuration;
    }

    @Override
    public boolean ended() {
        return ended || currentTick() >= duration();
    }

    @Override
    public void ended(boolean ended) {
        this.ended = ended;
    }
}
