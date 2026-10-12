package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Prediction;
import org.jetbrains.annotations.NotNull;

public class DropItemInHand extends InstantEvent {

    public static EventRegistry.Entry<?> ENTRY = EventRegistry.entry("drop_item_in_hand", DropItemInHand::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        // ignored
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }

    @Override
    public void startClient() {
        super.startClient();
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null)
            player.drop(player.getMainHandItem(), true, Prediction.PREDICTED); // TODO: TEST !!
    }
}
