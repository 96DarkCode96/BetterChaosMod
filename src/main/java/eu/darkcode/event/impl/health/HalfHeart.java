package eu.darkcode.event.impl.health;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.TimedEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static eu.darkcode.event.EventCategory.ATTRIBUTE;

public class HalfHeart extends TimedEvent {
    public static EventRegistry.Entry<?> ENTRY = EventRegistry.entry("half_heart", HalfHeart::new).category(ATTRIBUTE).build();
    private final @NotNull Map<ServerPlayer, Entry> rollback = new HashMap<>();

    private static void resetHealth(@NotNull ServerPlayer player, Entry entry) {
        AttributeInstance attribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (attribute == null)
            throw new IllegalStateException("Attribute " + Attributes.MAX_HEALTH + " wasn't found!");
        attribute.setBaseValue(entry.maxHealth);
        player.setHealth(entry.health);
    }

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            AttributeInstance attr = player.getAttribute(Attributes.MAX_HEALTH);
            if (attr == null)
                throw new IllegalStateException("Attribute " + Attributes.MAX_HEALTH + " wasn't found!");
            rollback.put(player, new Entry(attr.getBaseValue(), player.getHealth()));
            player.setHealth(1);
            attr.setBaseValue(1);
        });
    }

    @Override
    public void end(@NotNull EventManager manager) {
        rollback.forEach(HalfHeart::resetHealth);
        rollback.clear();
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }

    @Override
    public void endPlayer(@NotNull EventManager manager, @NotNull ServerPlayer player) {
        Entry entry = rollback.remove(player);
        if (entry == null)
            return;
        resetHealth(player, entry);
    }

    private record Entry(double maxHealth, float health) {
    }
}
