package eu.darkcode.client.widgets.config;

import eu.darkcode.client.widgets.AbstractOreElement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OreCategory extends AbstractOreElement implements Renderable {

    private final @NotNull Component nameComponent;
    private final @Nullable Component descriptionComponent;

    private @NotNull List<FormattedCharSequence> cachedName = List.of();
    private @NotNull List<FormattedCharSequence> cachedDescription = List.of();

    public OreCategory(@NotNull Component name, @Nullable Component description) {
        super(0, 0, 0, 0);
        if (name instanceof MutableComponent mc)
            mc.withStyle(ChatFormatting.BOLD);

        this.nameComponent = name;
        this.descriptionComponent = description;
        update();
    }

    private void update() {
        final Font font = Minecraft.getInstance().font;
        int h = 24;
        cachedName = font.split(nameComponent, getWidth() - 36);
        h += cachedName.size() * font.lineHeight;
        if (descriptionComponent != null) {
            h += 4;
            cachedDescription = font.split(descriptionComponent, getWidth() - 36);
            h += cachedDescription.size() * font.lineHeight;
        }
        setHeight(h);
    }

    @Override
    public void render(GuiGraphics g, int i, int j, float f) {
        g.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0xFF404142);

        final Font font = Minecraft.getInstance().font;
        int y = getY() + 12;
        for (FormattedCharSequence line : cachedName) {
            g.drawString(font, line, getX() + 18, y, 0xFFFFFFFF, false);
            y += font.lineHeight;
        }
        if (descriptionComponent == null)
            return;
        y += 4;
        for (FormattedCharSequence line : cachedDescription) {
            g.drawString(font, line, getX() + 18, y, 0xFFC5C6C8, false);
            y += font.lineHeight;
        }
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        update();
    }

}
