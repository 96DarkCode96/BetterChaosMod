package eu.darkcode.client.widgets.ore;

import eu.darkcode.client.widgets.styles.EdgeInsets;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract class OreWidget implements OreElement, GuiEventListener, NarratableEntry {

    private boolean active = true;
    private boolean focused;
    private boolean hovered;

    private int x;
    private int y;
    private int width;
    private int height;

    private @NotNull EdgeInsets padding = EdgeInsets.ZERO;
    private @NotNull EdgeInsets margin = EdgeInsets.ZERO;

    protected OreWidget() {
        this(0, 0, 0, 0);
    }

    protected OreWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public @NotNull EdgeInsets padding() {
        return this.padding;
    }

    @Override
    public @NotNull EdgeInsets margin() {
        return this.margin;
    }

    @Contract("_ -> this")
    public OreWidget padding(@NotNull EdgeInsets padding) {
        this.padding = padding;
        return this;
    }

    @Contract("_ -> this")
    public OreWidget margin(@NotNull EdgeInsets margin) {
        this.margin = margin;
        return this;
    }

    public boolean isHovered() {
        return this.hovered;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    @Override
    public @NotNull NarratableEntry.NarrationPriority narrationPriority() {
        if (this.isFocused())
            return NarrationPriority.FOCUSED;
        else if (this.isHovered())
            return NarrationPriority.HOVERED;
        return NarrationPriority.NONE;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void onClick(@NotNull MouseButtonEvent mouseButtonEvent, boolean bl) {
    }

    public void onRelease(@NotNull MouseButtonEvent mouseButtonEvent) {
    }

    protected void onDrag(@NotNull MouseButtonEvent mouseButtonEvent, double d, double e) {
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (this.isActive() && this.isValidClickButton(mouseButtonEvent.buttonInfo()) && this.isMouseOver(mouseButtonEvent.x(), mouseButtonEvent.y())) {
            this.onClick(mouseButtonEvent, bl);
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
        if (this.isValidClickButton(mouseButtonEvent.buttonInfo())) {
            this.onRelease(mouseButtonEvent);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double d, double e) {
        if (this.isValidClickButton(mouseButtonEvent.buttonInfo())) {
            this.onDrag(mouseButtonEvent, d, e);
            return true;
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double d, double e) {
        return this.isActive() && this.areCoordinatesInRectangle(d, e);
    }

    public void playDownSound(@NotNull SoundManager soundManager) {
        soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    protected boolean isValidClickButton(@NotNull MouseButtonInfo mouseButtonInfo) {
        return mouseButtonInfo.button() == 0;
    }

    private boolean areCoordinatesInRectangle(double d, double e) {
        return d >= this.getX() && e >= this.getY() && d < this.getRight() && e < this.getBottom();
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Override
    public boolean isFocused() {
        return this.focused;
    }


}