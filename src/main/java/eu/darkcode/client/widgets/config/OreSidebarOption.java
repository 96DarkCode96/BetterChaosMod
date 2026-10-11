package eu.darkcode.client.widgets.config;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import eu.darkcode.client.widgets.OreWidget;
import eu.darkcode.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OreSidebarOption extends OreWidget {
    private static final int ICON_SIZE = 24;
    private final boolean selected;
    private final @NotNull Runnable onClick;
    private final @Nullable Identifier sprite;
    private final @NotNull Component text;

    public OreSidebarOption(@NotNull Component component, @Nullable String spriteLocation, boolean selected, @NotNull Runnable onClick) {
        super(0, 0, 130, 14 + ICON_SIZE);
        text = component;
        this.selected = selected;
        this.onClick = onClick;
        this.sprite = spriteLocation == null ? null : Utils.resourceLocation("icons/" + spriteLocation);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics g, int i, int j, float f) {
        int right = getX() + getWidth();
        int bottom = getY() + getHeight();
        if (selected) {
            g.fill(getX(), getY(), right, getY() + 1, 0xFF1E1E1F);
            g.fill(getX(), getY() + 1, right, bottom - 1, 0xFF48494B);
            g.fill(getX(), bottom - 1, right, bottom, 0xFF5A5B5D);
        }
        if (isHovered() || isFocused())
            g.fill(getX(), getY(), right, bottom, 0x10FFFFFF);

        int xIcon = getX() + 10;
        if (sprite == null)
            g.fill(xIcon, getY() + 7, xIcon + ICON_SIZE, getY() + 7 + ICON_SIZE, 0xFF000000);
        else
            g.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, xIcon, getY() + 7, ICON_SIZE, ICON_SIZE, 0xFFFFFFFF);

        final Font font = Minecraft.getInstance().font;
        g.drawString(font, text, xIcon + ICON_SIZE + 8, getY() + (getHeight() - 8) / 2, 0xFFFFFFFF, false);
        if (isHovered())
            g.requestCursor(CursorTypes.POINTING_HAND);
    }

    @Override
    public void onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (selected)
            return;
        this.onClick.run();
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if (!keyEvent.isSelection())
            return false;
        if (!selected)
            this.onClick.run();
        return true;
    }
}
