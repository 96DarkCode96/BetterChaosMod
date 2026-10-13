package eu.darkcode.mixin;

import eu.darkcode.event.impl.inventory.LockableSlot;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author _dark_code_
 * @date 12.10.2026
 **/
@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {

    @Shadow
    protected abstract Slot addSlot(final Slot slot);

    @Inject(method = "addInventoryExtendedSlots", at = @At("HEAD"), cancellable = true)
    private void addInventoryExtendedSlots(Container inventory, int left, int top, CallbackInfo ci) {
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.addSlot(new LockableSlot(inventory, x + (y + 1) * 9, left + x * 18, top + y * 18));
            }
        }
        ci.cancel();
    }

}