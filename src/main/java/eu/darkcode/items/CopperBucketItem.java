package eu.darkcode.items;

import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import static eu.darkcode.items.ModItems.COPPER_BUCKET;

/**
 * @author _dark_code_
 * @date 10.11.2026
 **/
public class CopperBucketItem extends BucketItem {
    public CopperBucketItem(Fluid content, Properties properties) {
        super(content, properties);
    }

    @Override
    public @NonNull InteractionResult use(final @NonNull Level level, final Player player, final @NonNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, this.getFluidContext());
        if (hitResult.getType() == HitResult.Type.MISS || hitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResult.PASS;
        }

        BlockPos pos = hitResult.getBlockPos();
        Direction direction = hitResult.getDirection();
        BlockPos directionOffsetPos = pos.relative(direction);

        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(directionOffsetPos, direction, itemStack)) {
            return InteractionResult.FAIL;
        }

        BlockState clicked = level.getBlockState(pos);
        BlockPos placePos = clicked.getBlock() instanceof LiquidBlockContainer && this.content == Fluids.WATER ? pos : directionOffsetPos;
        if (this.emptyContents(player, level, placePos, hitResult)) {
            this.checkExtraContent(player, level, itemStack, placePos);
            if (player instanceof ServerPlayer serverPlayer && this.content != Fluids.EMPTY) {
                CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, placePos, itemStack);
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            ItemStack emptyResult = ItemUtils.createFilledResult(itemStack, player, player.hasInfiniteMaterials() ? itemStack : new ItemStack(COPPER_BUCKET));
            return InteractionResult.SUCCESS.heldItemTransformedTo(emptyResult);
        }

        if (this.content == Fluids.EMPTY) {
            BlockState blockState = level.getBlockState(pos);
            Block var13 = blockState.getBlock();
            if (var13 instanceof BucketPickup bucketPickupBlock) {
                ItemStack taken = bucketPickupBlock.pickupBlock(player, level, pos, blockState);
                if (!taken.isEmpty()) return pickupFluid(level, player, bucketPickupBlock, pos, itemStack, taken);
            }
        }

        return InteractionResult.FAIL;
    }

    @NotNull
    private InteractionResult.Success pickupFluid(@NotNull Level level, Player player, BucketPickup bucketPickupBlock, BlockPos pos, ItemStack itemStack, ItemStack taken) {
        player.awardStat(Stats.ITEM_USED.get(this));
        bucketPickupBlock.getPickupSound().ifPresent(se -> player.playSound(se, 1.0F, 1.0F));
        level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
        if (!level.isClientSide()) {
            CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, taken);
        }

        if (taken.is(Items.LAVA_BUCKET)) {
            // melt if lava
            if (!level.isClientSide()) {
                level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 1.0f);
                player.igniteForSeconds(5);
            }

            taken = ItemStack.EMPTY;
        }
        else if (taken.is(Items.WATER_BUCKET)) taken = new ItemStack(ModItems.WATER_COPPER_BUCKET);
        else if (taken.is(Items.POWDER_SNOW_BUCKET)) taken = new ItemStack(ModItems.POWDER_SNOW_COPPER_BUCKET);

        return InteractionResult.SUCCESS.heldItemTransformedTo(ItemUtils.createFilledResult(itemStack, player, taken));
    }

}
