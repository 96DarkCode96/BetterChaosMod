package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.TimedEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 02.01.2026
 **/
public class MeteorRain extends TimedEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("meteor_rain", MeteorRain::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        // ignored
    }

    @Override
    public void end(@NotNull EventManager manager) {
        // ignored
    }

    @Override
    public void tick(@NotNull EventManager manager) {
        if (currentTick() % 20 == 0) {
            manager.activePlayers().forEach(player -> {
                RandomSource random = player.getRandom();
                for (int i = 0; i < 7; i++) {
                    LargeFireball meteor = new LargeFireball(player.level(), player, new Vec3(0, -1d * (random.nextInt(4) + 1), 0), 4);
                    meteor.snapTo(player.getX() + (random.nextInt(100) - 50), player.getY() + 50 + (random.nextInt(10) - 5), player.getZ() + (random.nextInt(100) - 50), meteor.getYRot(), meteor.getXRot());
                    player.level().addFreshEntity(meteor);
                }
            });
        }
        super.tick(manager);
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
