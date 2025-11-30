package eu.darkcode.client.widgets;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntConsumer;
import java.util.function.IntFunction;

public final class Ore {

    public static final OreTextInput.TextFormatter PASSWORD_FORMATTER = new OreTextInput.TextFormatter() {
        @Override
        public @NotNull FormattedCharSequence format(String string, int i) {
            return FormattedCharSequence.forward("*".repeat(string.length()), Style.EMPTY.withColor(0xFFFFFFFF).withShadowColor(0));
        }
    };

    public static OreSliderOption sliderOption(@NotNull String name, @Nullable String description, int value, int min, int max, @NotNull IntFunction<Component> formatter, @NotNull IntConsumer valueConsumer, boolean enabled) {
        return new OreSliderOption(
                Component.translatable("betterchaosmod.option." + name),
                description != null ? Component.translatable("betterchaosmod.option." + description) : null,
                value, min, max, formatter, valueConsumer, enabled
        );
    }
}
