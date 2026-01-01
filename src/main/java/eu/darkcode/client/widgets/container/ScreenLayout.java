package eu.darkcode.client.widgets.container;

import eu.darkcode.client.screen.BaseScreen;
import eu.darkcode.client.widgets.OreElement;
import eu.darkcode.client.widgets.impl.OreBackButton;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.layouts.LayoutSettings;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ScreenLayout extends AbstractLayout<OreElement> {
    private final @NotNull OreHeader header;
    private final @NotNull OreContent contents;
    private final @NotNull BaseScreen screen;

    public ScreenLayout(@NotNull BaseScreen screen) {
        super(0, 0, 0, 0);
        this.screen = screen;
        this.header = new OreHeader(screen);
        this.contents = new OreContent(screen);
        this.header.addTitle();
    }

    @Override
    public void visitChildren(@NotNull Consumer<OreElement> consumer) {
        this.contents.visitChildren(consumer);
        consumer.accept(header);
    }

    @Override
    public void arrangeElements() {
        this.header.arrangeElements();

        this.contents.setHeaderHeight(this.header.getHeight());
        this.contents.arrangeElements();
    }

    public <T extends OreElement> T addToHeader(@NotNull T layoutElement) {
        return this.header.addChild(layoutElement);
    }

    @NotNull
    public <T extends OreElement> T addToHeader(@NotNull T layoutElement, Consumer<LayoutSettings> consumer) {
        return this.header.addChild(layoutElement, consumer);
    }

    public void addBackButton() {
        this.header.addChild(new OreBackButton(screen), LayoutSettings::alignHorizontallyLeft);
    }

    @NotNull
    public <T extends OreElement> T addToContents(@NotNull T layoutElement) {
        return this.contents.addChild(layoutElement);
    }

    public void clearWidgets() {
        this.header.clearWidgets();
        this.contents.clearWidgets();
        this.header.addTitle();
    }

    @Override
    public int getX() {
        return 0;
    }

    @Override
    public void setX(int i) {
        // ignored
    }

    @Override
    public int getY() {
        return 0;
    }

    @Override
    public void setY(int i) {
        // ignored
    }

    @Override
    public int getWidth() {
        return this.screen.width;
    }

    @Override
    public void setWidth(int width) {
        // ignored ?
    }

    @Override
    public int getHeight() {
        return this.screen.height;
    }

    public void scrollContent(double x, double y, double sx, double sy) {
        this.contents.visitChildren(oreElement -> {
            if(oreElement instanceof OrePanel op)
                op.scrollBy(sy);
        });
    }
}
