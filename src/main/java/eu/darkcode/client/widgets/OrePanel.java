package eu.darkcode.client.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;

public class OrePanel extends LinearLayout implements Renderable {

    private boolean first = true;

    public OrePanel() {
        super(0, 0, Orientation.VERTICAL);
        defaultCellSetting().alignHorizontallyLeft().paddingHorizontal(1).paddingBottom(1);
    }

    @Override
    public void render(GuiGraphics g, int i, int j, float f) {
        int right = getX() + getWidth();
        int bottom = getY() + getHeight();
        g.fill(getX(), getY(), right, bottom, 0xFF1E1E1F);
        g.fill(getX() + 1, getY() + 1, right - 1, bottom - 1, 0xFF313233);
    }

    @NotNull
    public OreCategory addCategory(@NotNull String name, @Nullable String description) {
        return addChild(new OreCategory(this,
                        Component.translatable("betterchaosmod.option.category." + name),
                        description != null ? Component.translatable("betterchaosmod.option.category." + description) : null),
                a -> a.paddingBottom(0));
    }

    @NotNull
    public OreSliderOption addSlider(@NotNull String name, @Nullable String description, int value, int min, int max, @NotNull IntFunction<Component> formatter, @NotNull IntConsumer valueConsumer, boolean enabled) {
        return addChild(Ore.sliderOption(name, description, value, min, max, formatter, valueConsumer, enabled));
    }

    @NotNull
    public OreInputOption addTextInput(@NotNull String name, @Nullable String description, @NotNull String text, @NotNull Consumer<String> valueConsumer, boolean enabled) {
        return addChild(new OreInputOption(
                Component.translatable("betterchaosmod.option." + name),
                description != null ? Component.translatable("betterchaosmod.option." + description) : null,
                text, valueConsumer, enabled
        ));
    }

    @Override
    public <T extends LayoutElement> T addChild(T layoutElement, LayoutSettings layoutSettings) {
        if (!first)
            return super.addChild(layoutElement, layoutSettings);
        first = false;
        layoutSettings.getExposed().paddingTop++;
        return super.addChild(layoutElement, layoutSettings);
    }

    @Override
    public <T extends LayoutElement> T addChild(T layoutElement, Consumer<LayoutSettings> consumer) {
        if (!first)
            return super.addChild(layoutElement, consumer);
        first = false;
        return super.addChild(layoutElement, a -> {
            consumer.accept(a);
            a.getExposed().paddingTop++;
        });
    }
}