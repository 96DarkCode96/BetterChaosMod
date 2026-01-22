package eu.darkcode.event.impl;

import eu.darkcode.client.SharedProperties;
import eu.darkcode.event.Event;
import eu.darkcode.event.EventCategory;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.TimedEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 01.01.2026
 **/
public class WhereIsMyWorld extends TimedEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("where_is_my_world", WhereIsMyWorld::new).category(EventCategory.VISUAL).build();

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
        SharedProperties.noWorld = true;
        Minecraft.getInstance().levelRenderer.allChanged();
    }

    @Override
    public void endClient() {
        super.endClient();
        SharedProperties.noWorld = false;
        Minecraft.getInstance().levelRenderer.allChanged();
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
