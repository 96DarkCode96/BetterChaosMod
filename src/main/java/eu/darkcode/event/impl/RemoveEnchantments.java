package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
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
            inventory.forEach(item -> EnchantmentHelper.updateEnchantments(item, enchantments ->
                    enchantments.removeIf(h -> true)));
        });
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
