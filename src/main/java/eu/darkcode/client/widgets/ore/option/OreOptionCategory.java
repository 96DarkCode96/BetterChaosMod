package eu.darkcode.client.widgets.ore.option;

import eu.darkcode.client.widgets.ore.OreLabel;
import eu.darkcode.client.widgets.styles.EdgeInsets;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OreOptionCategory extends OreOption {

    public OreOptionCategory(@NotNull Component name, @Nullable Component description) {
        super();

        addElement(new OreLabel(name, 0xFFFFFFFF));
        if (description != null)
            addElement(new OreLabel(description, 0xFFC5C6C8));

        padding(new EdgeInsets(18, 12));
        margin(new EdgeInsets(0, 0, 0, -1));
    }

    @Override
    public void render(GuiGraphics g, int i, int j, float f) {
        g.fill(getX(), getY(), getRight(), getBottom(), 0xFF48494B);
        elements(el -> el.render(g, i, j, f));
    }
}
