package eu.darkcode.event.impl;

import eu.darkcode.BetterChaosModClient;
import eu.darkcode.client.SharedProperties;
import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.TimedEvent;
import eu.darkcode.server.EventManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.NotNull;

import static eu.darkcode.event.EventCategory.VISUAL;

/**
 * @author _dark_code_
 * @date 30.12.2025
 **/
public class Jumpscare extends TimedEvent {
    public static final EventRegistry.Entry<?> ENTRY = EventRegistry.entry("jumpscare", Jumpscare::new).disableAccessibility().category(VISUAL).build();

    @Override
    public void start(@NotNull EventManager manager) {
        // ignored
    }

    @Override
    public void end(@NotNull EventManager manager) {
        // ignored
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void startClient() {
        Minecraft.getInstance().player.level().playSound(
                Minecraft.getInstance().player,
                Minecraft.getInstance().player.blockPosition(),
                BetterChaosModClient.jumpscareEvent,
                SoundSource.MASTER,
                1.0f, 0.9f);
        SharedProperties.jumpscareActive = true;
    }

    @Override
    public void endClient() {
        super.endClient();
        SharedProperties.jumpscareActive = false;
    }

    @Override
    public int duration() {
        return 60; // Duration in ticks (3 seconds)
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
