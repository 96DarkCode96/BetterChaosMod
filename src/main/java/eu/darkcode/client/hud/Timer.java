package eu.darkcode.client.hud;

import eu.darkcode.BetterChaosModClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public class Timer implements Hud {

    private double fraction = 0;

    @Override
    public void render(HudSettings settings, @NotNull GuiGraphics graphics, @NotNull DeltaTracker deltaTracker) {

        double targetFraction = BetterChaosModClient.instance().eventManager().getTimerFraction();
        if (targetFraction < fraction || targetFraction > 0.95) {
            fraction = targetFraction;
        } else {
            fraction += (targetFraction - fraction) * 0.1;
        }

        graphics.fill(0, 0, graphics.guiWidth(), settings.timerHeight(), settings.timerBackgroundColor());
        graphics.fill(0, 0, (int) Math.floor(graphics.guiWidth() * fraction), settings.timerHeight(), settings.timerForegroundColor());
    }

    @Override
    public boolean enabled(boolean hideGui) {
        return BetterChaosModClient.instance().eventManager() != null && !hideGui;
    }
}
