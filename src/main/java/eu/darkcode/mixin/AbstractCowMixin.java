package eu.darkcode.mixin;

import eu.darkcode.items.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.cow.AbstractCow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author _dark_code_
 * @date 10.11.2026
 **/
@Mixin(AbstractCow.class)
public abstract class AbstractCowMixin extends Animal {

    protected AbstractCowMixin() {
        super(null, null);
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void injectCustomBuckets(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(ModItems.COPPER_BUCKET) && !this.isBaby()) {
            player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
            ItemStack resultStack = ItemUtils.createFilledResult(itemStack, player, ModItems.MILK_COPPER_BUCKET.getDefaultInstance());
            player.setItemInHand(hand, resultStack);
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
