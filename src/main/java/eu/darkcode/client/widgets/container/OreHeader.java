package eu.darkcode.client.widgets.container;

import eu.darkcode.client.widgets.OreLabel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.AbstractLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class OreHeader extends AbstractLayout implements Renderable {

    private final List<ChildContainer> children = new ArrayList<>();
    private final LayoutSettings defaultChildLayoutSettings = LayoutSettings.defaults().align(0.5F, 0.5F);
    private final @NotNull Screen screen;

    public OreHeader(@NotNull Screen screen) {
        super(0, 0, screen.width, screen.height);
        this.screen = screen;
        defaultChildLayoutSetting().paddingTop(4).paddingBottom(7).paddingHorizontal(10);
    }

    public void addTitle() {
        if (screen.getTitle() instanceof MutableComponent mc)
            mc.withStyle(ChatFormatting.BOLD);
        addChild(new OreLabel(screen.getTitle(), Minecraft.getInstance().font).setColor(0xFF000000));
    }

    public LayoutSettings newChildLayoutSettings() {
        return this.defaultChildLayoutSettings.copy();
    }

    public LayoutSettings defaultChildLayoutSetting() {
        return this.defaultChildLayoutSettings;
    }

    public <T extends LayoutElement> T addChild(T layoutElement) {
        return this.addChild(layoutElement, this.newChildLayoutSettings());
    }

    public <T extends LayoutElement> T addChild(T layoutElement, LayoutSettings layoutSettings) {
        this.children.add(new ChildContainer(layoutElement, layoutSettings));
        return layoutElement;
    }

    public <T extends LayoutElement> T addChild(T layoutElement, Consumer<LayoutSettings> consumer) {
        return this.addChild(layoutElement, Util.make(this.newChildLayoutSettings(), consumer));
    }

    @Override
    public void visitChildren(Consumer<LayoutElement> consumer) {
        this.children.forEach(childContainer -> consumer.accept(childContainer.child));
    }

    @Override
    public void arrangeElements() {
        super.arrangeElements();

        width = screen.width;
        height = 0;
        for (ChildContainer child : this.children)
            height = Math.max(height, child.getHeight());

        for (ChildContainer childContainer : this.children) {
            childContainer.setX(this.getX(), width);
            childContainer.setY(this.getY(), height);
        }
    }

    @Override
    public void render(GuiGraphics g, int i, int j, float f) {
        int right = getX() + getWidth();
        int bottom = getY() + getHeight();
        g.fill(getX(), getY(), right, bottom - 3, 0xFFE9EAEE);
        g.fill(getX(), bottom - 3, right, bottom, 0xFF929291);
        g.fill(getX(), bottom - 1, right, bottom, 0xFF1E1E1F);
    }

    @Override
    public int getWidth() {
        return screen.width;
    }

    public void clearWidgets() {
        this.children.clear();
    }

    @Environment(EnvType.CLIENT)
    static class ChildContainer extends AbstractLayout.AbstractChildWrapper {
        protected ChildContainer(LayoutElement layoutElement, LayoutSettings layoutSettings) {
            super(layoutElement, layoutSettings);
        }
    }
}
