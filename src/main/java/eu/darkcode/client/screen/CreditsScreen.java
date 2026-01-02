package eu.darkcode.client.screen;

import eu.darkcode.BetterChaosModClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.WinScreen;

/**
 * @author _dark_code_
 * @date 02.01.2026
 **/
public class CreditsScreen extends WinScreen {

    public CreditsScreen() {
        super(true, () -> {
        });
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        BetterChaosModClient.instance().hudManager().render(guiGraphics, minecraft.options.hideGui);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
