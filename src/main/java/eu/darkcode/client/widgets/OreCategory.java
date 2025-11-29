package eu.darkcode.client.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OreCategory extends AbstractWidget {

    private final @NotNull Component name;
    private final @Nullable Component description;

    public OreCategory(@NotNull Component name, @Nullable Component description) {
        super(0, 0, 376, 0, Component.empty());
        this.name = name;
        this.description = description;
        if (description == null)
            setHeight(24 + Minecraft.getInstance().font.lineHeight);
        else
            setHeight(24 + Minecraft.getInstance().font.lineHeight * 2 + 4);
    }

    @Override
    protected void renderWidget(GuiGraphics g, int i, int j, float f) {
        final Font font = Minecraft.getInstance().font;

        g.fill(getX(), getY(), getRight(), getBottom(), 0xFF48494B);

        g.drawString(font, name, getX() + 18, getY() + 12, 0xFFFFFFFF, false);
        if (description != null)
            g.drawString(font, description, getX() + 18, getY() + 12 + font.lineHeight + 4, 0xFFC5C6C8, false);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        // ignored
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
