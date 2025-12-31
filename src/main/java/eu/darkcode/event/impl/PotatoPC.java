package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.TimedEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import org.jetbrains.annotations.NotNull;

import static eu.darkcode.event.EventCategory.VISUAL;

/**
 * @author _dark_code_
 * @date 31.12.2025
 **/
public class PotatoPC extends TimedEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("potato_pc", PotatoPC::new).category(VISUAL).build();

    private Integer storedFramelimit = null;

    @Override
    public void start(@NotNull EventManager manager) {
        //ignored
    }

    @Override
    public void end(@NotNull EventManager manager) {
        //ignored
    }

    @Override
    public void startClient() {
        super.startClient();
        OptionInstance<Integer> framerateLimit = Minecraft.getInstance().options.framerateLimit();
        storedFramelimit = framerateLimit.get();
        framerateLimit.set(10);
    }

    @Override
    public void tickClient() {
        super.tickClient();
        Minecraft.getInstance().options.framerateLimit().set(10);
    }

    @Override
    public void endClient() {
        super.endClient();
        if(storedFramelimit != null)
            Minecraft.getInstance().options.framerateLimit().set(storedFramelimit);
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
