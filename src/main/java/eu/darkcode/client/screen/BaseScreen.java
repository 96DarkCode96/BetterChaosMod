package eu.darkcode.client.screen;

import eu.darkcode.client.widgets.OreElement;
import eu.darkcode.client.widgets.OreLayout;
import eu.darkcode.client.widgets.OreWidget;
import eu.darkcode.client.widgets.container.ScreenLayout;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseScreen extends Screen {

    protected final @NotNull ScreenLayout layout = new ScreenLayout(this);
    protected final @Nullable Screen parent;

    protected BaseScreen(@Nullable Screen parent, @NotNull Component title) {
        super(title);
        this.parent = parent;
    }

    @Override
    public void onClose() {
        // [OPEN PREVIOUS SCREEN]
        this.minecraft.setScreenAndShow(parent);
    }

    @Override
    protected void clearWidgets() {
        super.clearWidgets();
        this.layout.clearWidgets();
    }

    @Override
    protected void init() {
        this.layout.visitChildren(this::addChildren);

        repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
    }

    private void addChildren(@NotNull OreElement element) {
        if (element instanceof OreWidget aw)
            this.addRenderableWidget(aw);
        else if (element instanceof Renderable r)
            this.addRenderableOnly(r);
        if (element instanceof OreLayout l)
            l.visitChildren(this::addChildren);
    }

    public void onBack() {
        onClose();
    }
}
