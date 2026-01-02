package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 02.01.2026
 **/
public class HungerGames extends InstantEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("hunger_games", HungerGames::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> player.getFoodData().setFoodLevel(0));
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
