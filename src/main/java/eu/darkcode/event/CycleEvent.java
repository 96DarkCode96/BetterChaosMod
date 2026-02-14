package eu.darkcode.event;

import eu.darkcode.server.EventManager;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public abstract class CycleEvent implements Event {
    protected int cycle = 0;
    private boolean ended = false;

    @Override
    public int currentTick() {
        return cycle;
    }

    @Override
    public int duration() {
        return 1;
    }

    @Override
    public void endClient() {

    }

    @Override
    public void endPlayer(@NotNull EventManager manager, @NotNull ServerPlayer player) {

    }

    @Override
    public boolean ended() {
        return ended || currentTick() >= duration();
    }

    @Override
    public void ended(boolean ended) {
        this.ended = ended;
    }

    @Override
    public void startClient() {

    }

    @Override
    public void tick(@NotNull EventManager manager) {
        // ignored
    }

    @Override
    public void tickClient() {
        // ignored
    }

    public void onCycle() {
        cycle++;
    }
}
