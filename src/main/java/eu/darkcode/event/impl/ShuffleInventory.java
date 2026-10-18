package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.event.impl.inventory.Lockable;
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
public class ShuffleInventory extends InstantEvent {
    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("shuffle_inventory", ShuffleInventory::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            Inventory inventory = player.getInventory();
            if (inventory.isEmpty())
                return;
            Lockable lockable = (Lockable) player;
            List<ItemStack> stacks = new ArrayList<>();
            List<Integer> unlockedSlots = new ArrayList<>();

            for (int i = 0; i < 41; i++) {
                if (lockable.betterchaosmod$isSlotLocked(i))
                    continue;
                unlockedSlots.add(i);
                stacks.add(inventory.getItem(i));
            }

            Collections.shuffle(stacks);

            for (int i = 0; i < stacks.size(); i++)
                inventory.setItem(unlockedSlots.get(i), stacks.get(i));
        });
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
