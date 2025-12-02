package eu.darkcode.client.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class OreWidget extends AbstractOreElement implements NarratableEntry, GuiEventListener, Renderable {

    private boolean focused = false;
    private boolean hovered = false;
    private boolean active = true;

    protected OreWidget(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public boolean isHovered() {
        return hovered;
    }

    @Override
    public NarrationPriority narrationPriority() {
        if (this.focused)
            return NarrationPriority.FOCUSED;
        if (this.hovered)
            return NarrationPriority.HOVERED;
        return NarrationPriority.NONE;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean enabled) {
        this.active = enabled;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        this.hovered = guiGraphics.containsPointInScissor(i, j) && this.areCoordinatesInRectangle(i, j);
        this.renderWidget(guiGraphics, i, j, f);
    }

    private boolean areCoordinatesInRectangle(double d, double e) {
        return d >= this.getX() && e >= this.getY() && d < this.getRight() && e < this.getBottom();
    }

    protected abstract void renderWidget(@NotNull GuiGraphics g, int mouseX, int mouseY, float delta);

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public void visitWidgets(@NotNull Consumer<OreWidget> consumer) {
        consumer.accept(this);
    }

    public void onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
    }

    public void onRelease(MouseButtonEvent mouseButtonEvent) {
    }

    protected void onDrag(MouseButtonEvent mouseButtonEvent, double d, double e) {
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (this.isActive() && mouseButtonEvent.button() == 0) {
            boolean bl2 = this.isMouseOver(mouseButtonEvent.x(), mouseButtonEvent.y());
            if (bl2) {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                this.onClick(mouseButtonEvent, bl);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
        if (mouseButtonEvent.button() == 0) {
            this.onRelease(mouseButtonEvent);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double d, double e) {
        if (mouseButtonEvent.button() == 0) {
            this.onDrag(mouseButtonEvent, d, e);
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent focusNavigationEvent) {
        if (!this.isActive() || this.isFocused())
            return null;
        return ComponentPath.leaf(this);
    }

    @Override
    public boolean isMouseOver(double d, double e) {
        return this.isActive() && this.areCoordinatesInRectangle(d, e);
    }

    public void playDownSound(@NotNull SoundManager soundManager) {
        soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public void setFocused(boolean bl) {
        this.focused = bl;
    }


    @Override
    public boolean isFocused() {
        return this.focused;
    }


}
