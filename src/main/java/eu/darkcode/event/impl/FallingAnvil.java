package eu.darkcode.event.impl;

import eu.darkcode.ChaosTags;
import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public class FallingAnvil extends InstantEvent {

    public static EventRegistry.Entry<?> ENTRY = EventRegistry.entry("falling_anvil", FallingAnvil::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            final ServerLevel world = player.level();
            final int x = player.getBlockX();
            final int y = player.getBlockY();
            final int z = player.getBlockZ();
            for (int j = -1; j < 2; j++) {
                for (int k = -1; k < 2; k++) {
                    for (int l = 1; l < 11; l++) {
                        BlockPos pos = new BlockPos(x + j, y + l, z + k);
                        if (!world.getBlockState(pos).is(ChaosTags.DO_NOT_REPLACE_BLOCKS))
                            world.setBlockAndUpdate(pos, (l == 10 ? Blocks.ANVIL : Blocks.AIR).defaultBlockState());
                    }
                }
            }
        });
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
