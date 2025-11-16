package eu.darkcode.event;

import eu.darkcode.server.EventManager;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class AttributeEvent extends TimedEvent {

    private final @NotNull Map<Holder<Attribute>, Function<ServerPlayer, Double>> attributes;
    private final @NotNull Map<ServerPlayer, Map<Holder<Attribute>, Double>> rollbackMap = new HashMap<>();

    protected AttributeEvent(@NotNull Map<Holder<Attribute>, Function<ServerPlayer, Double>> attributes) {
        this.attributes = attributes;
    }

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> attributes.forEach((attribute, newValue) -> {
            AttributeInstance attr = player.getAttribute(attribute);
            if (attr == null)
                throw new IllegalStateException("Attribute " + attribute + " wasn't found!");
            double rollBackValue = attr.getBaseValue();
            rollbackMap.computeIfAbsent(player, a -> new HashMap<>()).put(attribute, rollBackValue);
            attr.setBaseValue(newValue.apply(player));
        }));
    }

    @Override
    public void tick(@NotNull EventManager manager) {
        // ignored
    }

    @Override
    public void end(@NotNull EventManager manager) {
        rollbackMap.forEach((serverPlayer, map) -> {
            map.forEach((attr, rollBackValue) -> {
                AttributeInstance attribute = serverPlayer.getAttribute(attr);
                if (attribute == null)
                    throw new IllegalStateException("Attribute " + attr + " wasn't found!");
                attribute.setBaseValue(rollBackValue);
            });
        });
        rollbackMap.clear();
    }

    @Override
    public void endPlayer(@NotNull EventManager manager, @NotNull ServerPlayer player) {
        Map<Holder<Attribute>, Double> remove = rollbackMap.remove(player);
        if (remove == null)
            return;
        remove.forEach((attr, rollBackValue) -> {
            AttributeInstance attribute = player.getAttribute(attr);
            if (attribute == null)
                throw new IllegalStateException("Attribute " + attr + " wasn't found!");
            attribute.setBaseValue(rollBackValue);
        });
    }
}