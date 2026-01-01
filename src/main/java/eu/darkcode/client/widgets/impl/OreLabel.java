package eu.darkcode.client.widgets.impl;

import eu.darkcode.client.widgets.AbstractOreElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Environment(EnvType.CLIENT)
public class OreLabel extends AbstractOreElement implements Renderable {

    private final @NotNull Font font;
    private @NotNull Component text;
    private int color = 0xFFFFFFFF;
    private @NotNull List<FormattedCharSequence> cachedText = List.of();

    public OreLabel(@NotNull Component label, @NotNull Font font) {
        this(font.width(label.getVisualOrderText()), 8, label, font);
    }

    public OreLabel(int k, int l, @NotNull Component label, @NotNull Font font) {
        super(0, 0, k, l);
        this.text = label;
        this.font = font;
        updateText();
    }

    @Contract("_ -> this")
    public OreLabel setColor(int color) {
        this.color = color;
        return this;
    }

    public void setText(@NotNull Component text) {
        this.text = text;
        updateText();
    }

    public void setTextAndWidth(@NotNull Component text) {
        this.text = text;
        setWidth(font.width(text.getVisualOrderText()));
    }

    private void updateText() {
        cachedText = font.split(text, getWidth());
        int textHeight = cachedText.size() * font.lineHeight - 1;
        this.setHeight(textHeight);
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        updateText();
    }

    @Override
    public void render(GuiGraphics g, int i, int j, float f) {
        int yPos = this.getY();
        for (FormattedCharSequence line : cachedText) {
            g.drawString(font, line, this.getX(), yPos, color, false);
            yPos += font.lineHeight;
        }
    }
}