package eu.darkcode.event.impl;

import eu.darkcode.client.SharedProperties;
import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.TimedEvent;
import eu.darkcode.server.EventManager;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 31.12.2025
 **/
public class NoDrops extends TimedEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("no_drops", NoDrops::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        SharedProperties.noDrops = true;
    }

    @Override
    public void end(@NotNull EventManager manager) {
        SharedProperties.noDrops = false;
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
