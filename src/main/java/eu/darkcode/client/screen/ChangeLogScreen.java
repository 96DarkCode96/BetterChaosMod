package eu.darkcode.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jspecify.annotations.NonNull;

public class ChangeLogScreen extends Screen {
    private static final int PANEL_WIDTH = 310;
    private static final int PANEL_HEIGHT = 400;

    private static final int PADDING = 12;

    private static final int HEADER_HEIGHT = 30;
    private static final int BUTTON_HEIGHT = 30;

    private final Screen parent;

    private int w;
    private int h;

    public ChangeLogScreen(Screen parent) {
        super(Component.translatable("betterchaosmod.changelog.title"));
        this.parent = parent;
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int i, int j, float f) {
        // [RENDER PARENT]
        if (this.parent != null)
            this.parent.extractRenderState(graphics, 0, 0, f);

        ProfilerFiller profilerFiller = Profiler.get();
        profilerFiller.push("ChangeLogScreen");

        // [RENDER BLUR]
        graphics.fillGradient(0, 0, this.width, this.height, 0xCA000000, 0xCA000000);

        int x = (this.width - w) / 2;
        int y = (this.height - h) / 2;

        // [PANEL BORDER]
        graphics.fill(x, y, x + w, y + h, 0xFF1E1E1F);

        // [HEADER BACKGROUND]
        graphics.fill(x + 1, y + 1, x + w - 1, y + HEADER_HEIGHT + 1, 0xFF48494A);

        // [CONTENT BACKGROUND]
        graphics.fill(x + 1, y + HEADER_HEIGHT + 2, x + w - 1, y + h - 2 * PADDING - BUTTON_HEIGHT - 1, 0xFF313233);

        // [FOOTER BACKGROUND]
        graphics.fill(x + 1, y + h - 2 * PADDING - BUTTON_HEIGHT - 1, x + w - 1, y + h - 1, 0xFF48494A);

        super.extractRenderState(graphics, i, j, f);

        profilerFiller.pop();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreenAndShow(parent);
    }

    @Override
    protected void init() {
        
    }

    @Override
    protected void repositionElements() {
        w = PANEL_WIDTH;
        if (w > this.width)
            w = this.width;

        h = PANEL_HEIGHT;
        if (h > this.height)
            h = this.height;
        super.repositionElements();
    }

    @Override
    public void resize(int i, int j) {
        super.resize(i, j);
        if (this.parent != null)
            this.parent.resize(i, j);
    }
}
