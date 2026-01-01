package eu.darkcode.mixin;

import eu.darkcode.ChaosTags;
import eu.darkcode.client.SharedProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

        if (SharedProperties.randomDrops && level instanceof ServerLevel serverLevel && !itemStack.isEmpty() && serverLevel.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            ItemEntity itemEntity = supplier.get();
            ItemStack itemStack1 = new ItemStack(getRandomItem(serverLevel));
            itemStack1.setCount(serverLevel.random.nextIntBetweenInclusive(1, itemStack1.getMaxStackSize()));
            itemEntity.setItem(itemStack1);
            itemEntity.setDefaultPickUpDelay();
            level.addFreshEntity(itemEntity);
        }
    }

    @Unique
    @NotNull
    private static Holder.Reference<Item> getRandomItem(@NotNull ServerLevel serverLevel) {
        Holder.Reference<Item> holder;
        do {
            holder = BuiltInRegistries.ITEM.getRandom(RandomSource.create()).orElseThrow();
        } while (holder.is(ChaosTags.DO_NOT_DROP_RANDOMLY) || !holder.value().requiredFeatures().isSubsetOf(serverLevel.enabledFeatures()));
        return holder;
    }

    @Inject(method = "shouldRenderFace", at = @At("HEAD"), cancellable = true)
    private static void renderFace(BlockState blockState, BlockState blockState2, Direction direction, CallbackInfoReturnable<Boolean> cir){
        if(SharedProperties.noWorld){
            cir.setReturnValue(false);
            return;
        }
    }

}
