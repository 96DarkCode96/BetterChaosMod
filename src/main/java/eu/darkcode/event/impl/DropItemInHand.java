package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DropItemInHand extends InstantEvent {

    public static EventRegistry.Entry<?> ENTRY = EventRegistry.entry("drop_item_in_hand", DropItemInHand::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            Inventory inventory = player.getInventory();
            ItemStack itemStack = inventory.getSelectedItem();
            if (!itemStack.isEmpty()) {
                ItemEntity drop = inventory.player.createItemStackToDrop(itemStack, false, true);
                if (drop != null) {
                    inventory.player.level().addFreshEntity(drop);
                }

                inventory.setSelectedItem(ItemStack.EMPTY);
            }
        });
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }

}
