package eu.darkcode.client.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public interface Hud {

    void render(HudSettings hudManager, @NotNull GuiGraphicsExtractor graphics);

    boolean enabled(boolean hideGui);
}
