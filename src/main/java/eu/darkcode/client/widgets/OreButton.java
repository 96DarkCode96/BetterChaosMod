package eu.darkcode.client.widgets;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

public abstract class OreButton extends AbstractWidget {
    private static final WidgetSprites SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath("betterchaosmod", "widget/button"),
            ResourceLocation.fromNamespaceAndPath("betterchaosmod", "widget/button_disabled"),
            ResourceLocation.fromNamespaceAndPath("betterchaosmod", "widget/button_highlighted")
    );
    protected static final int TEXT_MARGIN = 2;

    protected OreButton(int i, int j, int k, int l, Component component) {
        super(i, j, k, l, component);
    }

    public abstract void onPress(InputWithModifiers inputWithModifiers);

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        Minecraft minecraft = Minecraft.getInstance();
        guiGraphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                SPRITES.get(this.active, this.isHoveredOrFocused()),
                this.getX(),
                this.getY(),
                this.getWidth(),
                this.getHeight(),
                ARGB.white(this.alpha)
        );
        int k = ARGB.color(this.alpha, this.active ? 0xFF333333 : 0xFF000000);
        this.renderString(guiGraphics, minecraft.font, k);
        if (this.isHovered()) {
            guiGraphics.requestCursor(this.isActive() ? CursorTypes.POINTING_HAND : CursorTypes.NOT_ALLOWED);
        }
    }

    @Override
    public void onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
        this.onPress(mouseButtonEvent);
    }

    public void renderString(GuiGraphics guiGraphics, Font font, int i) {
        this.renderScrollingString(guiGraphics, font, 2, i);
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if (!this.isActive()) {
            return false;
        } else if (keyEvent.isSelection()) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            this.onPress(keyEvent);
            return true;
        } else {
            return false;
        }
    }
}
