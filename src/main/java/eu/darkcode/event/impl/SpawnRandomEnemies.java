package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author _dark_code_
 * @date 01.01.2026
 **/
public class SpawnRandomEnemies extends InstantEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("spawn_random_enemies", SpawnRandomEnemies::new).build();

    public static final List<EntityType<? extends Monster>> ENEMY_TYPES = List.of(
            EntityTypes.SKELETON,
            EntityTypes.SKELETON,
            EntityTypes.SKELETON,
            EntityTypes.SKELETON,
            EntityTypes.SKELETON,
            EntityTypes.ZOMBIE,
            EntityTypes.ZOMBIE,
            EntityTypes.ZOMBIE,
            EntityTypes.ZOMBIE,
            EntityTypes.ZOMBIE,
            EntityTypes.CREEPER,
            EntityTypes.CREEPER,
            EntityTypes.CREEPER,
            EntityTypes.CREEPER,
            EntityTypes.CREEPER,
            EntityTypes.ENDERMAN,
            EntityTypes.ENDERMAN,
            EntityTypes.WITCH,
            EntityTypes.WITCH,
            EntityTypes.BLAZE,
            EntityTypes.BOGGED,
            EntityTypes.BREEZE,
            EntityTypes.BREEZE,
            EntityTypes.EVOKER,
            EntityTypes.EVOKER,
            EntityTypes.ENDERMITE,
            EntityTypes.ENDERMITE,
            EntityTypes.ILLUSIONER,
            EntityTypes.ILLUSIONER,
            EntityTypes.VEX,
            EntityTypes.HUSK,
            EntityTypes.HUSK,
            EntityTypes.HUSK,
            EntityTypes.WITHER_SKELETON,
            EntityTypes.WITHER_SKELETON,
            EntityTypes.VINDICATOR,
            EntityTypes.VINDICATOR,
            EntityTypes.SPIDER,
            EntityTypes.SPIDER,
            EntityTypes.SPIDER
    );

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            RandomSource random = player.getRandom();
            int max = random.nextInt(4, 20);
            for (int i = 0; i < max; i++) {
                ENEMY_TYPES.get(random.nextInt(ENEMY_TYPES.size())).spawn(player.level(), monster -> {
                            if (!(monster instanceof Creeper))
                                monster.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, -1, 1, false, false, false));
                        },
                        player.blockPosition().offset(random.nextIntBetweenInclusive(-4, 4), random.nextInt(2), random.nextIntBetweenInclusive(-4, 4)),
                        EntitySpawnReason.EVENT, false, false);
            }
        });
    }
}
