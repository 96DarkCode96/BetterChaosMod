package eu.darkcode.client.screen;

import eu.darkcode.client.widgets.ScreenLayout;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseScreen extends Screen {

    protected final @NotNull ScreenLayout layout = new ScreenLayout(this);
    private final @Nullable Screen parent;

    protected BaseScreen(@Nullable Screen parent, @NotNull Component title) {
        super(title);
        this.parent = parent;
    }

    @Override
    public void onClose() {
        // [OPEN PREVIOUS SCREEN]
        assert minecraft != null;
        minecraft.setScreen(parent);
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

    private void addChildren(LayoutElement element) {
        if (element instanceof AbstractWidget aw)
            this.addRenderableWidget(aw);
        else if (element instanceof Renderable r)
            this.addRenderableOnly(r);
        if (element instanceof Layout l)
            l.visitChildren(this::addChildren);
    }

    public void onBack() {
        onClose();
    }
}
