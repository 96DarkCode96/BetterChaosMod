package eu.darkcode.event.impl.health;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

import static eu.darkcode.event.EventCategory.ATTRIBUTE;

public class RandomPlayerHealth extends InstantEvent {
    public static EventRegistry.Entry<?> ENTRY = EventRegistry.entry("random_player_health", RandomPlayerHealth::new).category(ATTRIBUTE).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            AttributeInstance attr = player.getAttribute(Attributes.MAX_HEALTH);
            if (attr == null)
                throw new IllegalStateException("Attribute " + Attributes.MAX_HEALTH + " wasn't found!");
            int val = calc(Math.toIntExact(Math.round(attr.getBaseValue())));
            if (player.getHealth() > val)
                player.setHealth(val);
            attr.setBaseValue(val);
        });
    }

    private static int calc(int current) {
        int lowMax = current - 2;
        int highMin = current + 2;

        int lowRange = Math.max(0, lowMax - 10 + 1);
        int highRange = Math.max(0, 30 - highMin + 1);

        int r = ThreadLocalRandom.current().nextInt(lowRange + highRange);
        return r < lowRange ? 10 + r : highMin + (r - lowRange);
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }

}
