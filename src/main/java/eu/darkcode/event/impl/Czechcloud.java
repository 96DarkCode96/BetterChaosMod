package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 31.12.2025
 **/
public class Czechcloud extends InstantEvent {

    public static final EventRegistry.Entry<?> ENTRY = EventRegistry.entry("czechcloud", Czechcloud::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            ItemStack item = player.getItemBySlot(EquipmentSlot.HEAD);
            if (item.isEmpty())
                return;
            item.hurtAndBreak(item.getMaxDamage() + 1, player, EquipmentSlot.HEAD);
        });
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
