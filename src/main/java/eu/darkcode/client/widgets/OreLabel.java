package eu.darkcode.client.widgets;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractStringWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class OreLabel extends AbstractStringWidget {
    private int maxWidth = 0;
    private int cachedWidth = 0;
    private boolean cachedWidthDirty = true;
    private StringWidget.TextOverflow textOverflow = StringWidget.TextOverflow.CLAMPED;

    public OreLabel(@NotNull Component label, @NotNull Font font) {
        this(0, 0, font.width(label.getVisualOrderText()), 9, label, font);
    }

    public OreLabel(int k, int l, @NotNull Component label, @NotNull Font font) {
        this(0, 0, k, l, label, font);
    }

    public OreLabel(int i, int j, int k, int l, @NotNull Component label, @NotNull Font font) {
        super(i, j, k, l, label, font);
        this.active = false;
    }

    @Override
    public @NotNull OreLabel setColor(int i) {
        super.setColor(i);
        return this;
    }

    @Override
    public void setMessage(Component component) {
        super.setMessage(component);
        this.cachedWidthDirty = true;
    }

    public OreLabel setMaxWidth(int i) {
        return this.setMaxWidth(i, StringWidget.TextOverflow.CLAMPED);
    }

    public OreLabel setMaxWidth(int i, StringWidget.TextOverflow textOverflow) {
        this.maxWidth = i;
        this.textOverflow = textOverflow;
        return this;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        Component component = this.getMessage();
        Font font = this.getFont();
        int k = this.maxWidth > 0 ? this.maxWidth : this.getWidth();
        int l = font.width(component);
        int m = this.getX();
        int n = this.getY() + (this.getHeight() - 9) / 2;
        boolean bl = l > k;
        if (bl) {
            switch (this.textOverflow) {
                case CLAMPED:
                    guiGraphics.drawString(font, this.clipText(component, k), m, n, this.getColor(), false);
                    break;
                case SCROLLING:
                    this.renderScrollingString(guiGraphics, font, 2, this.getColor());
            }
        } else {
            guiGraphics.drawString(font, component.getVisualOrderText(), m, n, this.getColor(), false);
        }
    }

    @Override
    public int getWidth() {
        if (this.maxWidth > 0) {
            if (this.cachedWidthDirty) {
                this.cachedWidth = Math.min(this.maxWidth, this.getFont().width(this.getMessage().getVisualOrderText()));
                this.cachedWidthDirty = false;
            }

            return this.cachedWidth;
        } else {
            return super.getWidth();
        }
    }

    private FormattedCharSequence clipText(Component component, int i) {
        Font font = this.getFont();
        FormattedText formattedText = font.substrByWidth(component, i - font.width(CommonComponents.ELLIPSIS));
        return Language.getInstance().getVisualOrder(FormattedText.composite(formattedText, CommonComponents.ELLIPSIS));
    }

}