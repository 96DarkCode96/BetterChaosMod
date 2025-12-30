package eu.darkcode.client.hud;

import eu.darkcode.BetterChaosModClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class HudManager {
    private final Map<Class<? extends Hud>, Hud> registry = new HashMap<>();

    public HudManager() {
        registry.put(Timer.class, new Timer());
        registry.put(ActiveEventsPanel.class, new ActiveEventsPanel());
        registry.put(CurrentPollPanel.class, new CurrentPollPanel());


        registry.put(JumpscareImage.class, new JumpscareImage());
    }

    public void render(@NotNull GuiGraphics graphics, @NotNull DeltaTracker deltaTracker, boolean hideGui) {
        registry.forEach((aClass, hud) -> {
            if (hud.enabled(hideGui))
                hud.render(BetterChaosModClient.instance().settings().hud, graphics, deltaTracker);
        });
    }

    @NotNull
    public <T extends Hud> T hud(@NotNull Class<T> hudClass) {
        return hudClass.cast(Objects.requireNonNull(registry.get(hudClass), "Hud element not found!"));
    }
}
