package eu.darkcode.client.widgets.config;

import eu.darkcode.client.widgets.OreElement;
import eu.darkcode.client.widgets.impl.OreLabel;
import eu.darkcode.client.widgets.impl.OreSwitch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class OreSwitchOption extends OreOption {

    private final @NotNull OreLabel nameLabel;
    private final @Nullable OreLabel descriptionLabel;

    private final @NotNull OreSwitch switchWidget;

    public OreSwitchOption(@NotNull Component name, @Nullable Component description, boolean value, @NotNull Consumer<Boolean> valueConsumer, boolean enabled) {
        nameLabel = addChild(new OreLabel(name, Minecraft.getInstance().font).setColor(0xFFFFFFFF));
        descriptionLabel = description == null ? null : addChild(new OreLabel(description, Minecraft.getInstance().font).setColor(0xFFAAAAAA));
        switchWidget = addChild(new OreSwitch(value, valueConsumer));
        switchWidget.setActive(enabled);
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
            descriptionLabel.setWidth(getWidth() - 44 - switchWidget.getWidth());
            descriptionLabel.setX(getX() + 18);
            descriptionLabel.setY(getY() + height);
            height += descriptionLabel.getHeight();
        }

        height += 12;

        setHeight(height);

        if (switchWidget.getHeight() + 16 > height)
            setHeight(switchWidget.getHeight() + 16);

        switchWidget.setX(getRight() - 18 - switchWidget.getWidth());
        switchWidget.setY(getY() + (getHeight() - switchWidget.getHeight()) / 2);
    }

    @Override
    public void render(GuiGraphics g, int i, int j, float f) {
        int right = getX() + getWidth();
        g.fill(getX(), getY(), right, getY() + 1, 0xFF5A5B5D);
        g.fill(getX(), getY() + 1, right, getY() + getHeight(), 0xFF48494B);
    }
}
