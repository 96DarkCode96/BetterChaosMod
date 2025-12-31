package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class Blaze extends InstantEvent {

    public static EventRegistry.Entry<?> ENTRY = EventRegistry.entry("blaze", Blaze::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            net.minecraft.world.entity.monster.Blaze blaze = EntityType.BLAZE.spawn(player.level(), player.blockPosition(), EntitySpawnReason.EVENT);
            if (blaze == null)
                return;
            blaze.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 9999, 2));
            blaze.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 300, 2));
        });
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
