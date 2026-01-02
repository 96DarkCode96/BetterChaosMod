package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 02.01.2026
 **/
public class SlimeArmy extends InstantEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("slime_army", SlimeArmy::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            RandomSource random = player.getRandom();
            ServerLevel level = player.level();
            BlockPos position = player.blockPosition();

            int amount = random.nextInt(5) + 5;
            for (int i = 0; i < amount; i++) {
                EntityType.SLIME.spawn(level, position.offset(random.nextIntBetweenInclusive(-4, 4), random.nextInt(3), random.nextIntBetweenInclusive(-4, 4)), EntitySpawnReason.EVENT);
            }
        });
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
