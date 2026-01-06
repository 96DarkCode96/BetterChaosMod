package eu.darkcode.event;

import com.mojang.serialization.Lifecycle;
import eu.darkcode.Settings;
import eu.darkcode.event.impl.*;
import eu.darkcode.event.impl.health.*;
import eu.darkcode.event.impl.move.*;
import eu.darkcode.event.impl.sensitivity.HighSensitivity;
import eu.darkcode.event.impl.sensitivity.LowSensitivity;
import eu.darkcode.event.impl.sensitivity.MouseDrifting;
import eu.darkcode.event.impl.teleport.*;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class EventRegistry {

    public static final ResourceKey<Registry<Entry<?>>> REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("betterchaosmod", "events"));
    public static final StreamCodec<RegistryFriendlyByteBuf, Event> CODEC = ByteBufCodecs.registry(REGISTRY_KEY).dispatch(Event::entry, Entry::codec);
    public static final Registry<Entry<?>> EVENTS = bakeEvents();

    @Contract("-> new")
    private static Registry<Entry<?>> bakeEvents() {
        WritableRegistry<Entry<?>> registry = new MappedRegistry<>(REGISTRY_KEY, Lifecycle.stable());

        register(registry, AddHeart.ENTRY);
        register(registry, Adventure.ENTRY);
        register(registry, AppleMania.ENTRY);
        register(registry, Blaze.ENTRY);
        register(registry, Blind.ENTRY);
        register(registry, BruteForce.ENTRY);
        register(registry, CageTrap.ENTRY);
        register(registry, Cobweb.ENTRY);
        register(registry, Creeper.ENTRY);
        register(registry, CreeperAnxiety.ENTRY);
        register(registry, CurseArmor.ENTRY);
        register(registry, Czechcloud.ENTRY);
        register(registry, DamageItems.ENTRY);
        register(registry, Deaf.ENTRY);
        register(registry, DropInventory.ENTRY);
        register(registry, DropItemInHand.ENTRY);
        register(registry, ExplodeNearbyEntities.ENTRY);
        register(registry, FakeTeleport.ENTRY);
        register(registry, FallingAnvil.ENTRY);
        register(registry, FallingLava.ENTRY);
        register(registry, FastAndFurious.ENTRY);
        register(registry, FixItems.ENTRY);
        register(registry, ForceForwards.ENTRY);
        register(registry, ForceBackwards.ENTRY);
        register(registry, ForceJump.ENTRY);
        register(registry, ForceSneak.ENTRY);
        register(registry, GiveRandomItems.ENTRY);
        register(registry, GlowingMobs.ENTRY);
        register(registry, HalfHeart.ENTRY);
        register(registry, HighPitch.ENTRY);
        register(registry, HighSensitivity.ENTRY);
        register(registry, HungerGames.ENTRY);
        register(registry, InvertedControls.ENTRY);
        register(registry, Invulnerability.ENTRY);
        register(registry, Jumpscare.ENTRY);
        register(registry, KillerBunny.ENTRY);
        register(registry, Lava.ENTRY);
        register(registry, Levitation.ENTRY);
        register(registry, LowGravity.ENTRY);
        register(registry, LowPitch.ENTRY);
        register(registry, LowSensitivity.ENTRY);
        register(registry, MadSecurity.ENTRY);
        register(registry, Medkit.ENTRY);
        register(registry, MeteorRain.ENTRY);
        register(registry, MidasTouch.ENTRY);
        register(registry, MiningFatigue.ENTRY);
        register(registry, MobRevolution.ENTRY);
        register(registry, MouseDrifting.ENTRY);
        register(registry, NoDrops.ENTRY);
        register(registry, NoJump.ENTRY);
        register(registry, NoMove.ENTRY);
        register(registry, NoSneak.ENTRY);
        register(registry, OnlyBackwards.ENTRY);
        register(registry, OnlySideways.ENTRY);
        register(registry, OrichalcumTouch.ENTRY);
        register(registry, PotatoPC.ENTRY);
        register(registry, Quicksand.ENTRY);
        register(registry, RandomDrops.ENTRY);
        register(registry, RandomPlayerHealth.ENTRY);
        register(registry, RandomPlayerHeight.ENTRY);
        register(registry, RandomPotionEffect.ENTRY);
        register(registry, RemoveHeart.ENTRY);
        register(registry, RemoveEnchantments.ENTRY);
        register(registry, RemoveRandomItem.ENTRY);
        register(registry, Resistance.ENTRY);
        register(registry, RideClosestMob.ENTRY);
        register(registry, RollCredits.ENTRY);
        register(registry, Sandstorm.ENTRY);
        register(registry, ShuffleInventory.ENTRY);
        register(registry, Sinkhole.ENTRY);
        register(registry, SlimeArmy.ENTRY);
        register(registry, SpawnRandomEnemies.ENTRY);
        register(registry, StarterPack.ENTRY);
        register(registry, SwapHands.ENTRY);

        register(registry, TeleportHeaven.ENTRY);
        register(registry, TeleportNearbyEntities.ENTRY);
        register(registry, TeleportRandom.ENTRY);
        register(registry, TeleportSky.ENTRY);
        register(registry, TeleportSpawn.ENTRY);
        register(registry, TimeToFly.ENTRY);

        register(registry, TNT.ENTRY);
        register(registry, Warden.ENTRY);
        register(registry, WeakLegs.ENTRY);
        register(registry, WhereIsMyWorld.ENTRY);
        register(registry, Wither.ENTRY);

        return FabricRegistryBuilder.from(registry).buildAndRegister();
    }

    private static <T extends Event> void register(@NotNull WritableRegistry<Entry<?>> registry, @NotNull Entry<T> entry) {
        registry.register(ResourceKey.create(REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath("betterchaosmod", entry.key)), entry, RegistrationInfo.BUILT_IN);
    }

    public static <T extends Event> Builder<T> entry(@NotNull String key, @NotNull Supplier<T> constructor) {
        return new Builder<>(key, constructor);
    }

    @Nullable
    public static Entry<?> pickRandomEvent(@NotNull Predicate<Entry<?>> filter) {
        List<Entry<?>> candidates = EVENTS.stream().filter(filter).toList();

        // Return null if no candidates
        if (candidates.isEmpty())
            return null;

        // Pick a random entry
        return candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
    }

    public record Entry<T extends Event>(@NotNull String key, @NotNull Supplier<T> constructor,
                                         @NotNull StreamCodec<RegistryFriendlyByteBuf, T> codec,
                                         @Nullable EventCategory category, @NotNull EventServerType serverType,
                                         boolean disableAccessibility) {
        @Contract(pure = true, value = "-> new")
        public T construct() {
            return constructor.get();
        }

        public boolean enabled(@NotNull Settings settings) {
            return !disabledByAccessibility(settings) && !settings.disabledEvents.contains(key);
        }

        public boolean disabledByAccessibility(@NotNull Settings settings) {
            return disableAccessibility && settings.disableAccessibility;
        }
    }

    public static class Builder<T extends Event> {
        private final @NotNull String key;
        private final @NotNull Supplier<T> constructor;
        private @Nullable StreamCodec<RegistryFriendlyByteBuf, T> codec;
        private @Nullable EventCategory category;
        private @NotNull EventServerType eventServerType = EventServerType.BOTH;
        private boolean disableAccessibility = false;

        private Builder(@NotNull String key, @NotNull Supplier<T> constructor) {
            this.key = key;
            this.constructor = constructor;
        }

        @Contract("_ -> this")
        public Builder<T> codec(@Nullable StreamCodec<RegistryFriendlyByteBuf, T> codec) {
            this.codec = codec;
            return this;
        }

        @Contract("_ -> this")
        public Builder<T> category(@Nullable EventCategory category) {
            this.category = category;
            return this;
        }

        @Contract("_ -> this")
        public Builder<T> eventServerType(@NotNull EventServerType eventServerType) {
            this.eventServerType = eventServerType;
            return this;
        }

        @Contract("-> this")
        public Builder<T> disableAccessibility() {
            this.disableAccessibility = true;
            return this;
        }

        @Contract("-> new")
        public Entry<T> build() {
            if (codec == null) {
                codec = new StreamCodec<>() {
                    @Override
                    public T decode(RegistryFriendlyByteBuf object) {
                        return constructor.get();
                    }

                    @Override
                    public void encode(RegistryFriendlyByteBuf object, Event object2) {
                        // ignored
                    }
                };
            }
            return new Entry<>(key, constructor, codec, category, eventServerType, disableAccessibility);
        }
    }
}
