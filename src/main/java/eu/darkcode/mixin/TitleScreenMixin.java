package eu.darkcode.mixin;

import eu.darkcode.BetterChaosModClient;
import eu.darkcode.UpdateStatus;
import eu.darkcode.client.ClientSettings;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        ClientSettings settings = BetterChaosModClient.instance().settings();
        if (settings.lastShownChangelog == null || !settings.lastShownChangelog.equals(UpdateStatus.getModVersion())) {
            settings.lastShownChangelog = UpdateStatus.getModVersion();
            BetterChaosModClient.instance().saveSettings();

            assert this.minecraft != null;
            //this.minecraft.setScreen(new ChangeLogScreen(this));
        }
    }
}
