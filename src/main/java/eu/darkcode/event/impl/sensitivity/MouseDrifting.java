package eu.darkcode.event.impl.sensitivity;

import eu.darkcode.client.SharedProperties;
import eu.darkcode.event.Event;
import eu.darkcode.event.EventCategory;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.TimedEvent;
import eu.darkcode.server.EventManager;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author _dark_code_
 * @date 03.01.2026
 **/
public class MouseDrifting extends TimedEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("mouse_drifting", MouseDrifting::new).category(EventCategory.SENSITIVITY).build();

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
        ThreadLocalRandom r = ThreadLocalRandom.current();
        SharedProperties.mouseDX = (r.nextBoolean() ? 1 : -1) * r.nextDouble(1, 3);
        SharedProperties.mouseDY = (r.nextBoolean() ? 1 : -1) * r.nextDouble(0.25, 0.75);
    }

    @Override
    public void endClient() {
        super.endClient();
        SharedProperties.mouseDX = 0;
        SharedProperties.mouseDY = 0;
    }

    @Override
    public int duration() {
        return (int) (super.duration() * 1.5);
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
