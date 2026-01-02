package eu.darkcode.event.impl;

import eu.darkcode.ChaosTags;
import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.TimedEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 01.01.2026
 **/
public class Sandstorm extends TimedEvent {
    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("sandstorm", Sandstorm::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        // ignored
    }

    @Override
    public void tick(@NotNull EventManager manager) {
        super.tick(manager);
        manager.activePlayers().forEach(player -> {
            final BlockPos position = player.blockPosition();
            final ServerLevel world = player.level();
            final RandomSource random = player.getRandom();
            for (int i = 0; i < 20; i++) {
                BlockPos pos = position.offset(random.nextIntBetweenInclusive(-40, 40), 20, random.nextIntBetweenInclusive(-40, 40));
                if (!world.getBlockState(pos).is(ChaosTags.DO_NOT_REPLACE_BLOCKS))
                    world.setBlockAndUpdate(pos, Blocks.SAND.defaultBlockState());
            }
        });
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
