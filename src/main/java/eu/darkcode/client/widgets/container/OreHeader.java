package eu.darkcode.client.widgets.container;

import eu.darkcode.client.widgets.OreElement;
import eu.darkcode.client.widgets.impl.OreLabel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class OreHeader extends AbstractLayout<OreHeader.ChildContainer> implements Renderable {

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

    @NotNull
    public <T extends OreElement> T addChild(@NotNull T layoutElement) {
        return this.addChild(layoutElement, this.newChildLayoutSettings());
    }

    @NotNull
    public <T extends OreElement> T addChild(@NotNull T layoutElement, LayoutSettings layoutSettings) {
        this.addChild(new ChildContainer(layoutElement, layoutSettings.getExposed()));
        return layoutElement;
    }

    @NotNull
    public <T extends OreElement> T addChild(@NotNull T layoutElement, Consumer<LayoutSettings> consumer) {
        return this.addChild(layoutElement, Util.make(this.newChildLayoutSettings(), consumer));
    }

    @Override
    public void visitChildren(@NotNull Consumer<OreElement> consumer) {
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
    public void extractRenderState(GuiGraphicsExtractor g, int i, int j, float f) {
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
    record ChildContainer(OreElement child, LayoutSettings.LayoutSettingsImpl layoutSettings) {

        public int getHeight() {
            return this.child.getHeight() + this.layoutSettings.paddingTop + this.layoutSettings.paddingBottom;
        }

        public int getWidth() {
            return this.child.getWidth() + this.layoutSettings.paddingLeft + this.layoutSettings.paddingRight;
        }

        public void setX(int i, int j) {
            float f = this.layoutSettings.paddingLeft;
            float g = j - this.child.getWidth() - this.layoutSettings.paddingRight;
            int k = (int) Mth.lerp(this.layoutSettings.xAlignment, f, g);
            this.child.setX(k + i);
        }

        public void setY(int i, int j) {
            float f = this.layoutSettings.paddingTop;
            float g = j - this.child.getHeight() - this.layoutSettings.paddingBottom;
            int k = Math.round(Mth.lerp(this.layoutSettings.yAlignment, f, g));
            this.child.setY(k + i);
        }
    }
}
