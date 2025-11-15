package eu.darkcode.event;

import eu.darkcode.Settings;

public abstract class TimedEvent implements Event{
    private int tick = 0;
    private boolean ended = false;

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
        return Settings.eventDuration;
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
