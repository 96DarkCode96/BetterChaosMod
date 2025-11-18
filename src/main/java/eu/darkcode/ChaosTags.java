package eu.darkcode;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import static eu.darkcode.utils.Utils.resourceLocation;

public final class ChaosTags {
    public static final TagKey<Block> DO_NOT_REPLACE_BLOCKS = TagKey.create(Registries.BLOCK, resourceLocation("do_not_replace"));
}
