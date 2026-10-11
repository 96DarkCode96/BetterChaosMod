package eu.darkcode.client.widgets.impl;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import eu.darkcode.client.widgets.OreWidget;
import eu.darkcode.client.widgets.utils.SpriteAtlas;
import eu.darkcode.utils.Utils;
import net.minecraft.client.InputType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.function.IntConsumer;
import java.util.function.IntFunction;

public class OreSlider extends OreWidget {

    private static final @NotNull Identifier SPRITE_BAR = Utils.resourceLocation("widget/slider_bar");

    private static final @NotNull SpriteAtlas HANDLE_SPRITES = new SpriteAtlas("widget/slider_handle");

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
    private boolean canChangeValue;

    public OreSlider(int value, int min, int max, @NotNull IntConsumer valueConsumer) {
        super(0, 0, 340, HANDLE_HEIGHT);

        this.value = value;
        this.min = min;
        this.max = max;

        this.valueConsumer = valueConsumer;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics g, int i, int j, float f) {
        double fraction = ((double) value - min) / (max - min);
        int posX = (int) Math.floor(fraction * (getWidth() - HANDLE_WIDTH)) + getX();

        g.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE_BAR, getX(), getY() + 4, posX + 2 - getX(), 6, isActive() ? 0xFF639D52 : 0xFF999999);
        g.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE_BAR, posX, getY() + 4, getRight() - posX, 6, isActive() ? 0xFFA3A4A6 : 0xFF999999);

        final Identifier sprite = HANDLE_SPRITES.get(isHovered(), isFocused(), isActive());

        if (isFocused())
            g.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, posX - 1, getY() - 1, HANDLE_WIDTH + 2, HANDLE_HEIGHT + 2, 0xFFFFFFFF);
        else
            g.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, posX, getY(), HANDLE_WIDTH, HANDLE_HEIGHT, 0xFFFFFFFF);

        renderCursor(g);
    }

    @Override
    public void updateNarration(@NonNull NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, Component.translatable("gui.narrate.slider", Component.literal("Option")));
        if (!this.isActive())
            return;
        if (this.isFocused()) {
            if (this.canChangeValue) {
                narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.slider.usage.focused"));
            } else {
                narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.slider.usage.focused.keyboard_cannot_change_value"));
            }
        } else {
            narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.slider.usage.hovered"));
        }
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
    public void playDownSound(@NotNull SoundManager soundManager) {
        // ignored
    }

    @Override
    public void setFocused(boolean bl) {
        super.setFocused(bl);
        if (!bl) {
            this.canChangeValue = false;
            return;
        }
        InputType inputType = Minecraft.getInstance().getLastInputType();
        if (inputType == InputType.MOUSE || inputType == InputType.KEYBOARD_TAB)
            this.canChangeValue = true;
    }

    private void renderCursor(@NotNull GuiGraphics g) {
        if (!isHovered())
            return;
        if (!isActive()) {
            g.requestCursor(CursorTypes.NOT_ALLOWED);
            return;
        }
        if (this.dragging) {
            g.requestCursor(CursorTypes.RESIZE_EW);
            return;
        }
        g.requestCursor(CursorTypes.POINTING_HAND);
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if (keyEvent.isSelection()) {
            this.canChangeValue = !this.canChangeValue;
            return true;
        }
        if (!this.canChangeValue)
            return false;

        if (keyEvent.isLeft()) {
            value -= 1;
            if (value < min)
                value = min;
            valueConsumer.accept(this.value);
            return true;
        }

        if (keyEvent.isRight()) {
            value += 1;
            if (value > max)
                value = max;
            valueConsumer.accept(this.value);
            return true;
        }

        return false;
    }

    private void setValueFromMouse(@NotNull MouseButtonEvent mouseButtonEvent) {
        int startX = getX() + HANDLE_WIDTH / 2;
        int endX = getRight() - HANDLE_WIDTH / 2;

        double fraction = Math.clamp((mouseButtonEvent.x() - startX) / (endX - startX), 0, 1);
        this.value = (int) Math.floor(fraction * (max - min) + min);
        valueConsumer.accept(this.value);
    }

}
