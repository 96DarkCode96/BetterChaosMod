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

/**
 * @author _dark_code_
 * @date 02.01.2026
 **/
public class Cobweb extends InstantEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("cobweb", Cobweb::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            ServerLevel level = player.level();
            BlockPos blockPos = player.blockPosition();
            if (!level.getBlockState(blockPos).is(ChaosTags.DO_NOT_REPLACE_BLOCKS))
                level.setBlockAndUpdate(blockPos, Blocks.COBWEB.defaultBlockState());
        });
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
