package eu.darkcode.items;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.NonNull;

import static eu.darkcode.items.ModItems.COPPER_BUCKET;

/**
 * @author _dark_code_
 * @date 10.11.2026
 **/
public class SolidCopperBucketItem extends SolidBucketItem {

    public SolidCopperBucketItem(Block content, SoundEvent placeSound, Properties properties) {
        super(content, placeSound, properties);
    }

    @Override
    public @NonNull InteractionResult useOn(final @NonNull UseOnContext context) {
        InteractionResult placeResult = super.useOn(context);
        Player player = context.getPlayer();
        if (placeResult.consumesAction() && player != null) {
            player.setItemInHand(context.getHand(), player.hasInfiniteMaterials() ? context.getItemInHand() : new ItemStack(COPPER_BUCKET));
        }

        return placeResult;
    }
}
