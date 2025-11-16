package eu.darkcode.event;

import com.mojang.serialization.Lifecycle;
import eu.darkcode.event.impl.HalfHeart;
import eu.darkcode.event.impl.RandomPlayerHeight;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

public class EventRegistry {

    private static final ResourceKey<? extends Registry<Entry<?>>> REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("betterchaosmod", "events"));
    public static final Registry<Entry<?>> EVENTS = bakeEvents();

    @Contract("-> new")
    private static Registry<Entry<?>> bakeEvents() {
        WritableRegistry<Entry<?>> registry = new MappedRegistry<>(REGISTRY_KEY, Lifecycle.stable());

        register(registry, RandomPlayerHeight.class);
        register(registry, HalfHeart.class);

        return FabricRegistryBuilder.from(registry).buildAndRegister();
    }

    private static <T extends Event> void register(@NotNull WritableRegistry<Entry<?>> registry, @NotNull Class<T> event) {
        EventDefinition definition = event.getAnnotation(EventDefinition.class);
        if (definition == null)
            throw new IllegalStateException("Tried to register event that is not properly defined! " + event.getName());

        ResourceKey<Entry<?>> key = ResourceKey.create(REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath("betterchaosmod", definition.key()));
        Entry<T> entry = createEntry(event, definition);

        registry.register(key, entry, RegistrationInfo.BUILT_IN);
    }

    private static <T extends Event> @NotNull Entry<T> createEntry(@NotNull Class<T> event, EventDefinition definition) {
        Constructor<T> constructor;
        try {
            constructor = event.getConstructor();
            constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Proper constructor not found!", e);
        }

        return new Entry<>(event, () -> {
            try {
                return constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }, definition.category());
    }

    public record Entry<T extends Event>(@NotNull Class<T> clazz, @NotNull Supplier<T> constructor,
                                         @NotNull EventCategory category) {
        @Contract(pure = true, value = "-> new")
        public T construct() {
            return constructor.get();
        }
    }
}
