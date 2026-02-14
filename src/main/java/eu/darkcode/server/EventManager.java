package eu.darkcode.server;

import eu.darkcode.BetterChaosMod;
import eu.darkcode.client.SharedProperties;
import eu.darkcode.event.CycleEvent;
import eu.darkcode.event.Event;
import eu.darkcode.networking.ClientboundEventStartPayload;
import eu.darkcode.networking.ClientboundTickPayload;
import eu.darkcode.utils.Utils;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class EventManager {

    public final @NotNull MinecraftServer server;
    private final @NotNull List<Event> activeEvents = new ArrayList<>();
    private final @NotNull VotingSystem votingSystem;
    private int timerCount = 0;

    public EventManager(@NotNull MinecraftServer server) {
        this.server = server;
        this.votingSystem = new VotingSystem(this);
        resetTimerCount();
    }

    private void resetTimerCount() {
        this.votingSystem.newPoll();
        this.timerCount = (int) (BetterChaosMod.instance().settings().timerDuration / SharedProperties.timerDurationMultiplier);
    }

    @NotNull
    public List<ServerPlayer> activePlayers() {
        return Utils.getActivePlayers(server);
    }

    @NotNull
    public VotingSystem votingSystem() {
        return votingSystem;
    }

    public void tick() {
        if (timerCount == 0) {
            // Cycle events
            activeEvents.forEach(event -> {
                if (event instanceof CycleEvent ce && !event.ended()) {
                    ce.onCycle();
                    if (event.ended())
                        event.end(this);
                }
            });

            int selectCooldown = BetterChaosMod.instance().settings().eventRepetitionCooldown;
            Event pop;
            while (!this.activeEvents.isEmpty() && this.activeEvents.size() > selectCooldown && (pop = this.activeEvents.getFirst()) != null && pop.ended())
                this.activeEvents.removeFirst();

            try {
                VotingSystem.PollResult res = this.votingSystem.endPoll();
                Event event = res.event();
                executeEvent(event);
            } catch (Throwable e) {
                BetterChaosMod.LOGGER.error("Error when processing events", e);
            }

            resetTimerCount();
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

    public void executeEvent(@NotNull Event event) {
        activeEvents.add(event);
        event.start(this);

        ClientboundEventStartPayload packet = new ClientboundEventStartPayload(event);
        PlayerLookup.all(server).forEach(player -> ServerPlayNetworking.send(player, packet));
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

    @NotNull
    public Stream<Event> activeEvents() {
        return activeEvents.stream();
    }
}
