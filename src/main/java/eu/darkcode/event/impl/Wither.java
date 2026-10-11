package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import org.jetbrains.annotations.NotNull;

public class Wither extends InstantEvent {

    public static EventRegistry.Entry<?> ENTRY = EventRegistry.entry("wither", Wither::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            WitherBoss wither = EntityTypes.WITHER.spawn(player.level(), player.blockPosition(), EntitySpawnReason.EVENT);
            if (wither == null)
                return;
            wither.setHealth(6);
            wither.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 9999, 5));
        });
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
