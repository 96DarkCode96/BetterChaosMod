package eu.darkcode.client.screen;

import eu.darkcode.BetterChaosModClient;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.WinScreen;
import org.jspecify.annotations.NonNull;

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
    public void extractRenderState(@NonNull GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
        super.extractRenderState(guiGraphics, i, j, f);
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
