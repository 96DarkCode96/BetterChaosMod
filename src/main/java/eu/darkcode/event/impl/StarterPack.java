package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 02.01.2026
 **/
public class StarterPack extends InstantEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("starter_pack", StarterPack::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            give(player, new ItemStack(Items.IRON_PICKAXE));
            give(player, new ItemStack(Items.IRON_AXE));
            give(player, new ItemStack(Items.IRON_SHOVEL));
            give(player, new ItemStack(Items.IRON_SWORD));
            give(player, new ItemStack(Items.OAK_LOG, 8));
            give(player, new ItemStack(Items.COAL, 4));
            give(player, new ItemStack(Items.BREAD, 10));
        });
    }

    private static void give(@NotNull ServerPlayer player, @NotNull ItemStack item) {
        if (!player.getInventory().add(item))
            player.spawnAtLocation(player.level(), item);
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
