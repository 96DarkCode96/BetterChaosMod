package eu.darkcode.event.impl;

import eu.darkcode.BetterChaosMod;
import eu.darkcode.event.Event;
import eu.darkcode.event.EventCategory;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author _dark_code_
 * @date 02.01.2026
 **/
public class RandomPotionEffect extends InstantEvent {
    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("random_potion_effect", RandomPotionEffect::new).category(EventCategory.EFFECT).build();
    public static final List<Holder<MobEffect>> EFFECTS = List.of(
            MobEffects.MINING_FATIGUE,
            MobEffects.NIGHT_VISION,
            MobEffects.HASTE,
            MobEffects.RESISTANCE,
            MobEffects.DOLPHINS_GRACE,
            MobEffects.FIRE_RESISTANCE,
            MobEffects.BLINDNESS,
            MobEffects.DARKNESS,
            MobEffects.STRENGTH,
            MobEffects.WEAKNESS,
            MobEffects.SLOWNESS,
            MobEffects.WITHER,
            MobEffects.NAUSEA,
            MobEffects.POISON,
            MobEffects.LEVITATION
    );

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            int tries = 10;
            RandomSource random = player.getRandom();
            int duration = (int) (BetterChaosMod.instance().settings().eventDuration * 1.5);
            while (!player.addEffect(new MobEffectInstance(EFFECTS.get(random.nextInt(EFFECTS.size())), duration)) && tries-- > 0) {
            }
        });
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
