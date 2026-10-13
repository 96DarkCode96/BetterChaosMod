package eu.darkcode.mixin;

import eu.darkcode.event.impl.inventory.LockableSlot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

/**
 * @author _dark_code_
 * @date 13.10.2026
 **/
@Environment(EnvType.CLIENT)
@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen {
    @Shadow
    protected Slot hoveredSlot;

    protected AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @Shadow
    public abstract T getMenu();

    @Inject(method = "extractTooltip", at = @At("HEAD"))
    private void betterchaosmod$extractLockedSlotTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (!(this.hoveredSlot instanceof LockableSlot ls) || !this.getMenu().getCarried().isEmpty() || !ls.isLocked())
            return;

        graphics.setTooltipForNextFrame(
                this.font,
                List.of(Component.translatable("filled_map.locked")),
                Optional.empty(),
                mouseX,
                mouseY,
                null,
                true
        );
    }
}
