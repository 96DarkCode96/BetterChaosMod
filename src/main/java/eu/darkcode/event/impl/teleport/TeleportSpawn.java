package eu.darkcode.event.impl.teleport;

import eu.darkcode.BetterChaosMod;
import eu.darkcode.event.Event;
import eu.darkcode.event.EventCategory;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import eu.darkcode.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 02.01.2026
 **/
public class TeleportSpawn extends InstantEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("teleport_spawn", TeleportSpawn::new).category(EventCategory.TELEPORT).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            ServerLevel overworld = manager.server.getLevel(Level.OVERWORLD);
            if (overworld == null) {
                BetterChaosMod.LOGGER.error("Overworld level not found for teleport spawn!");
                return;
            }
            BlockPos pos = overworld.getLevelData().getRespawnData().pos();
            Utils.teleportPlayer(player, overworld, pos.getCenter());
            Utils.clearPlayerArea(player);
        });
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
