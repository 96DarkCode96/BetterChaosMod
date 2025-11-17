package eu.darkcode.client.hud;

import eu.darkcode.BetterChaosModClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class HudManager {
    private final Map<Class<? extends Hud>, Hud> registry = new HashMap<>();

    public HudManager() {
        registry.put(Timer.class, new Timer());
        registry.put(ActiveEventsPanel.class, new ActiveEventsPanel());
    }

    public void render(@NotNull GuiGraphics graphics, @NotNull DeltaTracker deltaTracker, boolean hideGui) {
        registry.forEach((aClass, hud) -> {
            if (hud.enabled(hideGui))
                hud.render(BetterChaosModClient.instance().settings().hud, graphics, deltaTracker);
        });
    }
}
