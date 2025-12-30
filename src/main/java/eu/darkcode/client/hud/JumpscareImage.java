package eu.darkcode.client.hud;

import eu.darkcode.client.SharedProperties;
import eu.darkcode.utils.Utils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 30.12.2025
 **/
public class JumpscareImage implements Hud {
    private static final ResourceLocation TEXTURE = Utils.resourceLocation("jumpscare");

    @Override
    public void render(HudSettings hudManager, @NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        int width = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();

        boolean isFlash = (System.currentTimeMillis() / 50) % 2 == 0;

        int flashColor = isFlash ? 0xFFFFFFFF : 0xFFFF0000;

        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, width, height, flashColor);
    }

    @Override
    public boolean enabled(boolean hideGui) {
        return SharedProperties.jumpscareActive;
    }
}
