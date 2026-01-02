package eu.darkcode;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static eu.darkcode.utils.Utils.resourceLocation;

public final class ChaosTags {

    public static final TagKey<Block> DO_NOT_REPLACE_BLOCKS = TagKey.create(Registries.BLOCK, resourceLocation("do_not_replace"));
    public static final TagKey<Block> IGNORED_BY_ORICHALCUM_TOUCH = TagKey.create(Registries.BLOCK, resourceLocation("ignored_by_orichalcum_touch"));
    public static final TagKey<Block> IGNORED_BY_MIDAS_TOUCH = TagKey.create(Registries.BLOCK, resourceLocation("ignored_by_midas_touch"));

    public static final TagKey<EntityType<?>> DO_NOT_EXPLODE_ENTITY = TagKey.create(Registries.ENTITY_TYPE, resourceLocation("do_not_explode"));
    public static final TagKey<EntityType<?>> IGNORED_BY_TOUCH_ENTITY = TagKey.create(Registries.ENTITY_TYPE, resourceLocation("ignored_by_touch"));

    public static final TagKey<Item> DO_NOT_DROP_RANDOMLY = TagKey.create(Registries.ITEM, resourceLocation("do_not_drop_randomly"));
    public static final TagKey<Item> IGNORED_BY_TOUCH_ITEM = TagKey.create(Registries.ITEM, resourceLocation("ignored_by_touch"));
    public static final TagKey<Item> ORICHALCUM_TOUCH_ITEMS = TagKey.create(Registries.ITEM, resourceLocation("orichalcum_touch_items"));
    public static final TagKey<Item> MIDAS_TOUCH_ITEMS = TagKey.create(Registries.ITEM, resourceLocation("midas_touch_items"));

}
