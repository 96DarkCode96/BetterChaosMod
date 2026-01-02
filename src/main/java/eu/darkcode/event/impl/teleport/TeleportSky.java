package eu.darkcode.event.impl.teleport;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventCategory;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import eu.darkcode.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 02.01.2026
 **/
public class TeleportSky extends InstantEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("teleport_sky", TeleportSky::new).category(EventCategory.TELEPORT).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            int height = player.level().dimension() == Level.OVERWORLD ? 319 : 254;
            spawnPlatform(player, height);
            Utils.teleportPlayer(player, Vec3.atBottomCenterOf(player.blockPosition().atY(height + 2)));
        });
    }

    private static void spawnPlatform(@NotNull ServerPlayer player, int height) {
        ServerLevel level = player.level();
        BlockPos pos = player.blockPosition().atY(height);
        for (int x = -3; x <= 4; x++) {
            for (int z = -3; z <= 4; z++) {
                level.setBlockAndUpdate(new BlockPos(pos.getX() + x, pos.getY(), pos.getZ() + z), Blocks.GLASS.defaultBlockState());
            }
        }
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
