package eu.darkcode.event.impl;

import eu.darkcode.event.AttributeEvent;
import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static eu.darkcode.event.EventCategory.ATTRIBUTE;

public class FastAndFurious extends AttributeEvent {

    public static EventRegistry.Entry<?> ENTRY = EventRegistry.entry("fast_and_furious", FastAndFurious::new).category(ATTRIBUTE).build();

    public FastAndFurious() {
        super(Map.of(
                Attributes.MOVEMENT_SPEED, p -> 0.5d
        ));
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
