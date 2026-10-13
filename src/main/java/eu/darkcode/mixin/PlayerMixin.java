package eu.darkcode.mixin;

import eu.darkcode.event.impl.inventory.Lockable;
import eu.darkcode.networking.ClientboundSetLockedSlotsPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

/**
 * @author _dark_code_
 * @date 13.10.2026
 **/
@Mixin(Player.class)
public class PlayerMixin implements Lockable {
    @Shadow
    @Final
    private Inventory inventory;
    @Unique
    private int betterchaosmod$lockedSlots = 0;

    @Override
    public int betterchaosmod$getLockedSlots() {
        return this.betterchaosmod$lockedSlots;
    }

    @Override
    public void betterchaosmod$setLockedSlots(int lockedSlots) {
        this.betterchaosmod$lockedSlots = Math.clamp(lockedSlots, 0, 27);
        for (int i = 9, max = 9 + this.betterchaosmod$lockedSlots; i < max; i++) {
            moveItemFromLockedSlot(i);
        }
        if (((Player) (Object) this) instanceof ServerPlayer sp && sp.connection != null) {
            ServerPlayNetworking.send(sp, new ClientboundSetLockedSlotsPayload(this.betterchaosmod$lockedSlots));
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void betterchaosmod$injectSavedData(ValueOutput output, CallbackInfo ci) {
        ValueOutput child = output.child("BetterChaosMod");
        child.putInt("lockedSlots", betterchaosmod$lockedSlots);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void betterchaosmod$injectReadData(ValueInput input, CallbackInfo ci) {
        Optional<ValueInput> opt = input.child("BetterChaosMod");
        if (opt.isEmpty()) return;
        ValueInput child = opt.get();

        child.getInt("lockedSlots").ifPresent(this::betterchaosmod$setLockedSlots);
    }

    @Unique
    private void moveItemFromLockedSlot(int slot) {
        ItemStack itemStack = inventory.getItem(slot);
        if (!itemStack.isEmpty()) {
            ItemEntity drop = inventory.player.createItemStackToDrop(itemStack, true, false);
            if (drop != null) {
                inventory.player.level().addFreshEntity(drop);
            }

            inventory.setItem(slot, ItemStack.EMPTY);
        }
    }
}
