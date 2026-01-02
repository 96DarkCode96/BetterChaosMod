package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 02.01.2026
 **/
public class RemoveEnchantments extends InstantEvent {
    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("remove_enchantments", RemoveEnchantments::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            Inventory inventory = player.getInventory();

            for (int i = 0; i < 41; i++) {
                ItemStack stack = inventory.getItem(i);
                if (stack.isEmpty())
                    continue;

                if (stack.is(Items.ENCHANTED_GOLDEN_APPLE)) {
                    inventory.setItem(i, new ItemStack(Items.GOLDEN_APPLE, stack.getCount()));
                } else if (stack.is(Items.ENCHANTED_BOOK)) {
                    inventory.setItem(i, new ItemStack(Items.BOOK, stack.getCount()));
                } else {
                    stack.remove(DataComponents.ENCHANTMENTS);
                    stack.remove(DataComponents.STORED_ENCHANTMENTS);
                }
            }
        });
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
