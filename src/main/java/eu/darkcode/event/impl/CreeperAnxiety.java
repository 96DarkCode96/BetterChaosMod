package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.TimedEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 02.01.2026
 **/
public class CreeperAnxiety extends TimedEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("creeper_anxiety", CreeperAnxiety::new).build();

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
        if (currentTick() % 60 == 0) {
            manager.activePlayers().forEach(player -> {
                RandomSource random = player.getRandom();
                if (random.nextInt(10) >= 5)
                    EntityTypes.CREEPER.spawn(player.level(), player.blockPosition().north(), EntitySpawnReason.EVENT);
                player.connection.send(new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.CREEPER_PRIMED), SoundSource.HOSTILE, player.getX(), player.getY(), player.getZ(), 1f, 0.5f, random.nextLong()));
            });
        }
        super.tick(manager);
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
