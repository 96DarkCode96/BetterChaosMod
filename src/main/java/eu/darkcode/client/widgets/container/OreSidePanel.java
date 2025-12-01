package eu.darkcode.client.widgets.container;

import eu.darkcode.client.widgets.OreSidebarOption;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.AbstractLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class OreSidePanel extends AbstractLayout implements Renderable {

    private final @NotNull List<LayoutElement> children = new ArrayList<>();

    public OreSidePanel() {
        super(0, 0, 0, 0);
    }

    @Override
    public void render(GuiGraphics g, int i, int j, float f) {
        int right = getX() + getWidth();
        int bottom = getY() + getHeight();
        g.fill(getX(), getY(), right, bottom, 0xFF1E1E1F);
        g.fill(getX() + 1, getY() + 1, right - 1, bottom - 1, 0xFF313233);
    }

    @NotNull
    public OreSidebarOption addSidebarOption(@NotNull String name, @Nullable String sprite, boolean selected, @NotNull Runnable onClick) {
        OreSidebarOption e = new OreSidebarOption(Component.translatable("betterchaosmod.option_section." + name), sprite, selected, onClick);
        children.add(e);
        return e;
    }

    @Override
    public void visitChildren(Consumer<LayoutElement> consumer) {
        children.forEach(consumer);
    }

    @Override
    public void arrangeElements() {
        super.arrangeElements();

        width = 0;
        for (LayoutElement child : children)
            width = Math.max(width, child.getWidth());

        int y = 10;
        for (LayoutElement child : children) {
            child.setPosition(getX() + 1, getY() + y);
            y += child.getHeight();
        }
        width += 2;
        height = y + 10;
    }

}