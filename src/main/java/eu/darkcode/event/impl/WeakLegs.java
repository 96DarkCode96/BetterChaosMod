package eu.darkcode.event.impl;

import eu.darkcode.event.AttributeEvent;
import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static eu.darkcode.event.EventCategory.ATTRIBUTE;

public class WeakLegs extends AttributeEvent {

    public static EventRegistry.Entry<?> ENTRY = EventRegistry.entry("weak_legs", WeakLegs::new).category(ATTRIBUTE).build();

    public WeakLegs() {
        super(Map.of(
                Attributes.FALL_DAMAGE_MULTIPLIER, p -> 3d,
                Attributes.SAFE_FALL_DISTANCE, p -> 1d
        ));
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }

    @Override
    public int duration() {
        return (int) (super.duration() * 1.25d);
    }
}
