package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 02.01.2026
 **/
public class KillerBunny extends InstantEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("killer_bunny", KillerBunny::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            RandomSource random = player.getRandom();
            int count = random.nextIntBetweenInclusive(3, 6);

            for (int i = 0; i < count; i++) {
                BlockPos pos = player.blockPosition().offset(random.nextIntBetweenInclusive(-4, 4), random.nextInt(2), random.nextIntBetweenInclusive(-4, 4));
                EntityTypes.RABBIT.spawn(player.level(), rabbit -> rabbit.setVariant(Rabbit.Variant.EVIL), pos, EntitySpawnReason.EVENT, false, false);
            }
        });
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
