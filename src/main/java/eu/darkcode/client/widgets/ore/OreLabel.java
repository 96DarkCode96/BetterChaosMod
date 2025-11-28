package eu.darkcode.client.widgets.ore;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OreLabel implements OreElement {
    private @NotNull List<FormattedCharSequence> renderText;
    private @NotNull FormattedText rawComponent;
    private int x;
    private int y;
    private int width = 100;
    private int height;
    private int color;

    public OreLabel(@NotNull FormattedText component, int color) {
        this.rawComponent = component;
        this.renderText = List.of();
        this.color = color;
    }

    @Override
    public void render(GuiGraphics g, int i, int j, float f) {
        final Font font = Minecraft.getInstance().font;
        int posY = getY();
        for (FormattedCharSequence text : renderText) {
            g.drawString(font, text, getX(), posY, color, false);
            posY += font.lineHeight + 4;
        }
    }

    public void setText(@NotNull FormattedText component) {
        this.rawComponent = component;
        updateText();
    }

    public int color() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
        updateText();
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    public void updateText() {
        final Font font = Minecraft.getInstance().font;
        this.renderText = font.split(this.rawComponent, this.width);
        this.height = this.renderText.size() * (font.lineHeight + 4) - 4;
    }

}
