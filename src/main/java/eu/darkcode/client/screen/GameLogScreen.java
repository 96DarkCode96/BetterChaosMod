package eu.darkcode.client.screen;

import eu.darkcode.BetterChaosModClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GameLogScreen extends Screen {

    private static final int VERTICAL_MARGIN = 100;
    private static final int VERTICAL_PADDING = 5;
    public static final int PANEL_WIDTH = 280;

    private final Screen prevScreen;
    public int scrollAmount = 0;

    public GameLogScreen() {
        this(null);
    }

    public GameLogScreen(Screen prevScreen) {
        super(Component.translatable("betterchaosmod.gamelog.title"));
        this.prevScreen = prevScreen;
    }

    @Override
    public void render(GuiGraphics graphics, int i, int j, float f) {
        super.render(graphics, i, j, f);

        int x = (graphics.guiWidth() - PANEL_WIDTH) / 2;

        graphics.fill(x, VERTICAL_MARGIN, x + PANEL_WIDTH, graphics.guiHeight() - VERTICAL_MARGIN, 0xFF23282D);

        List<GameLogEntry> gameLogEntries = BetterChaosModClient.instance().eventManager().getGameLogEntries();

        graphics.enableScissor(x, VERTICAL_MARGIN, x + PANEL_WIDTH, graphics.guiHeight() - VERTICAL_MARGIN);
        graphics.pose().pushMatrix();
        graphics.pose().translate(0.0F, -this.scrollAmount);

        int y = VERTICAL_MARGIN;
        for (GameLogEntry gameLogEntry : gameLogEntries)
            y += gameLogEntry.renderGameLogEntry(graphics, i, j, f, x, y, PANEL_WIDTH) + VERTICAL_PADDING;

        graphics.pose().popMatrix();
        graphics.disableScissor();

    }

    @Override
    public void onClose() {
        assert this.minecraft != null;
        minecraft.setScreen(prevScreen);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean isInGameUi() {
        return true;
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f, double g) {
        scrollAmount -= (int) (g * 5);
        if (scrollAmount < 0)
            scrollAmount = 0;
        return super.mouseScrolled(d, e, f, g);
    }

    public interface GameLogEntry {
        @Environment(EnvType.CLIENT)
        int renderGameLogEntry(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float delta, int x, int y, int width);
    }
}
