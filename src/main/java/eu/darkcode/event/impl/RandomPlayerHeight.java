package eu.darkcode.event.impl;

import eu.darkcode.event.AttributeEvent;
import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class RandomPlayerHeight extends AttributeEvent {

    public static EventRegistry.Entry<?> ENTRY = EventRegistry.entry("random_player_height", RandomPlayerHeight::new).build();

    public RandomPlayerHeight() {
        super(Map.of(
                Attributes.SCALE, p -> ThreadLocalRandom.current().nextInt(5, 30) / 10.0
        ));
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
