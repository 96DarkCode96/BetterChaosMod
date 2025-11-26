package eu.darkcode.client.widgets;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import eu.darkcode.client.widgets.ore.OreWidget;
import eu.darkcode.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntConsumer;
import java.util.function.IntFunction;

public class OreSlider extends OreWidget {

    private static final ResourceLocation SPRITE_BAR = Utils.resourceLocation("widget/slider_bar");
    private static final ResourceLocation SPRITE_HANDLE = Utils.resourceLocation("widget/slider_handle");
    private static final ResourceLocation SPRITE_HANDLE_HOVER = Utils.resourceLocation("widget/slider_handle_hover");
    private static final ResourceLocation SPRITE_HANDLE_DISABLED = Utils.resourceLocation("widget/slider_handle_disabled");

    private static final int VERTICAL_PADDING = 12;
    private static final int HORIZONTAL_PADDING = 18;
    private static final int HANDLE_WIDTH = 14;
    private static final int HANDLE_HEIGHT = 14;
    private static final int LINE_SPACE = 4;
    private final @NotNull Component name;
    private final @Nullable Component description;
    private final int min;
    private final int max;
    private final @NotNull IntFunction<Component> formatter;
    private final @NotNull IntConsumer valueConsumer;
    private int value;
    private boolean dragging;

    public OreSlider(@NotNull Component name, @Nullable Component description, int value, int min, int max, @NotNull IntFunction<Component> formatter, @NotNull IntConsumer valueConsumer) {
        this.name = name;
        this.description = description;

        updateHeight();

        this.value = value;
        this.min = min;
        this.max = max;

        this.formatter = formatter;
        this.valueConsumer = valueConsumer;
    }

    private void updateHeight() {
        final Font font = Minecraft.getInstance().font;
        int a = VERTICAL_PADDING * 2 + font.lineHeight + HANDLE_HEIGHT;

        if (description != null)
            a += LINE_SPACE + font.split(description, getWidth() - HORIZONTAL_PADDING * 2).size() * font.lineHeight;

        setHeight(a);
    }

    @Override
    public void render(GuiGraphics g, int i, int j, float f) {
        final Font font = Minecraft.getInstance().font;

        g.fill(getX(), getY(), getRight(), getY() + 1, 0xFF5A5B5D);
        g.fill(getX(), getY() + 1, getRight(), getBottom(), 0xFF48494B);

        int y = getY() + VERTICAL_PADDING;
        y = drawText(g, font, y);

        double fraction = ((double) value - min) / (max - min);
        int posX = (int) Math.floor(fraction * (getWidth() - HORIZONTAL_PADDING * 2 - HANDLE_WIDTH)) + getX() + HORIZONTAL_PADDING;

        g.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE_BAR, getX() + HORIZONTAL_PADDING, y + 4, posX + 2 - getX() - HORIZONTAL_PADDING, 6, enabled ? 0xFF639D52 : 0xFF999999);
        g.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE_BAR, posX, y + 4, getRight() - posX - HORIZONTAL_PADDING, 6, enabled ? 0xFFA3A4A6 : 0xFF999999);

        final boolean barHovered = i >= (getX() + HORIZONTAL_PADDING) && j >= y && i < (this.getRight() - HORIZONTAL_PADDING) && j < (y + HANDLE_HEIGHT);

        g.blitSprite(RenderPipelines.GUI_TEXTURED, enabled ? (barHovered ? SPRITE_HANDLE_HOVER : SPRITE_HANDLE) : SPRITE_HANDLE_DISABLED, posX, y, HANDLE_WIDTH, HANDLE_HEIGHT, 0xFFFFFFFF);

        if (barHovered && enabled)
            g.requestCursor(this.dragging ? CursorTypes.RESIZE_EW : CursorTypes.POINTING_HAND);
    }

    @Override
    public void setWidth(int i) {
        super.setWidth(i);
        updateHeight();
    }

    @Override
    public void onClick(@NotNull MouseButtonEvent mouseButtonEvent, boolean bl) {
        this.dragging = this.enabled;
        this.setValueFromMouse(mouseButtonEvent);
    }

    @Override
    public void onRelease(@NotNull MouseButtonEvent mouseButtonEvent) {
        this.dragging = false;
        super.playDownSound(Minecraft.getInstance().getSoundManager());
    }

    @Override
    protected void onDrag(@NotNull MouseButtonEvent mouseButtonEvent, double x, double y) {
        setValueFromMouse(mouseButtonEvent);
        super.onDrag(mouseButtonEvent, y, x);
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, name);
    }

    private void setValueFromMouse(@NotNull MouseButtonEvent mouseButtonEvent) {
        int startX = getX() + HORIZONTAL_PADDING + HANDLE_WIDTH / 2;
        int endX = getX() + getWidth() - HORIZONTAL_PADDING - HANDLE_WIDTH / 2;

        double fraction = Math.clamp((mouseButtonEvent.x() - startX) / (endX - startX), 0, 1);
        this.value = (int) Math.floor(fraction * (max - min) + min);
        valueConsumer.accept(this.value);
    }

    private int drawText(GuiGraphics g, Font font, int y) {
        g.drawString(font, name, getX() + HORIZONTAL_PADDING, y, 0xFFFFFFFF, false);
        Component valueText = formatter.apply(value);
        g.drawString(font, valueText, getRight() - HORIZONTAL_PADDING - font.width(valueText), y, 0xFFFFFFFF, false);
        y += font.lineHeight;

        if (description != null) {
            y += LINE_SPACE;
            g.drawString(font, description, getX() + HORIZONTAL_PADDING, y, 0xFFC5C6C8, false);
            y += font.lineHeight;
        }

        y += VERTICAL_PADDING / 2;
        return y;
    }
}
