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
public class HighPitch extends TimedEvent {
    public static EventRegistry.Entry<?> ENTRY = EventRegistry.entry("high_pitch", HighPitch::new).build();

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
        SharedProperties.soundPitchEditor = (a, b) -> 2.8f;
    }

    @Override
    public void endClient() {
        super.endClient();
        SharedProperties.soundPitchEditor = null;
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
