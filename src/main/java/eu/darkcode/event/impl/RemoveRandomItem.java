package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author _dark_code_
 * @date 02.01.2026
 **/
public class RemoveRandomItem extends InstantEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("remove_random_item", RemoveRandomItem::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            Inventory inventory = player.getInventory();
            if (inventory.isEmpty())
                return;
            List<Integer> slots = new ArrayList<>();
            for (int i = 0; i < inventory.getContainerSize(); i++)
                slots.add(i);
            Collections.shuffle(slots);

            for (Integer i : slots) {
                if (!inventory.getItem(i).isEmpty()) {
                    inventory.setItem(i, ItemStack.EMPTY);
                    break;
                }
            }
        });
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
