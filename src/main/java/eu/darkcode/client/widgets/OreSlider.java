package eu.darkcode.client.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntConsumer;
import java.util.function.IntFunction;

public class OreSlider extends AbstractWidget {

    private static final int VERTICAL_PADDING = 12;
    private static final int HORIZONTAL_PADDING = 18;
    private static final int HANDLE_WIDTH = 10;
    private final @NotNull Component description;
    private final @NotNull Component name;
    private final int min;
    private final int max;
    private final @NotNull IntFunction<Component> formatter;
    private final @NotNull IntConsumer valueConsumer;
    private int value;

    public OreSlider(@NotNull Component name, @NotNull Component description, int value, int min, int max, @NotNull IntFunction<Component> formatter, @NotNull IntConsumer valueConsumer) {
        super(0, 0, 0, 50, Component.empty());
        this.name = name;
        this.description = description;

        setHeight(VERTICAL_PADDING * 3 + Minecraft.getInstance().font.lineHeight * 2 + 20);

        this.value = value;
        this.min = min;
        this.max = max;

        this.formatter = formatter;
        this.valueConsumer = valueConsumer;
    }

    @Override
    protected void renderWidget(GuiGraphics g, int i, int j, float f) {
        final Font font = Minecraft.getInstance().font;

        g.fill(getX(), getY(), getX() + getWidth(), getY() + 1, 0xFF5A5B5D);
        g.fill(getX(), getY() + 1, getX() + getWidth(), getY() + getHeight(), 0xFF48494B);

        int y = getY() + VERTICAL_PADDING;

        g.drawString(font, name, getX() + HORIZONTAL_PADDING, y, 0xFFFFFFFF, false);
        Component valueText = formatter.apply(value);
        g.drawString(font, valueText, getX() + width - HORIZONTAL_PADDING - font.width(valueText), y, 0xFFFFFFFF, false);

        y += font.lineHeight + 4;

        g.drawString(font, description, getX() + HORIZONTAL_PADDING, y, 0xFFC5C6C8, false);
        y += font.lineHeight + VERTICAL_PADDING;

        double fraction = ((double) value - min) / (max - min);
        int posX = (int) Math.floor(fraction * (getWidth() - HORIZONTAL_PADDING * 2 - HANDLE_WIDTH)) + getX() + HORIZONTAL_PADDING;
        g.fill(posX, y, posX + HANDLE_WIDTH, y + HANDLE_WIDTH, 0xFFFFFFFF);
        y += HANDLE_WIDTH;
    }

    @Override
    public void playDownSound(SoundManager soundManager) {

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, name);
    }
}
