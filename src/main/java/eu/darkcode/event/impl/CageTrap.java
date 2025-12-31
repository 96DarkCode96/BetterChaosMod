package eu.darkcode.event.impl;

import eu.darkcode.ChaosTags;
import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 31.12.2025
 **/
public class CageTrap extends InstantEvent {

    public static final EventRegistry.Entry<?> ENTRY = EventRegistry.entry("cage_trap", CageTrap::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            ServerLevel world = player.level();
            int x = player.getBlockX();
            int y = player.getBlockY() - 1;
            int z = player.getBlockZ();
            placeObsidian(world, x, y++, z, player.getRandom());
            placeCage(world, x, y++, z);
            placeCage(world, x, y++, z);
            placeObsidian(world, x, y, z, player.getRandom());
        });
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }

    private static void placeObsidian(ServerLevel world, int x, int y, int z, RandomSource obs) {
        for (int j = -1; j < 2; j++) {
            for (int k = -1; k < 2; k++) {
                BlockPos pos = new BlockPos(x + j, y, z + k);
                if (!world.getBlockState(pos).is(ChaosTags.DO_NOT_REPLACE_BLOCKS))
                    world.setBlockAndUpdate(pos, (obs.nextBoolean() ? Blocks.OBSIDIAN : Blocks.CRYING_OBSIDIAN).defaultBlockState());
            }
        }
    }

    private static void placeCage(ServerLevel world, int x, int y, int z) {
        for (int j = -1; j < 2; j++) {
            for (int k = -1; k < 2; k++) {
                if (k == 0 && j == 0)
                    continue;
                BlockPos pos = new BlockPos(x + j, y, z + k);
                if (!world.getBlockState(pos).is(ChaosTags.DO_NOT_REPLACE_BLOCKS))
                    world.setBlockAndUpdate(pos, Blocks.IRON_BARS.defaultBlockState());
            }
        }
    }
}
