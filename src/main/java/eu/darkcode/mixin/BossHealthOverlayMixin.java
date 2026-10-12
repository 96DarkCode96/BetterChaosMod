package eu.darkcode.mixin;

import eu.darkcode.BetterChaosModClient;
import net.minecraft.client.gui.components.BossHealthOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BossHealthOverlay.class)
public class BossHealthOverlayMixin {

    @ModifyConstant(method = "extractRenderState", constant = @Constant(intValue = 12))
    private int shiftBossBarVerticalPosition(int original) {
        return original + BetterChaosModClient.instance().settings().hud.timerHeight();
    }
}
