package eu.darkcode.client.widgets;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import eu.darkcode.utils.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public abstract class OreButton extends OreWidget {
    private static final WidgetSprites SPRITES = new WidgetSprites(
            Utils.resourceLocation("widget/button"),
            Utils.resourceLocation("widget/button_disabled"),
            Utils.resourceLocation("widget/button_highlighted")
    );
    private final @NotNull Component label;

    protected OreButton(int i, int j, int k, int l, @NotNull Component label) {
        super(i, j, k, l);
        this.label = label;
    }

    protected static void renderScrollingString(GuiGraphics guiGraphics, Font font, Component component, int i, int j, int k, int l, int m, int n) {
        int o = font.width(component);
        int p = (k + m - 9) / 2 + 1;
        int q = l - j;
        if (o > q) {
            int r = o - q;
            double d = Util.getMillis() / 1000.0;
            double e = Math.max(r * 0.5, 3.0);
            double f = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * d / e)) / 2.0 + 0.5;
            double g = Mth.lerp(f, 0.0, r);
            guiGraphics.enableScissor(j, k, l, m);
            guiGraphics.drawString(font, component, j - (int) g, p, n, false);
            guiGraphics.disableScissor();
        } else {
            int r = Mth.clamp(i, j + o / 2, l - o / 2);
            FormattedCharSequence formattedCharSequence = component.getVisualOrderText();
            guiGraphics.drawString(font, formattedCharSequence, r - font.width(formattedCharSequence) / 2, p, n, false);
        }
    }

    public abstract void onPress(InputWithModifiers inputWithModifiers);

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        Minecraft minecraft = Minecraft.getInstance();
        guiGraphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                SPRITES.get(this.isActive(), isHovered() || isFocused()),
                this.getX(),
                this.getY(),
                this.getWidth(),
                this.getHeight(),
                0xFFFFFFFF
        );
        this.renderString(guiGraphics, minecraft.font, isActive() ? 0xFF000000 : 0xffA0A0A0);
        if (this.isHovered())
            guiGraphics.requestCursor(this.isActive() ? CursorTypes.POINTING_HAND : CursorTypes.NOT_ALLOWED);
    }

    @Override
    public void onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
        this.onPress(mouseButtonEvent);
    }

    public void renderString(GuiGraphics guiGraphics, Font font, int j) {
        int k = this.getX() + 2;
        int l = getRight() - 2;
        OreButton.renderScrollingString(guiGraphics, font, label, (k + l) / 2, k, this.getY(), l, getBottom(), j);
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