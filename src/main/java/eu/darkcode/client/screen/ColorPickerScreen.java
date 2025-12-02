package eu.darkcode.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntConsumer;

import static eu.darkcode.client.widgets.utils.ColorUtils.hsvToArgb;
import static eu.darkcode.client.widgets.utils.ColorUtils.rgbToHsv;

public class ColorPickerScreen extends Screen {

    private static final int CANVAS_SIZE = 200;
    private static final int INFO_HEIGHT = 50;

    private final @Nullable Screen parent;
    private final @NotNull IntConsumer valueConsumer;
    // store as HSV + alpha
    private float hue; // 0..360
    private float sat; // 0..1
    private float val; // 0..1
    private int alpha; // 0..255
    // computed layout (precomputed in init)
    private int canvasX;
    private int canvasY;
    private int hueX;
    private int hueY;
    private int hueW;
    private int alphaX;
    private int alphaY;
    private int alphaW;

    private boolean draggingCanvas = false;
    private boolean draggingHue = false;
    private boolean draggingAlpha = false;

    public ColorPickerScreen(int color, @NotNull IntConsumer valueConsumer) {
        super(Component.empty());
        this.parent = Minecraft.getInstance().screen;
        this.valueConsumer = valueConsumer;

        float[] hsv = rgbToHsv(color);
        this.hue = hsv[0];
        this.sat = hsv[1];
        this.val = hsv[2];
        this.alpha = (color >>> 24) & 0xFF;
    }

    @Override
    public void render(@NotNull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        ProfilerFiller profiler = Profiler.get();
        profiler.push("ColorPickerScreen");

        profiler.push("parent");
        if (parent != null)
            parent.render(g, 0, 0, partialTick);
        profiler.pop();

        profiler.push("blur");
        // dim background
        g.fill(0, 0, width, height, 0x88000000);
        profiler.pop();

        profiler.push("picker");
        super.render(g, mouseX, mouseY, partialTick);

        profiler.push("background");
        drawPanelBackground(g);
        profiler.pop();

        // current components from internal HSV
        int curA = alpha;
        float curHue = hue;
        float curSat = sat;
        float curVal = val;

        profiler.push("canvas");
        // --- draw canvas: X -> Saturation (0..1), Y -> Value (0..1) ---
        for (int x = 0; x < CANVAS_SIZE; x++) {
            float s = x * 1.0f / (CANVAS_SIZE - 1);
            int topCol = hsvToArgb(255, curHue, s, 1.0f);    // v = 1 (top)
            int bottomCol = hsvToArgb(255, curHue, s, 0.0f); // v = 0 (bottom)
            g.fillGradient(canvasX + x, canvasY, canvasX + x + 1, canvasY + CANVAS_SIZE, topCol, bottomCol);
        }
        profiler.pop();

        profiler.push("hue");

        g.fillGradient(hueX, hueY, hueX + hueW, hueY + 33, 0xFFFF0000, 0xFFFFFF00);
        g.fillGradient(hueX, hueY + 33, hueX + hueW, hueY + 66, 0xFFFFFF00, 0xFF00FF00);
        g.fillGradient(hueX, hueY + 66, hueX + hueW, hueY + 100, 0xFF00FF00, 0xFF00FFFF);
        g.fillGradient(hueX, hueY + 100, hueX + hueW, hueY + 133, 0xFF00FFFF, 0xFF0000FF);
        g.fillGradient(hueX, hueY + 133, hueX + hueW, hueY + 166, 0xFF0000FF, 0xFFFF00FF);
        g.fillGradient(hueX, hueY + 166, hueX + hueW, hueY + 200, 0xFFFF00FF, 0xFFFF0000);

        // hue indicator (thin horizontal line across hue bar)
        int huePosY = hueY + (int) (curHue * (CANVAS_SIZE - 1) / 360.0f);
        g.fill(hueX - 2, huePosY - 1, hueX + hueW + 2, huePosY + 1, 0xFFFFFFFF);
        g.fill(hueX - 1, huePosY - 1, hueX + hueW + 1, huePosY + 1, 0xFF000000);
        profiler.pop();

        profiler.push("alpha");
        // --- draw alpha slider with gradient (transparent -> opaque for current HSV color) ---
        g.fillGradient(alphaX, alphaY, alphaX + alphaW, alphaY + CANVAS_SIZE, 0xFF000000, 0xFFFFFFFF);

        // alpha indicator (thin horizontal bar across slider)
        int alphaPosY = alphaY + (int) (curA * (CANVAS_SIZE - 1) / 255.0);
        g.fill(alphaX - 2, alphaPosY - 1, alphaX + alphaW + 2, alphaPosY + 1, 0xFFFFFFFF);
        g.fill(alphaX - 1, alphaPosY - 1, alphaX + alphaW + 1, alphaPosY + 1, 0xFF000000);
        profiler.pop();

        profiler.push("selection");
        // selection coordinates on canvas
        int selX = canvasX + (int) (curSat * (CANVAS_SIZE - 1));
        int selY = canvasY + (int) ((1.0f - curVal) * (CANVAS_SIZE - 1)); // top = v=1, bottom = v=0
        drawSelectionRect(g, selX, selY);
        profiler.pop();

        profiler.pop(); // picker
        profiler.push("info");
        // info text and preview (compose ARGB for display)
        printInfo(g, curA, curHue, curSat, curVal);
        profiler.pop(); // info
        profiler.pop(); // ColorPickerScreen
    }

    @Override
    public void onClose() {
        // compose ARGB from internal HSV+alpha and pass to consumer
        int composed = hsvToArgb(alpha, hue, sat, val);
        valueConsumer.accept(composed);
        assert minecraft != null;
        minecraft.setScreen(parent);
    }

