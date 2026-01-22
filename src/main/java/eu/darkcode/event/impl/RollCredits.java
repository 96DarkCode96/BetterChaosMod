package eu.darkcode.event.impl;

import eu.darkcode.client.screen.CreditsScreen;
import eu.darkcode.event.Event;
import eu.darkcode.event.EventCategory;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.TimedEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 02.01.2026
 **/
public class RollCredits extends TimedEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("roll_credits", RollCredits::new).category(EventCategory.VISUAL).build();

    @Override
    public void start(@NotNull EventManager manager) {
        // ignored
    }

    @Override
    public void end(@NotNull EventManager manager) {
        // ignored
    }

    @Override
    public void startClient() {
        super.startClient();
        Minecraft.getInstance().setScreen(new CreditsScreen());
    }

    @Override
    public void tickClient() {
        if (currentTick() % 20 == 0 && Minecraft.getInstance().screen == null)
            Minecraft.getInstance().setScreen(new CreditsScreen());
        super.tickClient();
    }

    @Override
    public void endClient() {
        if (Minecraft.getInstance().screen instanceof CreditsScreen cs) {
            cs.onClose();
            Minecraft.getInstance().mouseHandler.grabMouse();
        }
        super.endClient();
    }

    @Override
    public int duration() {
        return super.duration() / 2;
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
