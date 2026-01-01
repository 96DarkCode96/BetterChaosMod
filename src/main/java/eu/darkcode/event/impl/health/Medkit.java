package eu.darkcode.event.impl.health;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.world.food.FoodData;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 01.01.2026
 **/
public class Medkit extends InstantEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("medkit", Medkit::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player ->{
            player.clearFire();
            player.clearFreeze();
            player.setHealth(player.getMaxHealth());
            FoodData foodData = player.getFoodData();
            foodData.setFoodLevel(20);
            foodData.setSaturation(3.5f);
        });
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
