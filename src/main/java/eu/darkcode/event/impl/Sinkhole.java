package eu.darkcode.event.impl;

import eu.darkcode.ChaosTags;
import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 01.01.2026
 **/
public class Sinkhole extends InstantEvent {
    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("sinkhole", Sinkhole::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            final BlockPos position = player.blockPosition();
            final ServerLevel world = player.level();
            for (int y = 3; y > -30; y--) {
                BlockState blockState = (y < -28 ? Blocks.WATER : Blocks.AIR).defaultBlockState();
                for (int x = -2; x <= 2; x++) {
                    for (int z = -2; z <= 2; z++) {
                        BlockPos pos = position.offset(x, y, z);
                        if (!world.getBlockState(pos).is(ChaosTags.DO_NOT_REPLACE_BLOCKS))
                            world.setBlockAndUpdate(pos, blockState);
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
