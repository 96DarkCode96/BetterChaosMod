package eu.darkcode.client.widgets.impl;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import eu.darkcode.client.screen.BaseScreen;
import eu.darkcode.client.widgets.OreWidget;
import eu.darkcode.utils.Utils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class OreBackButton extends OreWidget {

    private static final @NotNull Identifier SPRITE = Utils.resourceLocation("widget/button_back");
    private final @NotNull BaseScreen screen;

    public OreBackButton(@NotNull BaseScreen screen) {
        super(0, 0, 15, 15);
        this.screen = screen;
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if (!keyEvent.isSelection())
            return false;
        screen.onBack();
        return true;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics g, int i, int j, float f) {
        g.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE, getX() + 4, getY() + 4, 7, 7, isHovered() ? 0xFFAAAAAA : 0xFF000000);
        if (isHovered())
            g.requestCursor(CursorTypes.POINTING_HAND);
        if (isFocused()) {
            g.fill(getX() + 1, getY() + 1, getX() + getWidth() - 1, getY() + 2, 0xFF000000);
            g.fill(getX() + 1, getY() + getHeight() - 2, getX() + getWidth() - 1, getY() + getHeight() - 1, 0xFF000000);
            g.fill(getX() + 1, getY() + 2, getX() + 2, getY() + getHeight() - 2, 0xFF000000);
            g.fill(getX() + getWidth() - 2, getY() + 2, getX() + getWidth() - 1, getY() + getHeight() - 2, 0xFF000000);
        }
    }

    @Override
    public void updateNarration(@NonNull NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, CommonComponents.GUI_BACK);
    }

    @Override
    public void onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
        screen.onBack();
    }
}