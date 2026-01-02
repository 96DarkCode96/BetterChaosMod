package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.TimedEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 02.01.2026
 **/
public class TimeToFly extends TimedEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("time_to_fly", TimeToFly::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> player.getAbilities().mayfly = true);
    }

    @Override
    public void startClient() {
        var player = Minecraft.getInstance().player;
        if (player != null)
            player.getAbilities().mayfly = true;
    }

    @Override
    public void end(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        });
    }

    @Override
    public void endClient() {
        super.endClient();
        var player = Minecraft.getInstance().player;
        if (player != null) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }
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
