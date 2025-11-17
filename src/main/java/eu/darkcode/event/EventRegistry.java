package eu.darkcode.event;

import com.mojang.serialization.Lifecycle;
import eu.darkcode.event.impl.*;
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

import java.util.function.Supplier;

public class EventRegistry {

    private static final ResourceKey<Registry<Entry<?>>> REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("betterchaosmod", "events"));
    public static final StreamCodec<RegistryFriendlyByteBuf, Event> CODEC = ByteBufCodecs.registry(REGISTRY_KEY).dispatch(Event::entry, Entry::codec);
    public static final Registry<Entry<?>> EVENTS = bakeEvents();

    @Contract("-> new")
    private static Registry<Entry<?>> bakeEvents() {
        WritableRegistry<Entry<?>> registry = new MappedRegistry<>(REGISTRY_KEY, Lifecycle.stable());

        register(registry, RandomPlayerHeight.ENTRY);
        register(registry, HalfHeart.ENTRY);
        register(registry, Adventure.ENTRY);
        register(registry, DropInventory.ENTRY);
        register(registry, DropItemInHand.ENTRY);

        return FabricRegistryBuilder.from(registry).buildAndRegister();
    }

    private static <T extends Event> void register(@NotNull WritableRegistry<Entry<?>> registry, @NotNull Entry<T> entry) {
        registry.register(ResourceKey.create(REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath("betterchaosmod", entry.key)), entry, RegistrationInfo.BUILT_IN);
    }

    public static <T extends Event> Builder<T> entry(@NotNull String key, @NotNull Supplier<T> constructor) {
        return new Builder<>(key, constructor);
    }

    public record Entry<T extends Event>(@NotNull String key, @NotNull Supplier<T> constructor,
                                         @NotNull StreamCodec<RegistryFriendlyByteBuf, Event> codec) {
        @Contract(pure = true, value = "-> new")
        public T construct() {
            return constructor.get();
        }
    }

    public static class Builder<T extends Event> {
        private final @NotNull String key;
        private final @NotNull Supplier<T> constructor;
        private @Nullable StreamCodec<RegistryFriendlyByteBuf, Event> codec;

        private Builder(@NotNull String key, @NotNull Supplier<T> constructor) {
            this.key = key;
            this.constructor = constructor;
        }

        @Contract("_ -> this")
        public Builder<T> codec(@Nullable StreamCodec<RegistryFriendlyByteBuf, Event> codec) {
            this.codec = codec;
            return this;
        }

        @Contract("-> new")
        public Entry<T> build() {
            if (codec == null) {
                codec = new StreamCodec<>() {
                    @Override
                    public Event decode(RegistryFriendlyByteBuf object) {
                        return constructor.get();
                    }

                    @Override
                    public void encode(RegistryFriendlyByteBuf object, Event object2) {
                        // ignored
                    }
                };
            }
            return new Entry<>(key, constructor, codec);
        }
    }
}
