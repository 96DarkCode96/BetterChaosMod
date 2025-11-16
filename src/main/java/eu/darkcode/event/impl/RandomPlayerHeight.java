package eu.darkcode.event.impl;

import eu.darkcode.event.AttributeEvent;
import eu.darkcode.event.EventCategory;
import eu.darkcode.event.EventDefinition;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@EventDefinition(key = "random_player_height", category = EventCategory.ATTRIBUTE)
public class RandomPlayerHeight extends AttributeEvent {
    public RandomPlayerHeight() {
        super(Map.of(
                Attributes.SCALE, p -> ThreadLocalRandom.current().nextInt(5, 30) / 10.0
        ));
    }
}
