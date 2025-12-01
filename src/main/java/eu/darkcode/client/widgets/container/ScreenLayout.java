package eu.darkcode.client.widgets.container;

import eu.darkcode.client.screen.BaseScreen;
import eu.darkcode.client.widgets.OreBackButton;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ScreenLayout implements Layout {
    private final @NotNull OreHeader header;
    private final @NotNull OreContent contents;
    private final @NotNull BaseScreen screen;

    public ScreenLayout(@NotNull BaseScreen screen) {
        this.screen = screen;
        this.header = new OreHeader(screen);
        this.contents = new OreContent(screen);
        this.header.addTitle();
    }

    @Override
    public void visitChildren(@NotNull Consumer<LayoutElement> consumer) {
        this.contents.visitChildren(consumer);
        consumer.accept(header);
    }

    @Override
    public void arrangeElements() {
        this.header.arrangeElements();

        this.contents.setHeaderHeight(this.header.getHeight());
        this.contents.arrangeElements();
    }

    public <T extends LayoutElement> T addToHeader(T layoutElement) {
        return this.header.addChild(layoutElement);
    }

    public <T extends LayoutElement> T addToHeader(T layoutElement, Consumer<LayoutSettings> consumer) {
        return this.header.addChild(layoutElement, consumer);
    }

    @NotNull
    public OreBackButton addBackButton() {
        return this.header.addChild(new OreBackButton(screen), LayoutSettings::alignHorizontallyLeft);
    }

    public <T extends LayoutElement> T addToContents(T layoutElement) {
        return this.contents.addChild(layoutElement);
    }

    public <T extends LayoutElement> T addToContents(T layoutElement, Consumer<LayoutSettings> consumer) {
        return this.contents.addChild(layoutElement, consumer);
    }

    public void clearWidgets() {
        this.header.clearWidgets();
        this.contents.clearWidgets();
        this.header.addTitle();
    }

    @Override
    public void setX(int i) {
        // ignored
    }

    @Override
    public void setY(int i) {
        // ignored
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
