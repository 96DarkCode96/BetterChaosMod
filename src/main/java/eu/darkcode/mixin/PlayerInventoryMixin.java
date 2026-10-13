package eu.darkcode.mixin;

import eu.darkcode.event.impl.inventory.Lockable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author _dark_code_
 * @date 12.10.2026
 **/
@Mixin(Inventory.class)
public abstract class PlayerInventoryMixin {

    @Shadow
    @Final
    public Player player;

    @Unique
    private boolean betterchaosmod$isUnlocked(int slot) {
        return !(player instanceof Lockable l) || !l.betterchaosmod$isSlotLocked(slot);
    }

    @Inject(method = "getFreeSlot", at = @At("HEAD"), cancellable = true)
    private void betterchaosmod$onGetFreeSlot(CallbackInfoReturnable<Integer> cir) {
        Inventory self = (Inventory) (Object) this;
        for (int i = 0; i < 36; ++i) {
            if (betterchaosmod$isUnlocked(i) && self.getItem(i).isEmpty()) {
                cir.setReturnValue(i);
                return;
            }
        }
        cir.setReturnValue(-1);
    }

    @Inject(method = "getSlotWithRemainingSpace", at = @At("HEAD"), cancellable = true)
    private void betterchaosmod$onGetSlotWithRemainingSpace(ItemStack newItemStack, CallbackInfoReturnable<Integer> cir) {
        Inventory self = (Inventory) (Object) this;
        if (self.hasRemainingSpaceForItem(self.getItem(self.getSelectedSlot()), newItemStack)) {
            cir.setReturnValue(self.getSelectedSlot());
            return;
        }
        if (self.hasRemainingSpaceForItem(self.getItem(40), newItemStack)) {
            cir.setReturnValue(40);
            return;
        }
        for (int i = 0; i < 36; ++i) {
            if (betterchaosmod$isUnlocked(i) && self.hasRemainingSpaceForItem(self.getItem(i), newItemStack)) {
                cir.setReturnValue(i);
                return;
            }
        }
        cir.setReturnValue(-1);
    }

}
