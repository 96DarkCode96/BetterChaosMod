package eu.darkcode.client.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntConsumer;
import java.util.function.IntFunction;

public class OreSliderOption extends LinearLayout implements Renderable {

    private final @NotNull OreLabel valueLabel;

    public OreSliderOption(@NotNull Component name, @Nullable Component description, int value, int min, int max, @NotNull IntFunction<Component> formatter, @NotNull IntConsumer valueConsumer, boolean enabled) {
        super(0, 0, Orientation.VERTICAL);
        spacing(4).defaultCellSetting().alignHorizontallyLeft().paddingHorizontal(18);

        LinearLayout row = addChild(LinearLayout.horizontal().spacing(4));
        row.defaultCellSetting().paddingTop(13);

        row.addChild(new OreLabel(name, Minecraft.getInstance().font), LayoutSettings::alignHorizontallyLeft).setColor(0xFFFFFFFF);
        valueLabel = row.addChild(new OreLabel(formatter.apply(value), Minecraft.getInstance().font), LayoutSettings::alignHorizontallyRight).setColor(0xFFFFFFFF);

        if (description != null)
            addChild(new OreLabel(description, Minecraft.getInstance().font)).setColor(0xFFAAAAAA);
        addChild(new OreSlider(value, min, max, val -> {
            valueConsumer.accept(val);
            valueLabel.setMessage(formatter.apply(val));
        }), a -> a.paddingBottom(8).paddingTop(4)).active = enabled;
    }

    @Override
    public void arrangeElements() {
        super.arrangeElements();
        valueLabel.setX(getX() + getWidth() - valueLabel.getWidth() - 18);
    }

    @Override
    public void render(GuiGraphics g, int i, int j, float f) {
        int right = getX() + getWidth();
        g.fill(getX(), getY(), right, getY() + 1, 0xFF5A5B5D);
        g.fill(getX(), getY() + 1, right, getY() + getHeight(), 0xFF48494B);
    }
}
