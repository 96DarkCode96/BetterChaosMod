package eu.darkcode.event.impl;

import eu.darkcode.ChaosTags;
import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.TimedEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 01.01.2026
 **/
public class Quicksand extends TimedEvent {
    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("quicksand", Quicksand::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        // ignored
    }

    @Override
    public void tick(@NotNull EventManager manager) {
        if (currentTick() % 40 == 0) {
            manager.activePlayers().forEach(player -> {
                final BlockPos position = player.blockPosition();
                final ServerLevel world = player.level();
                for (int x = -2; x <= 2; x++) {
                    for (int z = -2; z <= 2; z++) {
                        BlockPos pos = position.offset(x, -1, z);
                        if (!world.getBlockState(pos).is(ChaosTags.DO_NOT_REPLACE_BLOCKS))
                            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    }
                }
            });
        }
        super.tick(manager);
    }

    @Override
    public void end(@NotNull EventManager manager) {
        // ignored
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
