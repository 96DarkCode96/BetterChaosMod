package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import org.jetbrains.annotations.NotNull;

public class DropInventory extends InstantEvent {

    public static EventRegistry.Entry<?> ENTRY = EventRegistry.entry("drop_inventory", DropInventory::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> player.getInventory().dropAll());
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
