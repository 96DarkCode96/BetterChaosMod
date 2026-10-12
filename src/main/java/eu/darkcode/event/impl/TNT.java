package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.item.PrimedTnt;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 01.01.2026
 **/
public class TNT extends InstantEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("tnt", TNT::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player ->{
            PrimedTnt spawn = EntityTypes.TNT.spawn(player.level(), player.blockPosition(), EntitySpawnReason.EVENT);
            if(spawn == null)
                return;
            spawn.setFuse(40);
        });
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
