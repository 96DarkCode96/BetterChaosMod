package eu.darkcode.client.widgets.container;

import eu.darkcode.client.widgets.OreElement;
import eu.darkcode.client.widgets.config.*;
import eu.darkcode.client.widgets.utils.Ore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;

public class OrePanel extends AbstractLayout<OrePanel.ChildWrapper> implements Renderable {

    private int paddingLeft;
    private int paddingTop;
    private int paddingBottom;
    private int paddingRight;
    private int spacing;

    private int scrollAmount = 0;
    private int unscrolledY;

    public OrePanel() {
        this(1, 1, 1, 1, 1);
    }

    public OrePanel(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom, int spacing) {
        super(0, 0, 0, 0);
        this.paddingLeft = paddingLeft;
        this.paddingTop = paddingTop;
        this.paddingRight = paddingRight;
        this.paddingBottom = paddingBottom;
        this.spacing = spacing;
    }

    @Override
    public void render(GuiGraphics g, int i, int j, float f) {
        int right = getX() + getWidth();
        int bottom = getY() + getHeight();
        g.fill(getX(), getY(), right, bottom, 0xFF1E1E1F);
        g.fill(getX() + 1, getY() + 1, right - 1, bottom - 1, 0xFF313233);
    }

    public <A extends OreElement> @NotNull A addChild(@NotNull A element) {
        return addChild(element, wrapper -> {
        });
    }

    public <A extends OreElement> @NotNull A addChild(@NotNull A element, @NotNull Consumer<ChildWrapper> wrapperConsumer) {
        ChildWrapper wrapper = addChild(new ChildWrapper(element));
        wrapperConsumer.accept(wrapper);
        return element;
    }

    @NotNull
    public OreSwitchOption addSwitch(@NotNull String name, @Nullable String description, boolean value, @NotNull Consumer<Boolean> valueConsumer, boolean enabled) {
        return addChild(new OreSwitchOption(
                Component.translatable("betterchaosmod.option." + name),
                description != null ? Component.translatable("betterchaosmod.option." + description) : null,
                value, valueConsumer, enabled
        ));
    }

    @NotNull
    public OreSwitchOption addSwitch(@NotNull Component name, @Nullable Component description, boolean value, @NotNull Consumer<Boolean> valueConsumer, boolean enabled) {
        return addChild(new OreSwitchOption(name, description, value, valueConsumer, enabled));
    }

    @NotNull
    public OreCategory addCategory(@NotNull String name, @Nullable String description) {
        return addChild(new OreCategory(Component.translatable("betterchaosmod.option.category." + name),
                        description != null ? Component.translatable("betterchaosmod.option.category." + description) : null),
                wrapper -> wrapper.marginBottom(-1));
    }

    @NotNull
    public OreCategory addCategory(@NotNull Component name, @Nullable Component description) {
        return addChild(new OreCategory(name, description), wrapper -> wrapper.marginBottom(-1));
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
    public OreColorOption addColorOption(@NotNull String name, @Nullable String description, int value, @NotNull IntConsumer valueConsumer, boolean enabled) {
        return addChild(new OreColorOption(
                Component.translatable("betterchaosmod.option." + name),
                description != null ? Component.translatable("betterchaosmod.option." + description) : null,
                value, valueConsumer, enabled
        ));
    }

    @NotNull
    public OreSidebarOption addSidebarOption(@NotNull String name, @Nullable String sprite, boolean selected, @NotNull Runnable onClick) {
        return addChild(new OreSidebarOption(Component.translatable("betterchaosmod.option_section." + name), sprite, selected, onClick));
    }

    @Override
    public void visitChildren(@NotNull Consumer<OreElement> consumer) {
        children.forEach(e -> consumer.accept(e.child));
    }

    @Override
    public void arrangeElements() {
        super.arrangeElements();

        width = 0;
        for (ChildWrapper wrapper : children)
            width = Math.max(width, wrapper.child.getWidth() + wrapper.marginLeft + wrapper.marginRight);

        int posY = paddingTop;
        for (ChildWrapper wrapper : children) {
            posY += wrapper.marginTop;
            wrapper.child.setWidth(width - wrapper.marginLeft - wrapper.marginRight);
            wrapper.child.setPosition(x + paddingLeft + wrapper.marginLeft, y + posY);
            posY += wrapper.child.getHeight() + spacing + wrapper.marginBottom;
        }
        if (!children.isEmpty())
            posY -= spacing;
        width += paddingLeft + paddingRight;
        height = posY + paddingBottom;
    }

    public int scrollAmount(){
        return this.scrollAmount;
    }

    public void scrollUp() {
        scrollTo(0);
    }

    public void scrollDown() {
        scrollTo(maxScrollAmount());
    }

    public void scrollTo(int value) {
        value = Math.min(value, maxScrollAmount());
        value = Math.max(value, 0);

        updateScrollPositions(value - this.scrollAmount);

        this.scrollAmount = value;
    }

    public void scrollBy(double yDelta) {
        scrollTo(this.scrollAmount - (int) (yDelta * 10));
    }

    public int maxScrollAmount() {
        Screen screen = Minecraft.getInstance().screen;
        return screen != null ? -(screen.height - height - unscrolledY - 10) : 0;
    }

    private void updateScrollPositions(int deltaAdd) {
        super.setY(getY() - deltaAdd);
    }

    @Override
    public void setY(int i) {
        this.unscrolledY = i;
        super.setY(i - scrollAmount);
    }

    public static class ChildWrapper {

        private final @NotNull OreElement child;
        private int marginLeft;
        private int marginTop;
        private int marginRight;
        private int marginBottom;

        private ChildWrapper(@NotNull OreElement child) {
            this.child = child;
        }

        @NotNull
        private OreElement getChild() {
            return child;
        }

        public int marginLeft() {
            return marginLeft;
        }

        public int marginTop() {
            return marginTop;
        }

        public int marginRight() {
            return marginRight;
        }

        public int marginBottom() {
            return marginBottom;
        }

        @NotNull
        public ChildWrapper margin(int left, int top, int right, int bottom) {
            this.marginLeft = left;
            this.marginTop = top;
            this.marginRight = right;
            this.marginBottom = bottom;
            return this;
        }

        public void marginTop(int top) {
            this.marginTop = top;
        }

        public void marginBottom(int bottom) {
            this.marginBottom = bottom;
        }

        public void marginLeft(int left) {
            this.marginLeft = left;
        }

        public void marginRight(int right) {
            this.marginRight = right;
        }
    }

}