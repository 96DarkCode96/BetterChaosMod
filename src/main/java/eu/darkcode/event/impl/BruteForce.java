package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 05.01.2026
 **/
public class BruteForce extends InstantEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("brute_force", BruteForce::new).build();

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            RandomSource random = player.getRandom();
            ServerLevel level = player.level();
            BlockPos position = player.blockPosition();

            int amount = random.nextIntBetweenInclusive(1, 3); // 1-3
            for (int i = 0; i < amount; i++) {
                PiglinBrute brute = EntityType.PIGLIN_BRUTE.spawn(level, position.offset(random.nextIntBetweenInclusive(-4, 4), random.nextInt(2), random.nextIntBetweenInclusive(-4, 4)), EntitySpawnReason.EVENT);
                if (brute != null) {
                    brute.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, -1, 0, false, false, false));
                    brute.setImmuneToZombification(true);
                }
            }
        });
    }
}
