package eu.darkcode.client.widgets.config;

import eu.darkcode.client.widgets.OreElement;
import eu.darkcode.client.widgets.container.AbstractLayout;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;

@Environment(EnvType.CLIENT)
public abstract class OreOption extends AbstractLayout<OreElement> implements Renderable {

    protected OreOption() {
    }

    protected OreOption(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void render(GuiGraphics g, int i, int j, float f) {
        g.fill(getX(), getY(), getRight(), getY() + 1, 0xFF5A5B5D);
        g.fill(getX(), getY() + 1, getRight(), getBottom(), 0xFF48494B);
    }
}
