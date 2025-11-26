package eu.darkcode.client.widgets.ore;

import eu.darkcode.client.widgets.styles.Constraint;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

public abstract class OrePanel extends OreContainer {

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

    public void stretchHorizontal(int maxX, int screenWidth) {
        if (maxX > screenWidth)
            maxX = screenWidth;
        if (maxX < 0)
            maxX = 0;

        setWidth(maxX);
        setX((screenWidth - maxX) / 2);
    }

    @Override
    public @NotNull Constraint padding() {
        return Constraint.ONE;
    }
}
