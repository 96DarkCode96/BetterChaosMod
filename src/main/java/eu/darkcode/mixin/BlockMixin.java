package eu.darkcode.mixin;

import eu.darkcode.client.SharedProperties;
import eu.darkcode.utils.Utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

/**
 * @author _dark_code_
 * @date 31.12.2025
 **/
@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "popResource(Lnet/minecraft/world/level/Level;Ljava/util/function/Supplier;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    private static void injectItemDrop(Level level, Supplier<ItemEntity> supplier, ItemStack itemStack, CallbackInfo ci) {
        // Cancel default method for noDrops & randomDrops
        if (SharedProperties.noDrops || SharedProperties.randomDrops)
            ci.cancel();

        if (SharedProperties.randomDrops && level instanceof ServerLevel serverLevel && !itemStack.isEmpty() && serverLevel.getGameRules().get(GameRules.BLOCK_DROPS)) {
            ItemEntity itemEntity = supplier.get();
            ItemStack itemStack1 = new ItemStack(Utils.getRandomItem(serverLevel));
            itemStack1.setCount(serverLevel.getRandom().nextIntBetweenInclusive(1, itemStack1.getMaxStackSize()));
            itemEntity.setItem(itemStack1);
            itemEntity.setDefaultPickUpDelay();
            level.addFreshEntity(itemEntity);
        }
    }

}
