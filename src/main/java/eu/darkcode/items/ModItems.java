package eu.darkcode.items;

import eu.darkcode.utils.Utils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

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
