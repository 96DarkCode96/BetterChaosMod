package eu.darkcode.client;

import eu.darkcode.BetterChaosModClient;
import eu.darkcode.Settings;
import eu.darkcode.client.screen.GameLogScreen;
import eu.darkcode.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EventManager {

    private final @NotNull List<GameLogScreen.GameLogEntry> gameLogEntries = new ArrayList<>();

    private final @NotNull List<Event> activeEvents = new ArrayList<>();
    private final @NotNull Settings settings;
    private int timerCount;

    public EventManager(@NotNull Settings settings) {
        this.settings = settings;
        this.timerCount = settings.timerDuration;
    }

    @NotNull
    public List<GameLogScreen.GameLogEntry> getGameLogEntries() {
        return gameLogEntries;
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
        int maxEntries = BetterChaosModClient.instance().settings().hud.activeEventsMaxEntries;
        Event pop;
        while (!this.activeEvents.isEmpty() && this.activeEvents.size() >= maxEntries && (pop = this.activeEvents.getFirst()) != null && pop.ended())
            this.activeEvents.removeFirst();
        this.gameLogEntries.add(event);
        this.activeEvents.add(event);
        event.startClient();
    }
}