package eu.darkcode.client.widgets.config;

import eu.darkcode.client.widgets.OreElement;
import eu.darkcode.client.widgets.impl.OreLabel;
import eu.darkcode.client.widgets.impl.OreSlider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;

public class OreSliderOption extends OreOption {

    private final @NotNull OreLabel nameLabel;
    private final @NotNull OreLabel valueLabel;
    private final @Nullable OreLabel descriptionLabel;
    private final @NotNull OreSlider slider;

    public OreSliderOption(@NotNull Component name, @Nullable Component description, int value, int min, int max, @NotNull IntFunction<Component> formatter, @NotNull IntConsumer valueConsumer, boolean enabled) {

        nameLabel = addChild(new OreLabel(name, Minecraft.getInstance().font).setColor(0xFFFFFFFF));
        valueLabel = addChild(new OreLabel(formatter.apply(value), Minecraft.getInstance().font).setColor(0xFFFFFFFF));
        descriptionLabel = description == null ? null : addChild(new OreLabel(description, Minecraft.getInstance().font).setColor(0xFFAAAAAA));
        slider = addChild(new OreSlider(value, min, max, val -> {
            valueConsumer.accept(val);
            valueLabel.setTextAndWidth(formatter.apply(val));
            valueLabel.setX(getRight() - valueLabel.getWidth() - 18);
        }));
        slider.setActive(enabled);
    }

    @Override
    public void visitChildren(@NotNull Consumer<OreElement> consumer) {
        this.children.forEach(consumer);
    }

    @Override
    public void arrangeElements() {
        super.arrangeElements();

        width = slider.getWidth() + 36;
        int height = 12;

        valueLabel.setX(getRight() - valueLabel.getWidth() - 18);
        valueLabel.setY(getY() + 12);
        nameLabel.setX(getX() + 18);
        nameLabel.setY(getY() + 12);
        nameLabel.setWidth(slider.getWidth() - valueLabel.getWidth() - 4);

        height += nameLabel.getHeight();

        if (descriptionLabel != null) {
            height += 4;
            descriptionLabel.setX(getX() + 18);
            descriptionLabel.setY(getY() + height);
            descriptionLabel.setWidth(slider.getWidth());
            height += descriptionLabel.getHeight();
        }

        height += slider.getHeight() + 16;

        setHeight(height);

        slider.setX(getX() + 18);
        slider.setY(getBottom() - slider.getHeight() - 8);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int i, int j, float f) {
        int right = getX() + getWidth();
        g.fill(getX(), getY(), right, getY() + 1, 0xFF5A5B5D);
        g.fill(getX(), getY() + 1, right, getY() + getHeight(), 0xFF48494B);
    }
}
