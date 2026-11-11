package eu.darkcode.mixin;

import eu.darkcode.items.CopperBucketItem;
import eu.darkcode.items.ModItems;
import eu.darkcode.items.SolidCopperBucketItem;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author _dark_code_
 * @date 11.11.2026
 **/
@Mixin(CauldronInteractions.class)
public class CauldronInteractionsMixin {

    @Inject(method = "bootStrap", at = @At("TAIL"))
    private static void bootStrap(CallbackInfo ci) {
        CauldronInteractions.WATER.put(ModItems.COPPER_BUCKET, (state, level, pos, player, hand, itemInHand) -> CauldronInteractions.fillBucket(state, level, pos, player, hand, itemInHand, new ItemStack(ModItems.WATER_COPPER_BUCKET), s -> s.getValue(LayeredCauldronBlock.LEVEL) == 3, SoundEvents.BUCKET_FILL));
        CauldronInteractions.POWDER_SNOW.put(ModItems.COPPER_BUCKET, (state, level, pos, player, hand, itemInHand) -> CauldronInteractions.fillBucket(state, level, pos, player, hand, itemInHand, new ItemStack(ModItems.POWDER_SNOW_COPPER_BUCKET), s -> s.getValue(LayeredCauldronBlock.LEVEL) == 3, SoundEvents.BUCKET_FILL_POWDER_SNOW));
        CauldronInteractions.LAVA.put(ModItems.COPPER_BUCKET, (_, level, pos, player, _, itemInHand) -> {
            if (!level.isClientSide()) {
                player.awardStat(Stats.USE_CAULDRON);
                player.awardStat(Stats.ITEM_USED.get(itemInHand.getItem()));
                level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
                level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
            }
            return CopperBucketItem.pickupLavaBucket(level, player, pos, itemInHand);
        });
    }

    @Inject(method = "addDefaultInteractions", at = @At("TAIL"))
    private static void addDefaultInteractions(CauldronInteraction.Dispatcher interactionMap, CallbackInfo ci) {
        interactionMap.put(ModItems.WATER_COPPER_BUCKET, CauldronInteractions::fillWaterInteraction);
        interactionMap.put(ModItems.POWDER_SNOW_COPPER_BUCKET, CauldronInteractions::fillPowderSnowInteraction);
    }

    @Redirect(
            method = "emptyBucket",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemUtils;createFilledResult(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"
            )
    )
    private static ItemStack redirectEmptyBucketStack(ItemStack itemStack, Player player, ItemStack newItemStack) {
        if (itemStack.getItem() instanceof CopperBucketItem || itemStack.getItem() instanceof SolidCopperBucketItem) {
            newItemStack = new ItemStack(ModItems.COPPER_BUCKET);
        }

        return ItemUtils.createFilledResult(itemStack, player, newItemStack);
    }

}
