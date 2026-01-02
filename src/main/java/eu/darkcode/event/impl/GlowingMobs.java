package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.TimedEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author _dark_code_
 * @date 02.01.2026
 **/
public class GlowingMobs extends TimedEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("glowing_mobs", GlowingMobs::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        // ignored
    }

    @Override
    public void end(@NotNull EventManager manager) {
        // ignored
    }

    @Override
    public void tick(@NotNull EventManager manager) {
        if (currentTick() % 5 == 0) {
            List<ServerLevel> worlds = new ArrayList<>();
            for (ServerPlayer player : manager.activePlayers()) {
                ServerLevel playerWorld = player.level();
                if (!worlds.contains(playerWorld))
                    worlds.add(playerWorld);
            }
            worlds.forEach(world -> world.getAllEntities().forEach(entity -> {
                if (entity instanceof Mob mob)
                    mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, 10));
            }));
        }
        super.tick(manager);
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
