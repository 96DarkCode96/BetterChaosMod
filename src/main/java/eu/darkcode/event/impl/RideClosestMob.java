package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 03.01.2026
 **/
public class RideClosestMob extends InstantEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("ride_closest_mob", RideClosestMob::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            Vec3 position = player.position();
            LivingEntity nearestEntity = player.level().getNearestEntity(LivingEntity.class,
                    TargetingConditions.forCombat(), player,
                    position.x(), position.y(), position.z(),
                    new AABB(position.add(64, 64, 64), position.add(-64, -64, -64)));
            if (nearestEntity != null)
                player.startRiding(nearestEntity, true, true);
        });
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
