package eu.darkcode.event.impl.move;

import eu.darkcode.client.SharedProperties;
import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.TimedEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.world.entity.player.Input;
import org.jetbrains.annotations.NotNull;

import static eu.darkcode.event.EventCategory.MOVEMENT;

public class NoSneak extends TimedEvent {

    public static EventRegistry.Entry<?> ENTRY = EventRegistry.entry("no_sneak", NoSneak::new).category(MOVEMENT).build();

    @Override
    public void start(@NotNull EventManager manager) {
        // ignored
    }

    @Override
    public void end(@NotNull EventManager manager) {
        // ignored
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }

    @Override
    public void startClient() {
        super.startClient();
        SharedProperties.moveEditor = input -> new Input(input.forward(), input.backward(), input.left(), input.right(), input.jump(), false, input.sprint());
    }

    @Override
    public void endClient() {
        super.endClient();
        SharedProperties.moveEditor = null;
    }
}
