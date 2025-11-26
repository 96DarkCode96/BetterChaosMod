package eu.darkcode.client.widgets.ore;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class OreContainer extends OreWidget {

    protected final @NotNull List<OreElement> children = new ArrayList<>();
    protected final @NotNull List<OreWidget> widgets = new ArrayList<>();

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

    public abstract void arrangeElements();

    @NotNull
    public <T extends OreElement> T addElement(@NotNull T element) {
        this.children.add(element);
        return element;
    }

    @NotNull
    public <T extends OreWidget> T addWidget(@NotNull T element) {
        this.widgets.add(element);
        return addElement(element);
    }

    @Override
    public void onClick(@NotNull MouseButtonEvent mouseButtonEvent, boolean bl) {
        widgets.forEach(widget -> widget.onClick(mouseButtonEvent, bl));
    }

    @Override
    public void onRelease(@NotNull MouseButtonEvent mouseButtonEvent) {
        widgets.forEach(widget -> widget.onRelease(mouseButtonEvent));
    }

    @Override
    protected void onDrag(@NotNull MouseButtonEvent mouseButtonEvent, double d, double e) {
        widgets.forEach(widget -> widget.onDrag(mouseButtonEvent, d, e));
    }
}
