package eu.darkcode.mixin;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.SoundOptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static eu.darkcode.BetterChaosModClient.jumpscareOption;

/**
 * @author _dark_code_
 * @date 30.12.2025
 **/
@Mixin(SoundOptionsScreen.class)
public abstract class SoundOptionsScreenMixin extends OptionsSubScreen {

    public SoundOptionsScreenMixin(Screen screen, Options options, Component component) {
        super(screen, options, component);
    }

    @Inject(method = "getAllSoundOptionsExceptMaster", at = @At("RETURN"), cancellable = true)
    private void injectCustomSoundSource(CallbackInfoReturnable<OptionInstance<?>[]> cir) {
        OptionInstance<?>[] original = cir.getReturnValue();
        OptionInstance<?>[] modified = new OptionInstance<?>[original.length + 1];
        System.arraycopy(original, 0, modified, 0, original.length);

        modified[original.length] = jumpscareOption;

        cir.setReturnValue(modified);
    }

}