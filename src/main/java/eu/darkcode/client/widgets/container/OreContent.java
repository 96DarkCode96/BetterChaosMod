package eu.darkcode.client.widgets.container;

import eu.darkcode.client.screen.BaseScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.gui.layouts.AbstractLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class OreContent extends AbstractLayout {
    private final @NotNull BaseScreen screen;
    private final List<ChildContainer> children = new ArrayList<>();
    private final LayoutSettings defaultChildLayoutSettings = LayoutSettings.defaults().align(0.5F, 0.5F);
    private int headerHeight = 20;
    private int spacing = 10;

    public OreContent(@NotNull final BaseScreen screen) {
        super(0, 0, screen.width, screen.height);
        this.screen = screen;
    }

    public LayoutSettings newChildLayoutSettings() {
        return this.defaultChildLayoutSettings.copy();
    }

    public LayoutSettings defaultChildLayoutSetting() {
        return this.defaultChildLayoutSettings;
    }

    @NotNull
    public <T extends LayoutElement> T addChild(@NotNull T layoutElement) {
        return this.addChild(layoutElement, this.newChildLayoutSettings());
    }

    @NotNull
    public <T extends LayoutElement> T addChild(@NotNull T layoutElement, @NotNull LayoutSettings layoutSettings) {
        this.children.add(new ChildContainer(layoutElement, layoutSettings.getExposed()));
        return layoutElement;
    }

    @NotNull
    public <T extends LayoutElement> T addChild(@NotNull T layoutElement, @NotNull Consumer<LayoutSettings> consumer) {
        return this.addChild(layoutElement, Util.make(this.newChildLayoutSettings(), consumer));
    }

    @Override
    public void visitChildren(@NotNull Consumer<LayoutElement> consumer) {
        this.children.forEach(childContainer -> consumer.accept(childContainer.child));
    }

    @Override
    public void arrangeElements() {
        if (this.children.isEmpty())
            return;

        super.arrangeElements();
        int totalWidth = 0;
        for (ChildContainer child : this.children)
            totalWidth += child.getWidth();
        totalWidth += (this.children.size() - 1) * spacing;

        int xPointer = (screen.width - totalWidth) / 2;
        for (ChildContainer childContainer : this.children) {
            childContainer.setX(xPointer);
            childContainer.setY(headerHeight + 10);
            xPointer += childContainer.getWidth() + spacing;
        }
    }

    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }

    public void setHeaderHeight(int height) {
        this.headerHeight = height;
    }

    public void clearWidgets() {
        this.children.clear();
    }

    @Environment(EnvType.CLIENT)
    record ChildContainer(LayoutElement child, LayoutSettings.LayoutSettingsImpl layoutSettings) {

        public int getHeight() {
            return this.child.getHeight();
        }

        public int getWidth() {
            return this.child.getWidth();
        }

        public void setX(int x) {
            this.child.setX(x);
        }

        public void setY(int y) {
            this.child.setY(y);
        }
    }
}