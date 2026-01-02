package eu.darkcode.event.impl.teleport;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventCategory;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.TimedEvent;
import eu.darkcode.server.EventManager;
import eu.darkcode.utils.Utils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author _dark_code_
 * @date 02.01.2026
 **/
public class FakeTeleport extends TimedEvent {

    private static final @NotNull StreamCodec<RegistryFriendlyByteBuf, FakeTeleport> CODEC = StreamCodec.composite(EventRegistry.CODEC, FakeTeleport::teleportEvent, FakeTeleport::new);
    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("fake_teleport", FakeTeleport::new).codec(CODEC).category(EventCategory.TELEPORT).build();
    private static final @NotNull List<EventRegistry.Entry<?>> TELEPORT_EVENTS = Arrays.asList(
            TeleportHeaven.ENTRY,
            TeleportRandom.ENTRY,
            TeleportSpawn.ENTRY,
            TeleportSky.ENTRY
    );

    private final @NotNull Map<ServerPlayer, Utils.TeleportInfo> originalPositions = new HashMap<>();
    private final @NotNull Event teleportEvent;

    public FakeTeleport() {
        this(TELEPORT_EVENTS.get(new Random().nextInt(TELEPORT_EVENTS.size())).construct());
    }

    public FakeTeleport(@NotNull Event teleportEvent) {
        this.teleportEvent = teleportEvent;
    }

    @NotNull
    public Event teleportEvent() {
        return this.teleportEvent;
    }

    @Override
    public void start(@NotNull EventManager manager) {
        Utils.savePositions(manager, originalPositions);
        teleportEvent.start(manager);
    }

    @Override
    public void end(@NotNull EventManager manager) {
        Utils.loadPositions(manager, originalPositions);
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }

    @Override
    public int duration() {
        return 100;
    }

    @Override
    public boolean renderDurationBar() {
        return false;
    }

    @Override
    public @NotNull Component getName() {
        return ended() ? super.getName() : teleportEvent.getName();
    }

    @Override
    public @NotNull Component getVoteName() {
        return super.getName();
    }
}
