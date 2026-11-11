package eu.darkcode.items;

import eu.darkcode.utils.Utils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.function.Function;

/**
 * @author _dark_code_
 * @date 09.11.2026
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ModItems {
    public static final Item COPPER_BUCKET;
    public static final Item WATER_COPPER_BUCKET;
    public static final Item POWDER_SNOW_COPPER_BUCKET;
    public static final Item MILK_COPPER_BUCKET;

    static {
        COPPER_BUCKET = registerItem(id("copper_bucket"), p -> new CopperBucketItem(Fluids.EMPTY, p), new Item.Properties().stacksTo(16));
        WATER_COPPER_BUCKET = registerItem(id("water_copper_bucket"), p -> new CopperBucketItem(Fluids.WATER, p), new Item.Properties().craftRemainder(COPPER_BUCKET).stacksTo(1));
        POWDER_SNOW_COPPER_BUCKET = registerItem(id("powder_snow_copper_bucket"), p -> new SolidCopperBucketItem(Blocks.POWDER_SNOW, SoundEvents.BUCKET_EMPTY_POWDER_SNOW, p), new Item.Properties().stacksTo(1).useItemDescriptionPrefix());
        MILK_COPPER_BUCKET = registerItem(id("milk_copper_bucket"), Item::new, new Item.Properties().craftRemainder(COPPER_BUCKET).component(DataComponents.CONSUMABLE, Consumables.MILK_BUCKET).usingConvertsTo(COPPER_BUCKET).stacksTo(1));
    }

    /**
     * empty function to initialize this class
     */
    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(tab -> {
            tab.accept(COPPER_BUCKET);
            tab.accept(WATER_COPPER_BUCKET);
            tab.accept(POWDER_SNOW_COPPER_BUCKET);
            tab.accept(MILK_COPPER_BUCKET);
        });

        // Hook dispenser logic

        DispenseItemBehavior filledBucketBehavior = new DefaultDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

            @Override
            public ItemStack execute(final BlockSource source, final ItemStack dispensed) {
                DispensibleContainerItem bucket = (DispensibleContainerItem)dispensed.getItem();
                BlockPos target = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
                Level level = source.level();
                if (bucket.emptyContents(null, level, target, null)) {
                    bucket.checkExtraContent(null, level, dispensed, target);
                    return this.consumeWithRemainder(source, dispensed, new ItemStack(ModItems.COPPER_BUCKET));
                } else {
                    return this.defaultDispenseItemBehavior.dispense(source, dispensed);
                }
            }
        };
        DispenserBlock.DISPENSER_REGISTRY.put(ModItems.WATER_COPPER_BUCKET, filledBucketBehavior);
        DispenserBlock.DISPENSER_REGISTRY.put(ModItems.POWDER_SNOW_COPPER_BUCKET, filledBucketBehavior);

        DispenserBlock.DISPENSER_REGISTRY.put(ModItems.COPPER_BUCKET, new DefaultDispenseItemBehavior() {
            @Override
            public ItemStack execute(final @NonNull BlockSource source, final @NonNull ItemStack dispensed) {
                LevelAccessor level = source.level();
                BlockPos target = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
                BlockState blockState = level.getBlockState(target);
                Block block = blockState.getBlock();
                if (block instanceof BucketPickup bucket) {
                    ItemStack pickup = bucket.pickupBlock(null, level, target, blockState);
                    if (pickup.isEmpty()) {
                        return super.execute(source, dispensed);
                    } else {
                        level.gameEvent(null, GameEvent.FLUID_PICKUP, target);
                        return this.consumeWithRemainder(source, dispensed, getRemainder(pickup));
                    }
                } else {
                    return super.execute(source, dispensed);
                }
            }

            private ItemStack getRemainder(ItemStack pickup) {
                ItemStack remainder = ItemStack.EMPTY;
                Item targetType = pickup.getItem();
                if (targetType == Items.WATER_BUCKET)
                    remainder = new ItemStack(ModItems.WATER_COPPER_BUCKET);
                else if (targetType == Items.POWDER_SNOW_BUCKET)
                    remainder = new ItemStack(ModItems.POWDER_SNOW_COPPER_BUCKET);
                return remainder;
            }
        });
    }

    private static <T extends Item> T registerItem(final ResourceKey<Item> id, final Function<Item.Properties, T> itemFactory, final Item.Properties properties) {
        T item = itemFactory.apply(properties.setId(id));
        if (item instanceof BlockItem bi) bi.registerBlocks(Item.BY_BLOCK, item);
        return Registry.register(BuiltInRegistries.ITEM, id, item);
    }

    @NotNull
    private static ResourceKey<Item> id(@NotNull String name) {
        return ResourceKey.create(Registries.ITEM, Utils.resourceLocation(name));
    }
}
