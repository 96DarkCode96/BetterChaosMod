package eu.darkcode.client.widgets.container;

import eu.darkcode.client.screen.BaseScreen;
import eu.darkcode.client.widgets.OreElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class OreContent extends AbstractLayout<OreElement> {
    private final @NotNull BaseScreen screen;
    private int headerHeight = 20;
    private int spacing = 10;

    public OreContent(@NotNull final BaseScreen screen) {
        super(0, 0, screen.width, screen.height);
        this.screen = screen;
    }

    @Override
    public void visitChildren(@NotNull Consumer<OreElement> consumer) {
        this.children.forEach(consumer);
    }

    @Override
    public void arrangeElements() {
        if (this.children.isEmpty())
            return;

        super.arrangeElements();
        int totalWidth = 0;
        for (OreElement child : this.children)
            totalWidth += child.getWidth();
        totalWidth += (this.children.size() - 1) * spacing;

        int xPointer = (screen.width - totalWidth) / 2;
        for (OreElement childContainer : this.children) {
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

}