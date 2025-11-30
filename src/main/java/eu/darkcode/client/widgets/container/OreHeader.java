package eu.darkcode.client.widgets.container;

import eu.darkcode.client.widgets.OreLabel;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

public class OreHeader extends FrameLayout implements Renderable {

    private final @NotNull Screen screen;

    public OreHeader(@NotNull Screen screen) {
        super(0, 0, screen.width, screen.height);
        this.screen = screen;
        defaultChildLayoutSetting().paddingTop(5).paddingBottom(8).paddingHorizontal(10);
        if (screen.getTitle() instanceof MutableComponent mc)
            mc.withStyle(ChatFormatting.BOLD);

        addChild(new OreLabel(screen.getTitle(), Minecraft.getInstance().font).setColor(0xFF000000));
    }

    @Override
    public void render(GuiGraphics g, int i, int j, float f) {
        int right = getX() + getWidth();
        int bottom = getY() + getHeight();
        g.fill(getX(), getY(), right, bottom - 3, 0xFFE9EAEE);
        g.fill(getX(), bottom - 3, right, bottom, 0xFF929291);
        g.fill(getX(), bottom - 1, right, bottom, 0xFF1E1E1F);
    }

    @Override
    public int getWidth() {
        return screen.width;
    }
}
