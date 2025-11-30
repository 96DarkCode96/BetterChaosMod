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
        this.children.add(new ChildContainer(layoutElement, layoutSettings));
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
        super.arrangeElements();
        for (ChildContainer childContainer : this.children) {
            childContainer.setX(0, screen.width);
            childContainer.setY(headerHeight + 10, screen.height - headerHeight - 10);
        }
    }

    public void setHeaderHeight(int height) {
        this.headerHeight = height;
    }

    @Environment(EnvType.CLIENT)
    static class ChildContainer extends AbstractLayout.AbstractChildWrapper {
        protected ChildContainer(LayoutElement layoutElement, LayoutSettings layoutSettings) {
            super(layoutElement, layoutSettings);
        }
    }
}