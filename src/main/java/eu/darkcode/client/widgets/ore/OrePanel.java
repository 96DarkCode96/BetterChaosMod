package eu.darkcode.client.widgets.ore;

import eu.darkcode.client.widgets.ore.layout.Layout;
import eu.darkcode.client.widgets.styles.EdgeInsets;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

public class OrePanel extends OreContainer {

    public OrePanel(@NotNull Layout layout) {
        super(layout);
    }

    @Override
    public void render(GuiGraphics g, int i, int j, float f) {
        ProfilerFiller profiler = Profiler.get();
        profiler.push("OrePanel");

        g.fill(getX(), getY(), getRight(), getBottom(), 0xFF1E1E1F);

        g.enableScissor(getX() + 1, getY() + 1, getRight() - 1, getBottom() - 1);
        g.fill(0, 0, getWidth(), getHeight(), 0xFF313233);

        super.render(g, i, j, f);

        g.disableScissor();

        profiler.pop();
    }

    @Override
    public @NotNull EdgeInsets padding() {
        return EdgeInsets.ONE;
    }
}
