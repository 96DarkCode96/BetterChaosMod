package eu.darkcode.client;

import eu.darkcode.Settings;
import eu.darkcode.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EventManager {

    private final @NotNull List<Event> activeEvents = new ArrayList<>();
    private final @NotNull Settings settings;
    private int timerCount;

    public EventManager(@NotNull Settings settings) {
        this.settings = settings;
        this.timerCount = settings.timerDuration;
    }

    @NotNull
    public List<Event> getActiveEvents() {
        return activeEvents;
    }

    public void tick(int timerCount) {
        this.timerCount = timerCount;

        activeEvents.forEach(event -> {
            if (!event.ended()) {
                event.tickClient();
                if (event.currentTick() >= event.duration())
                    event.endClient();
            }
        });
    }

    public double getTimerFraction() {
        return 1.0 - (double) timerCount / settings.timerDuration;
    }

    public void end() {
        activeEvents.forEach(event -> {
            if (!event.ended())
                event.endClient();
        });
    }

    public void startEvent(Event event) {
        this.activeEvents.add(event);
        event.startClient();
    }
}