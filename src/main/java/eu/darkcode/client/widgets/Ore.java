package eu.darkcode.client.widgets;

import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntConsumer;
import java.util.function.IntFunction;

public final class Ore {

    public static OreCategory category(@NotNull String name, @Nullable String description) {
        return new OreCategory(Component.translatable("betterchaosmod.option.category." + name),
                description != null ? Component.translatable("betterchaosmod.option.category." + description) : null);
    }

    public static OreSliderOption sliderOption(@NotNull String name, @Nullable String description, int value, int min, int max, @NotNull IntFunction<Component> formatter, @NotNull IntConsumer valueConsumer, boolean enabled) {
        return new OreSliderOption(
                Component.translatable("betterchaosmod.option." + name),
                description != null ? Component.translatable("betterchaosmod.option." + description) : null,
                value, min, max, formatter, valueConsumer, enabled
        );
    }
}
