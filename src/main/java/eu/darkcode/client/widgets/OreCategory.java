package eu.darkcode.client.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OreCategory extends LinearLayout implements Renderable {

    private final @NotNull LayoutElement parent;

    public OreCategory(@NotNull LayoutElement parent, @NotNull Component name, @Nullable Component description) {
        super(0, 0, Orientation.VERTICAL);
        this.parent = parent;
        spacing(4).defaultCellSetting().alignHorizontallyLeft().paddingHorizontal(18);

        if (description == null) {
            addChild(new OreLabel(name, Minecraft.getInstance().font), a -> a.paddingVertical(12)).setColor(0xFFFFFFFF);
        } else {
            addChild(new OreLabel(name, Minecraft.getInstance().font), a -> a.paddingTop(12)).setColor(0xFFFFFFFF);
            addChild(new OreLabel(description, Minecraft.getInstance().font), a -> a.paddingBottom(12)).setColor(0xFFC5C6C8);
        }
    }

    @Override
    public int getWidth() {
        return parent.getWidth() - 2;
    }

    @Override
    public void render(GuiGraphics g, int i, int j, float f) {
        g.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0xFF48494B);
    }
}
