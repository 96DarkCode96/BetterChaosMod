package eu.darkcode.event.impl;

import eu.darkcode.event.AttributeEvent;
import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static eu.darkcode.event.EventCategory.ATTRIBUTE;

/**
 * @author _dark_code_
 * @date 03.01.2026
 **/
public class LowGravity extends AttributeEvent {
    public static EventRegistry.Entry<?> ENTRY = EventRegistry.entry("low_gravity", LowGravity::new).category(ATTRIBUTE).build();

    public LowGravity() {
        super(Map.of(
                Attributes.GRAVITY, p -> 0.03
        ));
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }

}
