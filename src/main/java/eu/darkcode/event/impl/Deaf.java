package eu.darkcode.event.impl;

import eu.darkcode.client.SharedProperties;
import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.TimedEvent;
import eu.darkcode.server.EventManager;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 30.12.2025
 **/
public class Deaf extends TimedEvent {
    public static EventRegistry.Entry<?> ENTRY = EventRegistry.entry("deaf", Deaf::new).disableAccessibility().build();

    @Override
    public void start(@NotNull EventManager manager) {
        // ignored
    }

    @Override
    public void end(@NotNull EventManager manager) {
        // ignored
    }

    @Override
    public void startClient() {
        super.startClient();
        SharedProperties.disableSounds = true;
    }

    @Override
    public void endClient() {
        super.endClient();
        SharedProperties.disableSounds = false;
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
