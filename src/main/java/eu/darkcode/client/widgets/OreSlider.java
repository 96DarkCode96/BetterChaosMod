package eu.darkcode.client.widgets;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import eu.darkcode.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntConsumer;
import java.util.function.IntFunction;

public class OreSlider extends AbstractWidget {

    private static final @NotNull ResourceLocation SPRITE_BAR = Utils.resourceLocation("widget/slider_bar");
    private static final @NotNull ResourceLocation SPRITE_HANDLE = Utils.resourceLocation("widget/slider_handle");
    private static final @NotNull ResourceLocation SPRITE_HANDLE_HOVER = Utils.resourceLocation("widget/slider_handle_hover");
    private static final @NotNull ResourceLocation SPRITE_HANDLE_DISABLED = Utils.resourceLocation("widget/slider_handle_disabled");
    private static final int HANDLE_WIDTH = 14;
    private static final int HANDLE_HEIGHT = 14;
    public static final @NotNull IntFunction<Component> FORMATTER_PLAIN = i -> Component.literal(String.valueOf(i));
    public static final @NotNull IntFunction<Component> FORMATTER_SECONDS = i -> Component.literal(i + "s");
    public static final @NotNull IntFunction<Component> FORMATTER_PIXELS = i -> Component.literal(i + "px");

    private final int min;
    private final int max;
    private final @NotNull IntConsumer valueConsumer;
    private int value;
    private boolean dragging;

    public OreSlider(int value, int min, int max, @NotNull IntConsumer valueConsumer) {
        super(0, 0, 340, HANDLE_HEIGHT, Component.empty());

        this.value = value;
        this.min = min;
        this.max = max;

        this.valueConsumer = valueConsumer;
    }

    @Override
    protected void renderWidget(GuiGraphics g, int i, int j, float f) {
        double fraction = ((double) value - min) / (max - min);
        int posX = (int) Math.floor(fraction * (getWidth() - HANDLE_WIDTH)) + getX();

        g.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE_BAR, getX(), getY() + 4, posX + 2 - getX(), 6, isActive() ? 0xFF639D52 : 0xFF999999);
        g.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE_BAR, posX, getY() + 4, getRight() - posX, 6, isActive() ? 0xFFA3A4A6 : 0xFF999999);

        final boolean barHovered = i >= getX() && j >= getY() && i < this.getRight() && j < this.getBottom();

        g.blitSprite(RenderPipelines.GUI_TEXTURED, isActive() ? (barHovered ? SPRITE_HANDLE_HOVER : SPRITE_HANDLE) : SPRITE_HANDLE_DISABLED, posX, getY(), HANDLE_WIDTH, HANDLE_HEIGHT, 0xFFFFFFFF);

        if (barHovered && isActive())
            g.requestCursor(this.dragging ? CursorTypes.RESIZE_EW : CursorTypes.POINTING_HAND);
    }

    @Override
    public void onClick(@NotNull MouseButtonEvent mouseButtonEvent, boolean bl) {
        this.dragging = this.isActive();
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
    public void playDownSound(SoundManager soundManager) {
        // ignored
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        // ignored ? should add at least something
    }

    private void setValueFromMouse(@NotNull MouseButtonEvent mouseButtonEvent) {
        int startX = getX() + HANDLE_WIDTH / 2;
        int endX = getRight() - HANDLE_WIDTH / 2;

        double fraction = Math.clamp((mouseButtonEvent.x() - startX) / (endX - startX), 0, 1);
        this.value = (int) Math.floor(fraction * (max - min) + min);
        valueConsumer.accept(this.value);
    }

}
