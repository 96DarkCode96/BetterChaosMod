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
public class RandomDrops extends TimedEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("random_drops", RandomDrops::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        SharedProperties.randomDrops = true;
    }

    @Override
    public void end(@NotNull EventManager manager) {
        SharedProperties.randomDrops = false;
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }

    @Override
    public int duration() {
        return super.duration() / 2;
    }
}