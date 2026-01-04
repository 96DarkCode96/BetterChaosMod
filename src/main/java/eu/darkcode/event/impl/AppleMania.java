package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 03.01.2026
 **/
public class AppleMania extends InstantEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("apple_mania", AppleMania::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            Inventory inventory = player.getInventory();
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack itemStack = inventory.getItem(i);
                if (itemStack.isEmpty() || !itemStack.has(DataComponents.FOOD) || itemStack.is(Items.APPLE) || itemStack.is(Items.GOLDEN_APPLE) || itemStack.is(Items.ENCHANTED_GOLDEN_APPLE))
                    continue;

                int chance = player.getRandom().nextInt(100);

                ItemStack result;
                if (chance < 70)
                    result = new ItemStack(Items.APPLE, itemStack.getCount());
                else if (chance < 95)
                    result = new ItemStack(Items.GOLDEN_APPLE, itemStack.getCount());
                else
                    result = new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, itemStack.getCount());
                inventory.setItem(i, result);
            }
        });
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }

}
