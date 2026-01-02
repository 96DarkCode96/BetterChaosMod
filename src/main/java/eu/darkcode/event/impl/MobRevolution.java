package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;


/**
 * @author _dark_code_
 * @date 02.01.2026
 **/
public class MobRevolution extends InstantEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("mob_revolution", MobRevolution::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            Vec3 position = player.position();
            AABB area = new AABB(position.add(-80, -80, -80), position.add(80, 80, 80));
            player.level().getEntities(player, area, NeutralMob.class::isInstance).forEach(entity -> {
                if (entity instanceof NeutralMob nm)
                    nm.setTarget(player);
            });
        });
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
