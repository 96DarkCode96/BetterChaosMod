package eu.darkcode.server;

import eu.darkcode.BetterChaosMod;
import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.networking.ClientboundEventStartPayload;
import eu.darkcode.networking.ClientboundTickPayload;
import eu.darkcode.utils.Utils;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class EventManager {

    public final @NotNull MinecraftServer server;
    private final @NotNull List<Event> activeEvents = new ArrayList<>();
    private int timerCount = 0;

    public EventManager(@NotNull MinecraftServer server) {
        this.server = server;
        resetTimerCount();
    }

    private void resetTimerCount() {
        this.timerCount = BetterChaosMod.instance().settings().timerDuration;
    }

    @NotNull
    public List<ServerPlayer> activePlayers() {
        return Utils.getActivePlayers(server);
    }

    public void tick() {
        if (timerCount == 0) {
            resetTimerCount();

            Event event = chooseEvent();

            if (event != null) {
                activeEvents.add(event);
                event.start(this);
                ClientboundEventStartPayload packet = new ClientboundEventStartPayload(event);
                PlayerLookup.all(server).forEach(player -> ServerPlayNetworking.send(player, packet));
            }

            // TODO: Pick new events
        }

        activeEvents.forEach(event -> {
            if (!event.ended()) {
                event.tick(this);
                if (event.currentTick() >= event.duration())
                    event.end(this);
            }
        });

        ClientboundTickPayload packet = new ClientboundTickPayload(timerCount);
        PlayerLookup.all(server).forEach(player -> ServerPlayNetworking.send(player, packet));

        timerCount--;
    }

    @Nullable
    private Event chooseEvent() {
        EventRegistry.Entry<?> entry = pickRandomEvent(List.of());
        return entry == null ? null : entry.construct();
    }

    @Nullable
    private EventRegistry.Entry<?> pickRandomEvent(@NotNull List<EventRegistry.Entry<?>> alreadyPicked) {
        Set<EventRegistry.Entry<?>> activeSet = activeEvents.stream().map(Event::entry).collect(Collectors.toSet());

        List<EventRegistry.Entry<?>> candidates = EventRegistry.EVENTS.stream()
                .filter(e -> !activeSet.contains(e))              // remove active
                .filter(e -> !alreadyPicked.contains(e))          // remove already picked
                .toList();

        // Return null if no candidates
        if (candidates.isEmpty())
            return null;

        // Pick a random entry
        return candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
    }

    public void end() {
        activeEvents.forEach(event -> {
            if (!event.ended()) {
                event.end(this);
                event.ended(true);
            }
        });
    }

    public void endPlayer(@NotNull ServerPlayer player) {
        activeEvents.forEach(event -> {
            if (!event.ended())
                event.endPlayer(this, player);
        });
    }
}
