package eu.darkcode.client.widgets.config;

import eu.darkcode.client.widgets.OreElement;
import eu.darkcode.client.widgets.impl.OreColor;
import eu.darkcode.client.widgets.impl.OreLabel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class OreColorOption extends OreOption {

    private final @NotNull OreLabel nameLabel;
    private final @Nullable OreLabel descriptionLabel;
    private final @NotNull OreColor colorWidget;

    public OreColorOption(@NotNull Component name, @Nullable Component description, int colorValue, @NotNull IntConsumer valueConsumer, boolean enabled) {
        nameLabel = addChild(new OreLabel(name, Minecraft.getInstance().font).setColor(0xFFFFFFFF));
        descriptionLabel = description == null ? null : addChild(new OreLabel(description, Minecraft.getInstance().font).setColor(0xFFAAAAAA));
        colorWidget = addChild(new OreColor(colorValue, valueConsumer));
        colorWidget.setActive(enabled);
    }

    @Override
    public void visitChildren(@NotNull Consumer<OreElement> consumer) {
        this.children.forEach(consumer);
    }

    @Override
    public void arrangeElements() {
        super.arrangeElements();

        int height = 12;

        nameLabel.setX(getX() + 18);
        nameLabel.setY(getY() + 12);

        height += nameLabel.getHeight();

        if (descriptionLabel != null) {
            height += 4;
            descriptionLabel.setX(getX() + 18);
            descriptionLabel.setY(getY() + height);
            height += descriptionLabel.getHeight();
        }

        height += 12;

        setHeight(height);

        if (colorWidget.getHeight() + 24 > height)
            setHeight(colorWidget.getHeight() + 24);

        colorWidget.setX(getRight() - 18 - colorWidget.getWidth());
        colorWidget.setY(getY() + (getHeight() - colorWidget.getHeight()) / 2);
    }

    @Override
    public void render(GuiGraphics g, int i, int j, float f) {
        int right = getX() + getWidth();
        g.fill(getX(), getY(), right, getY() + 1, 0xFF5A5B5D);
        g.fill(getX(), getY() + 1, right, getY() + getHeight(), 0xFF48494B);
    }
}
