package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import org.jetbrains.annotations.NotNull;

public class Warden extends InstantEvent {

    public static EventRegistry.Entry<?> ENTRY = EventRegistry.entry("warden", Warden::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            net.minecraft.world.entity.monster.warden.Warden warden = EntityTypes.WARDEN.spawn(player.level(), player.blockPosition(), EntitySpawnReason.EVENT);
            if (warden == null)
                return;
            warden.setHealth(20);
            warden.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 9999, 5));
        });
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
