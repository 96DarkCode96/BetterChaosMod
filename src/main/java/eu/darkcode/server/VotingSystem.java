package eu.darkcode.server;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventCategory;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.networking.ClientboundPollStartPayload;
import eu.darkcode.utils.Utils;
import eu.darkcode.voting.Poll;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class VotingSystem {

    public static final @NotNull AtomicInteger POLL_ID = new AtomicInteger(0);

    private final @NotNull EventManager eventManager;
    private final @NotNull List<Event> events = new ArrayList<>();

    private final int[] votes = new int[Utils.COUNT_VOTE_OPTIONS];
    private @Nullable Poll poll;
    private int totalVotes = 0;

    public VotingSystem(@NotNull EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void newPoll() {
        this.events.clear();
        while (this.events.size() < Utils.COUNT_VOTE_OPTIONS - 1) {
            EventRegistry.Entry<?> entry = pickRandomEvent(events.stream().map(Event::entry).collect(Collectors.toSet()));
            if (entry == null)
                throw new IllegalStateException("There were not enough events for selection!");
            this.events.add(entry.construct());
        }

        List<Component> eventNames = new ArrayList<>();
        this.events.forEach(e -> eventNames.add(e.getName()));
        eventNames.add(Component.translatable("betterchaosmod.event.random"));

        this.poll = new Poll(POLL_ID.incrementAndGet(), eventNames);

        this.totalVotes = 0;
        Arrays.fill(this.votes, 0);

        ClientboundPollStartPayload packet = new ClientboundPollStartPayload(poll);
        PlayerLookup.all(eventManager.server).forEach(player -> ServerPlayNetworking.send(player, packet));
    }

    @NotNull
    public Poll poll() {
        return Objects.requireNonNull(poll, "Poll was accessed before VotingSystem initialization!");
    }

    @NotNull
    public PollResult endPoll() {
        if (poll == null)
            throw new IllegalStateException("Poll was accessed before VotingSystem initialization!");

        int max = 0;
        int selected = -1;
        if (totalVotes > 0) {
            for (int i = 0; i < this.votes.length; i++) {
                if (this.votes[i] <= max)
                    continue;
                max = this.votes[i];
                selected = i;
            }
        }

        Event result;
        if (selected >= 0 && selected < events.size()) {
            result = events.get(selected);
        } else {
            EventRegistry.Entry<?> entry = pickRandomEvent(this.events.stream().map(Event::entry).collect(Collectors.toSet()));
            if (entry == null)
                throw new IllegalStateException("There was nothing left that could be selected as random event!");
            result = entry.construct();
        }
        return new PollResult(poll.pollId(), this.totalVotes, this.votes, selected, result);
    }

    @Nullable
    private EventRegistry.Entry<?> pickRandomEvent(@NotNull Set<EventRegistry.Entry<?>> alreadyPicked) {
        Set<EventRegistry.Entry<?>> activeSet = eventManager.activeEvents().map(Event::entry).collect(Collectors.toSet());
        Set<EventCategory> activeCategories = eventManager.activeEvents()
                .filter(event -> !event.ended())
                .map(Event::entry)
                .map(EventRegistry.Entry::category)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return EventRegistry.pickRandomEvent(entry -> !activeSet.contains(entry) && !alreadyPicked.contains(entry) && (entry.category() == null || !activeCategories.contains(entry.category())));
    }

    public record PollResult(int pollId, int totalVotes, int[] votes, int selected, @NotNull Event event) {
    }
}
