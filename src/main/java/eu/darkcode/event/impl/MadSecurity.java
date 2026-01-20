package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.golem.IronGolem;
import org.jetbrains.annotations.NotNull;

/**
 * @author darkcode
 * @date 12.12.25
 **/
public class MadSecurity extends InstantEvent {

    public static EventRegistry.Entry<?> ENTRY = EventRegistry.entry("mad_security", MadSecurity::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            IronGolem golem = EntityType.IRON_GOLEM.spawn(player.level(), player.blockPosition(), EntitySpawnReason.EVENT);
            if (golem == null)
                return;
            golem.setTarget(player);
            golem.setPersistentAngerTarget(EntityReference.of(player.getUUID()));
            golem.setTimeToRemainAngry(20_000);
        });
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
