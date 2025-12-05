package eu.darkcode.mixin;

import com.mojang.blaze3d.platform.Window;
import eu.darkcode.client.overlay.OverlayWebServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public abstract class WindowMixin implements AutoCloseable {
    @Shadow
    private int guiScale;

    @Inject(method = "setGuiScale", at = @At("HEAD"))
    public void onSetGuiScale(int i, CallbackInfo ci) {
        if (guiScale != i) {
            guiScale = i;
            OverlayWebServer.sendStyleUpdate();
        }
    }
}
