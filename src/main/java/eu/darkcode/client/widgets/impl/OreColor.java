package eu.darkcode.client.widgets.impl;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import eu.darkcode.client.screen.ColorPickerScreen;
import eu.darkcode.client.widgets.OreWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntConsumer;

public class OreColor extends OreWidget {

    private final @NotNull IntConsumer valueConsumer;
    private int color;

    public OreColor(int color, @NotNull IntConsumer valueConsumer) {
        this(0, 0, 16, 16, color, valueConsumer);
    }

    public OreColor(int x, int y, int width, int height, int color, @NotNull IntConsumer valueConsumer) {
        super(x, y, width, height);
        this.color = color;
        this.valueConsumer = valueConsumer;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics g, int mouseX, int mouseY, float delta) {
        if (isFocused()) {
            g.fill(getX() - 1, getY() - 1, getRight() + 1, getY(), 0xFFFFFFFF);
            g.fill(getX() - 1, getY(), getX(), getBottom(), 0xFFFFFFFF);
            g.fill(getRight(), getY(), getRight() + 1, getBottom(), 0xFFFFFFFF);
            g.fill(getX() - 1, getBottom(), getRight() + 1, getBottom() + 1, 0xFFFFFFFF);
        }

        g.fill(getX(), getY(), getRight(), getBottom(), 0xFF000000);
        g.fill(getX() + 2, getY() + 2, getRight() - 2, getBottom() - 2, color | 0xFF000000);
        if (isHovered())
            g.requestCursor(isActive() ? CursorTypes.POINTING_HAND : CursorTypes.NOT_ALLOWED);
    }

    @Override
    public void onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (!isActive())
            return;
        openColorPicker();
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if (!keyEvent.isSelection())
            return false;
        openColorPicker();
        return true;
    }

    private void openColorPicker() {
        Minecraft.getInstance().setScreen(new ColorPickerScreen(color, newColor -> {
            this.color = newColor;
            valueConsumer.accept(newColor);
        }));
    }
}
