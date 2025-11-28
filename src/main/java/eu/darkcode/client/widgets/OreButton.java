package eu.darkcode.client.widgets;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import eu.darkcode.client.widgets.ore.OreWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.NotNull;

public abstract class OreButton extends OreWidget {
    private static final WidgetSprites SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath("betterchaosmod", "widget/button"),
            ResourceLocation.fromNamespaceAndPath("betterchaosmod", "widget/button_disabled"),
            ResourceLocation.fromNamespaceAndPath("betterchaosmod", "widget/button_highlighted")
    );
    private final @NotNull Component text;

    protected OreButton(int i, int j, int k, int l, @NotNull Component text) {
        super(i, j, k, l);
        this.text = text;
    }

    public abstract void onPress(InputWithModifiers inputWithModifiers);

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        Minecraft minecraft = Minecraft.getInstance();
        guiGraphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                SPRITES.get(isActive(), isHovered() || isFocused()),
                this.getX(),
                this.getY(),
                this.getWidth(),
                this.getHeight(),
                ARGB.white(1.0F)
        );
        int k = ARGB.color(1.0F, this.isActive() ? 0xFF333333 : 0xFF000000);
        guiGraphics.drawCenteredString(minecraft.font, text, getX(), getY(), k);

        if (this.isHovered())
            guiGraphics.requestCursor(this.isActive() ? CursorTypes.POINTING_HAND : CursorTypes.NOT_ALLOWED);
    }

    @Override
    public void onClick(@NotNull MouseButtonEvent mouseButtonEvent, boolean bl) {
        this.onPress(mouseButtonEvent);
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if (this.isActive() && keyEvent.isSelection()) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            this.onPress(keyEvent);
            return true;
        }
        return false;
    }

}
