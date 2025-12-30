package eu.darkcode.event.impl.health;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import static eu.darkcode.event.EventCategory.ATTRIBUTE;

public class RemoveHeart extends InstantEvent {
    public static EventRegistry.Entry<?> ENTRY = EventRegistry.entry("remove_heart", RemoveHeart::new).category(ATTRIBUTE).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            AttributeInstance attr = player.getAttribute(Attributes.MAX_HEALTH);
            if (attr == null)
                throw new IllegalStateException("Attribute " + Attributes.MAX_HEALTH + " wasn't found!");
            if (attr.getBaseValue() <= 2)
                return;
            attr.setBaseValue(attr.getBaseValue() - 2);
        });
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }

}
