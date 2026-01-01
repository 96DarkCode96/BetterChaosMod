package eu.darkcode.mixin;

import eu.darkcode.client.SharedProperties;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * @author _dark_code_
 * @date 01.01.2026
 **/
@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @ModifyArg(method = "turnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"), index = 0)
    public double invertMouseX(double x) {
        return SharedProperties.invertedMouse ? -x : x;
    }

    @ModifyArg(method = "turnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"), index = 1)
    public double invertMouseY(double y) {
        return SharedProperties.invertedMouse ? -y : y;
    }
}