    @Override
    protected void init() {
        hueW = 12;
        alphaW = 12;

        canvasX = (width - CANVAS_SIZE - hueW - alphaW - 16) / 2;
        canvasY = (height - CANVAS_SIZE - 8 - INFO_HEIGHT) / 2;

        hueX = canvasX + CANVAS_SIZE + 8;
        hueY = canvasY;

        alphaX = hueX + hueW + 8;
        alphaY = canvasY;
    }

    @Override
    public void resize(Minecraft minecraft, int i, int j) {
        super.resize(minecraft, i, j);
        if (this.parent != null)
            this.parent.resize(minecraft, i, j);
    }

    private void printInfo(@NotNull GuiGraphics g, int curA, float curHue, float curSat, float curVal) {
        int composed = hsvToArgb(curA, curHue, curSat, curVal);
        String hex = String.format("#%08X", composed);
        int infoX = canvasX;
        int infoY = canvasY + CANVAS_SIZE + 8;
        g.drawString(font, Component.literal("HEX: " + hex), infoX, infoY, 0xFFFFFFFF);
        g.drawString(font, Component.literal("HUE: " + curHue), infoX, infoY + 9, 0xFFFFFFFF);
        g.drawString(font, Component.literal("SAT: " + curSat), infoX, infoY + 18, 0xFFFFFFFF);
        g.drawString(font, Component.literal("VAL: " + curVal), infoX, infoY + 27, 0xFFFFFFFF);
    }

    private void drawPanelBackground(@NotNull GuiGraphics g) {
        int panelX = canvasX - 6;
        int panelY = canvasY - 6;
        int panelW = CANVAS_SIZE + hueW + alphaW + 8 + 8 + 12;
        int panelH = CANVAS_SIZE + 8 + INFO_HEIGHT + 20;
        // panel background
        g.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xFF313233);
        // panel border
        g.fill(panelX, panelY, panelX + panelW, panelY + 1, 0xFF1E1E1F);
        g.fill(panelX, panelY + panelH - 1, panelX + panelW, panelY + panelH, 0xFF1E1E1F);
        g.fill(panelX, panelY, panelX + 1, panelY + panelH, 0xFF1E1E1F);
        g.fill(panelX + panelW - 1, panelY, panelX + panelW, panelY + panelH, 0xFF1E1E1F);
    }

    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent mouseButtonEvent, boolean bl) {
        double mouseX = mouseButtonEvent.x();
        double mouseY = mouseButtonEvent.y();
        if (insideCanvas(mouseX, mouseY)) {
            updateColorFromCanvas(mouseX, mouseY);
            draggingCanvas = true;
            return true;
        } else if (insideHue(mouseX, mouseY)) {
            updateHueFromBar(mouseY);
            draggingHue = true;
            return true;
        } else if (insideAlpha(mouseX, mouseY)) {
            updateAlphaFromSlider(mouseY);
            draggingAlpha = true;
            return true;
        }
        return super.mouseClicked(mouseButtonEvent, bl);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
        draggingCanvas = false;
        draggingHue = false;
        draggingAlpha = false;
        return super.mouseReleased(mouseButtonEvent);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double d, double e) {
        double mouseX = mouseButtonEvent.x();
        double mouseY = mouseButtonEvent.y();
        if (draggingCanvas) {
            updateColorFromCanvas(mouseX, mouseY);
            return true;
        } else if (draggingHue) {
            updateHueFromBar(mouseY);
            return true;
        } else if (draggingAlpha) {
            updateAlphaFromSlider(mouseY);
            return true;
        }
        return super.mouseDragged(mouseButtonEvent, d, e);
    }

    private boolean insideCanvas(double mx, double my) {
        return mx >= canvasX && mx < canvasX + CANVAS_SIZE && my >= canvasY && my < canvasY + CANVAS_SIZE;
    }

    private boolean insideHue(double mx, double my) {
        return mx >= hueX && mx < hueX + hueW && my >= hueY && my < hueY + CANVAS_SIZE;
    }

    private boolean insideAlpha(double mx, double my) {
        return mx >= alphaX && mx < alphaX + alphaW && my >= alphaY && my < alphaY + CANVAS_SIZE;
    }

    private void updateColorFromCanvas(double mx, double my) {
        int localX = clamp((int) (mx - canvasX));
        int localY = clamp((int) (my - canvasY));
        sat = localX / (float) Math.max(1, CANVAS_SIZE - 1);
        val = 1.0f - (localY / (float) Math.max(1, CANVAS_SIZE - 1)); // top=1, bottom=0
    }

    private void updateHueFromBar(double my) {
        int localY = clamp((int) (my - hueY));
        hue = localY * 360.0f / Math.max(1, CANVAS_SIZE - 1);
    }

    private void updateAlphaFromSlider(double my) {
        int localY = clamp((int) (my - alphaY));
        alpha = (int) (localY * 255.0 / Math.max(1, CANVAS_SIZE - 1));
    }

    // --- HSV / RGB helpers ---

    private int clamp(int v) {
        return Math.clamp(v, 0, 199);
    }

    private void drawSelectionRect(@NotNull GuiGraphics g, int cx, int cy) {
        int left = cx - 4;
        int top = cy - 4;
        // outer white border
        g.fill(left - 1, top - 1, left + 8 + 1, top + 8 + 1, 0xFFFFFFFF);
        // inner black border
        g.fill(left, top, left + 8, top + 8, 0xFF000000);
        // center small sample using current composed ARGB (1px inset)
        g.fill(left + 1, top + 1, left + 8 - 1, top + 8 - 1, hsvToArgb(alpha, hue, sat, val));
    }

}