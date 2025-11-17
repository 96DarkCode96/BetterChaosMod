package eu.darkcode.mixin;

import eu.darkcode.BetterChaosModClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
abstract class GuiMixin {
    @Final
    @Shadow
    private Minecraft minecraft;

    @Inject(method = "render", at = @At("TAIL"))
    private void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!(minecraft.screen instanceof LevelLoadingScreen)) {
            BetterChaosModClient.instance().hudManager().render(guiGraphics, deltaTracker, this.minecraft.options.hideGui);
        }
    }
}