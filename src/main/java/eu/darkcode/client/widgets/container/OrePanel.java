package eu.darkcode.client.widgets.container;

import eu.darkcode.client.widgets.OreElement;
import eu.darkcode.client.widgets.config.OreCategory;
import eu.darkcode.client.widgets.config.OreInputOption;
import eu.darkcode.client.widgets.config.OreSidebarOption;
import eu.darkcode.client.widgets.config.OreSliderOption;
import eu.darkcode.client.widgets.utils.Ore;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;

public class OrePanel extends AbstractLayout<OreElement> implements Renderable {

    private int paddingLeft;
    private int paddingTop;
    private int paddingBottom;
    private int paddingRight;
    private int spacing;

    public OrePanel() {
        this(1, 1, 1, 1, 1);
    }

    public OrePanel(int paddingLeft, int paddingTop, int paddingBottom, int paddingRight, int spacing) {
        super(0, 0, 0, 0);
        this.paddingLeft = paddingLeft;
        this.paddingTop = paddingTop;
        this.paddingBottom = paddingBottom;
        this.paddingRight = paddingRight;
        this.spacing = spacing;
    }

    @Override
    public void render(GuiGraphics g, int i, int j, float f) {
        int right = getX() + getWidth();
        int bottom = getY() + getHeight();
        g.fill(getX(), getY(), right, bottom, 0xFF1E1E1F);
        g.fill(getX() + 1, getY() + 1, right - 1, bottom - 1, 0xFF313233);
    }

    @NotNull
    public OreCategory addCategory(@NotNull String name, @Nullable String description) {
        return addChild(new OreCategory(Component.translatable("betterchaosmod.option.category." + name),
                description != null ? Component.translatable("betterchaosmod.option.category." + description) : null));
    }

    @NotNull
    public OreSliderOption addSlider(@NotNull String name, @Nullable String description, int value, int min, int max, @NotNull IntFunction<Component> formatter, @NotNull IntConsumer valueConsumer, boolean enabled) {
        return addChild(Ore.sliderOption(name, description, value, min, max, formatter, valueConsumer, enabled));
    }

    @NotNull
    public OreInputOption addTextInput(@NotNull String name, @Nullable String description, @NotNull String text, @NotNull Consumer<String> valueConsumer, boolean enabled) {
        return addChild(new OreInputOption(
                Component.translatable("betterchaosmod.option." + name),
                description != null ? Component.translatable("betterchaosmod.option." + description) : null,
                text, valueConsumer, enabled
        ));
    }

    @NotNull
    public OreSidebarOption addSidebarOption(@NotNull String name, @Nullable String sprite, boolean selected, @NotNull Runnable onClick) {
        OreSidebarOption e = new OreSidebarOption(Component.translatable("betterchaosmod.option_section." + name), sprite, selected, onClick);
        children.add(e);
        return e;
    }

    @Override
    public void visitChildren(@NotNull Consumer<OreElement> consumer) {
        children.forEach(consumer);
    }

    @Override
    public void arrangeElements() {
        super.arrangeElements();

        width = 0;
        for (OreElement child : children)
            width = Math.max(width, child.getWidth());

        int posY = paddingTop;
        for (OreElement child : children) {
            child.setWidth(width);
            child.setPosition(x + paddingLeft, y + posY);
            posY += child.getHeight() + spacing;
        }
        if (!children.isEmpty())
            posY -= spacing;
        width += paddingLeft + paddingRight;
        height = posY + paddingBottom;
    }

}