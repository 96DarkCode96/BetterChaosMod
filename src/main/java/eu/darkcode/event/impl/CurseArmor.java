package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * @author _dark_code_
 * @date 03.01.2026
 **/
public class CurseArmor extends InstantEvent {

    public static final @NotNull EquipmentSlot[] SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("curse_armor", CurseArmor::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        Holder.Reference<Enchantment> enchant = manager.server.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.BINDING_CURSE);
        manager.activePlayers().forEach(player ->
                Arrays.stream(SLOTS).forEach(slot ->
                        player.getItemBySlot(slot).enchant(enchant, 1)));
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
