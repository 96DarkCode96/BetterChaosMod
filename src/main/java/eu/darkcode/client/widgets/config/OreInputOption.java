package eu.darkcode.client.widgets.config;

import eu.darkcode.client.widgets.OreElement;
import eu.darkcode.client.widgets.impl.OreLabel;
import eu.darkcode.client.widgets.impl.OreTextInput;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class OreInputOption extends OreOption {

    private final @NotNull OreLabel nameLabel;
    private final @Nullable OreLabel descriptionLabel;
    private final @NotNull OreTextInput oreTextInput;

    public OreInputOption(@NotNull Component name, @Nullable Component description, @NotNull String text, @NotNull Consumer<String> valueConsumer, boolean enabled) {
        this.nameLabel = addChild(new OreLabel(name, Minecraft.getInstance().font).setColor(0xFFFFFFFF));
        this.oreTextInput = addChild(new OreTextInput(text, valueConsumer));
        this.oreTextInput.setActive(enabled);

        descriptionLabel = description == null ? null : addChild(new OreLabel(description, Minecraft.getInstance().font).setColor(0xFFAAAAAA));
    }

    public @NotNull OreTextInput textInput() {
        return oreTextInput;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int i, int j, float f) {
        int right = getX() + getWidth();
        g.fill(getX(), getY(), right, getY() + 1, 0xFF5A5B5D);
        g.fill(getX(), getY() + 1, right, getY() + getHeight(), 0xFF48494B);
    }

    @Override
    public void visitChildren(@NotNull Consumer<OreElement> consumer) {
        this.children.forEach(consumer);
    }

    @Override
    public void arrangeElements() {
        super.arrangeElements();
        width = oreTextInput.getWidth() + 36;
        int height = 12;

        nameLabel.setX(getX() + 18);
        nameLabel.setY(getY() + 12);
        nameLabel.setWidth(oreTextInput.getWidth());
        height += nameLabel.getHeight() + 4;

        oreTextInput.setX(getX() + 18);
        oreTextInput.setY(getY() + height);
        height += oreTextInput.getHeight();

        if (descriptionLabel != null) {
            height += 4;
            descriptionLabel.setX(getX() + 18);
            descriptionLabel.setY(getY() + height);
            descriptionLabel.setWidth(oreTextInput.getWidth());
            height += descriptionLabel.getHeight();
        }

        setHeight(height + 12);
    }
}
