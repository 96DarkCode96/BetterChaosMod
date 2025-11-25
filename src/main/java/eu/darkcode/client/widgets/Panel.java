package eu.darkcode.client.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Panel implements Renderable, Layout {

    private final @NotNull List<LayoutElement> children = new ArrayList<>();
    private int x;
    private int y;
    private int width;
    private int height;

    @Override
    public void visitChildren(Consumer<LayoutElement> consumer) {
        children.forEach(consumer);
    }

    @Override
    public void arrangeElements() {
        int yPos = 1;
        for (LayoutElement layoutElement : children) {
            if (layoutElement instanceof Layout layout)
                layout.arrangeElements();
            layoutElement.setPosition(x + 1, yPos + y);
            if (layoutElement instanceof AbstractWidget aw)
                aw.setWidth(width - 2);
            yPos += layoutElement.getHeight() + 1;
        }
        height = yPos;
    }

    @Override
    public void render(GuiGraphics graphics, int i, int j, float f) {
        graphics.fill(x, y, x + width, y + height, 0xFF1E1E1F);
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, 0xFF313233);
    }

    public void stretchHorizontal(int maxX, int screenWidth) {
        if (maxX > screenWidth)
            maxX = screenWidth;
        if (maxX < 0)
            maxX = 0;

        width = maxX;
        x = (screenWidth - maxX) / 2;
    }

    public void addChild(@NotNull LayoutElement element) {
        this.children.add(element);
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }


}
