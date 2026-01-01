package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 01.01.2026
 **/
public class DamageItems extends InstantEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("damage_items", DamageItems::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            RandomSource random = player.getRandom();
            player.getInventory().forEach(item -> {
                if (!item.isDamageableItem())
                    return;
                int damageAmount = Mth.ceil((item.getMaxDamage() - item.getDamageValue()) * random.nextFloat());
                item.hurtAndBreak(damageAmount, player.level(), player, a -> {});
            });
        });
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
