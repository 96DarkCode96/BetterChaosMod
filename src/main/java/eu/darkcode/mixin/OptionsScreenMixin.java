package eu.darkcode.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import eu.darkcode.client.screen.ConfigScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {

    protected OptionsScreenMixin(Component title) {
        super(title);
    }

    @Shadow
    protected abstract Button openScreenButton(Component name, Supplier<Screen> screenSupplier);

    @Inject(method = "init", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/layouts/HeaderAndFooterLayout;addToContents(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;",
            shift = At.Shift.BEFORE
    ))
    private void insertModButton(CallbackInfo ci, @Local GridLayout.RowHelper rowHelper) {
        rowHelper.addChild(this.openScreenButton(
                Component.translatable("betterchaosmod.options.title"),
                () -> new ConfigScreen((OptionsScreen) (Object) this)
        ));
    }

}