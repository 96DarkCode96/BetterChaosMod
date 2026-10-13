package eu.darkcode.event.impl.inventory;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 12.10.2026
 **/
public class LockInventorySlots extends InstantEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("lock_inventory_slots", LockInventorySlots::new).build();

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> ((Lockable) player).betterchaosmod$lockSlots(player.getRandom().nextInt(3, 5)));
    }
}
