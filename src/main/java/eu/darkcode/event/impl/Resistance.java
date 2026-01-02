package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventCategory;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.TimedEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 31.12.2025
 **/
public class Resistance extends TimedEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("resistance", Resistance::new).category(EventCategory.EFFECT).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, duration())));
    }

    @Override
    public void end(@NotNull EventManager manager) {
        // ignored
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }

}
