package eu.darkcode.client.widgets;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ScreenLayout implements Layout {
    private final @NotNull OreHeader header;
    private final @NotNull FrameLayout contentsFrame = new FrameLayout();
    private final @NotNull Screen screen;

    public ScreenLayout(@NotNull Screen screen) {
        this.screen = screen;
        this.header = new OreHeader(screen);
    }

    @Override
    public void visitChildren(Consumer<LayoutElement> consumer) {
        this.contentsFrame.visitChildren(consumer);
        consumer.accept(header);
    }

    @Override
    public void arrangeElements() {
        this.header.setPosition(0, 0);
        this.header.setMinWidth(this.screen.width);
        this.header.arrangeElements();
        this.contentsFrame.setMinWidth(this.screen.width);
        this.contentsFrame.arrangeElements();
        int k = header.getHeight() + 20;
        int l = this.screen.height - this.contentsFrame.getHeight();
        this.contentsFrame.setPosition(0, Math.min(k, l));
    }

    public <T extends LayoutElement> T addToHeader(T layoutElement) {
        return this.header.addChild(layoutElement);
    }

    public <T extends LayoutElement> T addToHeader(T layoutElement, Consumer<LayoutSettings> consumer) {
        return this.header.addChild(layoutElement, consumer);
    }

    public <T extends LayoutElement> T addToContents(T layoutElement) {
        return this.contentsFrame.addChild(layoutElement);
    }

    public <T extends LayoutElement> T addToContents(T layoutElement, Consumer<LayoutSettings> consumer) {
        return this.contentsFrame.addChild(layoutElement, consumer);
    }

    @Override
    public void setX(int i) {
    }


    @Override
    public void setY(int i) {
    }


    @Override
    public int getX() {
        return 0;
    }

    @Override
    public int getY() {
        return 0;
    }

    @Override
    public int getWidth() {
        return this.screen.width;
    }

    @Override
    public int getHeight() {
        return this.screen.height;
    }

}
