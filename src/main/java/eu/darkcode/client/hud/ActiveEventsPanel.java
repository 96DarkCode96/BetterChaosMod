package eu.darkcode.client.hud;

import eu.darkcode.BetterChaosModClient;
import eu.darkcode.event.Event;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ActiveEventsPanel implements Hud {
    private static final int TOP_PADDING = 20;

    @Override
    public void render(HudSettings settings, @NotNull GuiGraphics graphics, @NotNull DeltaTracker deltaTracker) {
        List<Event> activeEvents = BetterChaosModClient.instance().eventManager().getActiveEvents();
        int y = settings.timerHeight + TOP_PADDING;
        for (Event activeEvent : activeEvents) {
            Component component = activeEvent.getName();

            Font font = Minecraft.getInstance().font;
            int size = font.width(component);

            int width = graphics.guiWidth();
            graphics.drawString(font, component, width - size - 30, y, 0xFFFFFFFF);
            if (!activeEvent.ended()) {
                int startX = width - 25;
                graphics.fill(startX, y + 1, startX + 20, y + 8, 0xFF999999);
                graphics.fill(startX, y + 1, startX + (int) (20 * ((double) activeEvent.currentTick() / activeEvent.duration())), y + 8, 0xFFFFFFFF);
            }

            y += settings.activeEventsVerticalSpacing;
        }
    }

    @Override
    public boolean enabled(boolean hideGui) {
        return BetterChaosModClient.instance().eventManager() != null && !hideGui;
    }
}
