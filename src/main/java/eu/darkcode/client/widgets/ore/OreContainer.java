package eu.darkcode.client.widgets.ore;

import eu.darkcode.client.widgets.ore.layout.Layout;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class OreContainer extends OreWidget {

    protected final @NotNull List<OreElement> children = new ArrayList<>();
    protected final @NotNull List<OreWidget> widgets = new ArrayList<>();

    private @Nullable OreWidget focused = null;
    private boolean dragging = false;

    private @NotNull Layout layout;

    protected OreContainer(@NotNull Layout layout) {
        this.layout = layout;
    }

    public void widgets(@NotNull Consumer<OreWidget> consumer) {
        widgets.forEach(consumer);
    }

    public void elements(@NotNull Consumer<OreElement> consumer) {
        children.forEach(consumer);
    }

    @Override
    public void render(GuiGraphics g, int i, int j, float f) {
        elements(el -> el.render(g, i, j, f));
    }

    @Override
    public void updateNarration(NarrationElementOutput output) {
        widgets(e -> e.updateNarration(output));
    }

    public void arrangeElements() {
        this.layout.arrangeElements(this);
    }

    @NotNull
    public <T extends OreElement> T addElement(@NotNull T element) {
        this.children.add(element);
        return element;
    }

    public void setLayout(@NotNull Layout layout) {
        this.layout = layout;
    }

    @NotNull
    public <T extends OreWidget> T addWidget(@NotNull T element) {
        this.widgets.add(element);
        return addElement(element);
    }

    @NotNull
    public OreContainer addContainer(@NotNull Layout layout) {
        return addWidget(new OreContainer(layout));
    }

    @NotNull
    public Optional<OreWidget> getChildAt(double x, double y) {
        for (OreWidget widget : widgets)
            if (widget.isMouseOver(x, y))
                return Optional.of(widget);
        return Optional.empty();
    }

    private void setFocusedWidget(@Nullable OreWidget oreWidget) {
        if (this.focused != null)
            this.focused.setFocused(false);
        if (oreWidget != null)
            oreWidget.setFocused(true);
        this.focused = oreWidget;
    }

    public boolean isEmpty() {
        return this.children.isEmpty();
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        widgets(e -> e.setActive(active));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        Optional<OreWidget> childAt = getChildAt(mouseButtonEvent.x(), mouseButtonEvent.y());
        if (childAt.isEmpty())
            return false;

        OreWidget oreWidget = childAt.get();
        if (oreWidget.mouseClicked(mouseButtonEvent, bl)) {
            setFocusedWidget(oreWidget);
            if (mouseButtonEvent.button() == 0)
                dragging = true;
        }

        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
        if (mouseButtonEvent.button() == 0 && dragging) {
            dragging = false;
            if (focused != null)
                return focused.mouseReleased(mouseButtonEvent);
        }

        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double d, double e) {
        return focused != null && dragging && mouseButtonEvent.button() == 0 && focused.mouseDragged(mouseButtonEvent, d, e);
    }

}
