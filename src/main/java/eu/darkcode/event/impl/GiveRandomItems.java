package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import eu.darkcode.utils.Utils;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 02.01.2026
 **/
public class GiveRandomItems extends InstantEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("give_random_items", GiveRandomItems::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            Inventory inventory = player.getInventory();
            for (int i = 0; i < inventory.getContainerSize(); i++)
                if (inventory.getItem(i).isEmpty())
                    inventory.setItem(i, new ItemStack(Utils.getRandomItem(player.level())));
        });
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
