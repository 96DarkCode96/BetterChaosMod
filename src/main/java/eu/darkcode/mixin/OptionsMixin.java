package eu.darkcode.mixin;

import eu.darkcode.BetterChaosModClient;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author _dark_code_
 * @date 30.12.2025
 **/
@Mixin(Options.class)
public class OptionsMixin {

    @Inject(method = "processOptions", at = @At("TAIL"))
    private void injectCustomOptions(Options.FieldAccess accessor, CallbackInfo ci) {
        accessor.process("soundCategory.jumpscare", BetterChaosModClient.jumpscareOption);
    }
}
