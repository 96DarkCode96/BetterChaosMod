package eu.darkcode.event.impl;

import eu.darkcode.ChaosTags;
import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;


/**
 * @author _dark_code_
 * @date 02.01.2026
 **/
public class ExplodeNearbyEntities extends InstantEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("explode_nearby_entities", ExplodeNearbyEntities::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            ServerLevel level = player.level();
            Vec3 position = player.position();
            AABB area = new AABB(position.add(-80, -80, -80), position.add(80, 80, 80));
            level.getEntities(player, area).forEach(entity -> {
                if (entity.is(ChaosTags.DO_NOT_EXPLODE_ENTITY))
                    return;

                level.explode(entity, entity.getX(), entity.getY() + 0.5, entity.getZ(), 2, Level.ExplosionInteraction.MOB);
                entity.kill(level);
            });
        });
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
