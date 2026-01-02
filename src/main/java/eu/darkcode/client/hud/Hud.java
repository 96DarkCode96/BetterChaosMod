package eu.darkcode.client.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public interface Hud {

    void render(HudSettings hudManager, @NotNull GuiGraphics graphics);

    boolean enabled(boolean hideGui);
}
