package eu.darkcode.client;

import eu.darkcode.BetterChaosModClient;
import eu.darkcode.Settings;
import eu.darkcode.client.screen.GameLogScreen;
import eu.darkcode.client.screen.IntegrationCheckScreen;
import eu.darkcode.client.voting.Poll;
import eu.darkcode.client.voting.VotingSystem;
import eu.darkcode.event.CycleEvent;
import eu.darkcode.event.Event;
import eu.darkcode.voting.PollDefinition;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EventManager {

    private final @NotNull List<GameLogScreen.GameLogEntry> gameLogEntries = new ArrayList<>();

    private final @NotNull List<Event> activeEvents = new ArrayList<>();
    private final @NotNull Settings settings;
    private final @NotNull VotingSystem votingSystem;
    private int timerCount;
    private int timerDuration;

    public EventManager(@NotNull Settings settings) {
        this.settings = settings;
        this.timerCount = settings.timerDuration;
        this.timerDuration = settings.timerDuration;
        this.votingSystem = new VotingSystem();
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

        if (timerCount % 10 == 5) {
            Poll poll = votingSystem.poll();
            if (poll != null)
                poll.sendVotes();
        }
    }

    public @NotNull VotingSystem votingSystem() {
        return votingSystem;
    }

    public int baseEventDuration() {
        return settings.eventDuration;
    }

    public double getTimerFraction() {
        return 1.0 - timerCount / (double) timerDuration;
    }

    public void end() {
        activeEvents.forEach(event -> {
            if (!event.ended())
                event.endClient();
        });
        votingSystem.end();
    }

    public void startEvent(Event event) {
        int maxEntries = BetterChaosModClient.instance().settings().hud.activeEventsMaxEntries();
        Event pop;
        while (!this.activeEvents.isEmpty() && this.activeEvents.size() >= maxEntries && (pop = this.activeEvents.getFirst()) != null && pop.ended())
            this.activeEvents.removeFirst();
        this.gameLogEntries.add(event);
        this.activeEvents.add(event);
        event.startClient();
    }

    public void setupIntegration(@NotNull ClientConfigurationNetworking.Context context) {
        final Minecraft minecraft = context.client();
        // [OPEN INTEGRATION CHECK SCREEN]
        minecraft.setScreen(new IntegrationCheckScreen(minecraft.screen, context.responseSender()));
    }

    public void pollStarted(@NotNull PollDefinition poll) {
        // Cycle events
        activeEvents.forEach(event -> {
            if (event instanceof CycleEvent ce && !event.ended()) {
                ce.onCycle();
                if (event.ended())
                    event.endClient();
            }
        });
        this.timerDuration = (int) (settings.timerDuration / SharedProperties.timerDurationMultiplier);
    }
}