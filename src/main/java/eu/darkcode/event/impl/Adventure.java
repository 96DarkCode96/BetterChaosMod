package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.TimedEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.NotNull;

public class Adventure extends TimedEvent {

    public static EventRegistry.Entry<?> ENTRY = EventRegistry.entry("adventure", Adventure::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> player.setGameMode(GameType.ADVENTURE));
    }

    @Override
    public void end(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> player.setGameMode(GameType.DEFAULT_MODE));
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }

    @Override
    public void endPlayer(@NotNull EventManager manager, @NotNull ServerPlayer player) {
        player.setGameMode(GameType.DEFAULT_MODE);
    }
}
