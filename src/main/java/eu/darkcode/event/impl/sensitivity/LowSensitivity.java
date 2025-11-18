package eu.darkcode.event.impl.sensitivity;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.TimedEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import org.jetbrains.annotations.NotNull;

import static eu.darkcode.event.EventCategory.SENSITIVITY;

public class LowSensitivity extends TimedEvent {

    public static EventRegistry.Entry<?> ENTRY = EventRegistry.entry("low_sensitivity", LowSensitivity::new).category(SENSITIVITY).build();

    private double storedSensitivity = 0.5d;

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
        OptionInstance<Double> sensitivity = Minecraft.getInstance().options.sensitivity();
        storedSensitivity = sensitivity.get();
        sensitivity.set(0d);
    }

    @Override
    public void tickClient() {
        super.tickClient();
        Minecraft.getInstance().options.sensitivity().set(0d);
    }

    @Override
    public void endClient() {
        super.endClient();
        Minecraft.getInstance().options.sensitivity().set(storedSensitivity);
    }
}
