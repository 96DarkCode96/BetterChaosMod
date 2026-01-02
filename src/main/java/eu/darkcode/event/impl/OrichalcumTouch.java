package eu.darkcode.event.impl;

import eu.darkcode.ChaosTags;
import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.TimedEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * @author _dark_code_
 * @date 02.01.2026
 **/
public class OrichalcumTouch extends TimedEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("orichalcum_touch", OrichalcumTouch::new).build();

    @Override
    public void start(@NotNull EventManager manager) {
        // ignored
    }

    @Override
    public void tick(@NotNull EventManager manager) {
        super.tick(manager);
        manager.activePlayers().forEach(player -> {
            int minX = (int) (player.getX() - (player.getX() < 0 ? 2.5 : 1.5));
            int minY = (int) player.getY() - 1;
            int minZ = (int) (player.getZ() - (player.getZ() < 0 ? 2.5 : 1.5));
            int maxX = minX + 2;
            int maxY = minY + 3;
            int maxZ = minZ + 2;

            ServerLevel world = player.level();

            replaceItems(player);
            killEntities(player, minX, minY, minZ, maxX, maxY, maxZ, world);
            replaceBlocks(player, minX, maxX, minY, maxY, minZ, maxZ, world);
        });
    }

    @Override
    public void end(@NotNull EventManager manager) {
        // ignored
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }

    @Override
    public int duration() {
        return super.duration() / 5;
    }

    private static void replaceItems(@NotNull ServerPlayer player) {
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack.isEmpty() || itemStack.is(ChaosTags.IGNORED_BY_TOUCH_ITEM) || itemStack.is(ChaosTags.ORICHALCUM_TOUCH_ITEMS))
                continue;

            Item item = itemStack.getItem();
            ItemStack result = itemStack.copy();
            if (itemStack.has(DataComponents.FOOD)) {
                int chance = player.getRandom().nextInt(100);

                if (chance < 25)
                    result = new ItemStack(Items.CARROT, itemStack.getCount());
                else if (chance < 50)
                    result = new ItemStack(Items.BREAD, itemStack.getCount());
                else if (chance < 75)
                    result = new ItemStack(Items.APPLE, itemStack.getCount());
                else
                    result = new ItemStack(Items.ROTTEN_FLESH, itemStack.getCount());
            } else if (item instanceof BlockItem) {
                result = switch (player.getRandom().nextInt(6)) {
                    case 0 -> new ItemStack(Items.COPPER_BLOCK, Math.min(itemStack.getCount(), 3));
                    case 1 -> new ItemStack(Items.RAW_COPPER_BLOCK, Math.min(itemStack.getCount(), 3));
                    default -> new ItemStack(Items.COPPER_ORE, Math.min(itemStack.getCount(), 10));
                };
            } else if (itemStack.has(DataComponents.EQUIPPABLE)) {
                EquipmentSlot itemSlot = itemStack.get(DataComponents.EQUIPPABLE).slot();
                if (itemSlot == EquipmentSlot.HEAD)
                    result = new ItemStack(Items.COPPER_HELMET, itemStack.getCount());
                else if (itemSlot == EquipmentSlot.CHEST)
                    result = new ItemStack(Items.COPPER_CHESTPLATE, itemStack.getCount());
                else if (itemSlot == EquipmentSlot.LEGS)
                    result = new ItemStack(Items.COPPER_LEGGINGS, itemStack.getCount());
                else if (itemSlot == EquipmentSlot.FEET)
                    result = new ItemStack(Items.COPPER_BOOTS, itemStack.getCount());
                else if (itemSlot == EquipmentSlot.BODY && item != Items.WOLF_ARMOR)
                    result = new ItemStack(Items.COPPER_HORSE_ARMOR, itemStack.getCount());
                else
                    result = new ItemStack(Items.GOLD_INGOT);
            } else if (item instanceof AxeItem) {
                result = new ItemStack(Items.COPPER_AXE, itemStack.getCount());
            } else if (item instanceof ShovelItem) {
                result = new ItemStack(Items.COPPER_SHOVEL, itemStack.getCount());
            } else if (item instanceof HoeItem) {
                result = new ItemStack(Items.COPPER_HOE, itemStack.getCount());
            } else if (itemStack.has(DataComponents.TOOL)) {
                Tool tool = itemStack.get(DataComponents.TOOL);
                List<Tool.Rule> rules = tool.rules();
                for (Tool.Rule rule : rules) {
                    if (rule.correctForDrops().orElse(false)) {
                        Optional<TagKey<Block>> key = rule.blocks().unwrapKey();
                        if (key.isPresent() && key.get().equals(BlockTags.MINEABLE_WITH_PICKAXE)) {
                            result = new ItemStack(Items.COPPER_PICKAXE, itemStack.getCount());
                            break;
                        }
                    } else if (rule.correctForDrops().isEmpty()) {
                        Optional<TagKey<Block>> key = rule.blocks().unwrapKey();
                        if (key.isPresent() && key.get().equals(BlockTags.SWORD_EFFICIENT)) {
                            result = new ItemStack(Items.COPPER_SWORD, itemStack.getCount());
                            break;
                        }
                    }
                }
            } else {
                result = switch (player.getRandom().nextInt(3)) {
                    case 0 -> new ItemStack(Items.COPPER_NUGGET, itemStack.getCount());
                    case 1 -> new ItemStack(Items.RAW_COPPER, itemStack.getCount());
                    default -> new ItemStack(Items.COPPER_INGOT, itemStack.getCount());
                };
            }
            inventory.setItem(i, result);
        }
    }

    private static void killEntities(@NotNull ServerPlayer player, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, @NotNull ServerLevel world) {
        AABB box = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
        List<Entity> mobs = world.getEntities(player, box, x -> {
            if (x instanceof ItemEntity a)
                return !a.getItem().isEmpty() && !a.getItem().is(ChaosTags.IGNORED_BY_TOUCH_ITEM) && !a.getItem().is(ChaosTags.ORICHALCUM_TOUCH_ITEMS);
            if (x instanceof LivingEntity && x.isAlive())
                return !x.getType().is(ChaosTags.IGNORED_BY_TOUCH_ENTITY);
            return false;
        });
        for (Entity mob : mobs) {

            ItemStack itemStack = switch (player.getRandom().nextInt(14)) {
                case 0 -> new ItemStack(Items.COPPER_INGOT);
                case 1 -> new ItemStack(Items.COPPER_BLOCK);
                case 2 -> new ItemStack(Items.RAW_COPPER_BLOCK);
                case 3 -> new ItemStack(Items.COPPER_ORE);
                default -> new ItemStack(Items.COPPER_NUGGET, player.getRandom().nextInt(8) + 2);
            };

            world.addFreshEntity(new ItemEntity(world, mob.getX(), mob.getY(), mob.getZ(), itemStack));
            mob.kill(world);
        }
    }

    private static void replaceBlocks(@NotNull ServerPlayer player, int minX, int maxX, int minY, int maxY, int minZ, int maxZ, @NotNull ServerLevel world) {
        for (int ix = minX; ix <= maxX; ix++) {
            for (int iy = minY; iy <= maxY; iy++) {
                for (int iz = minZ; iz <= maxZ; iz++) {

                    BlockPos blockPos = new BlockPos(ix, iy, iz);
                    if (world.getBlockState(blockPos).is(ChaosTags.IGNORED_BY_ORICHALCUM_TOUCH))
                        continue;

                    int odds = player.getRandom().nextInt(100);

                    if (odds < 96)
                        world.setBlockAndUpdate(blockPos, Blocks.COPPER_ORE.defaultBlockState());
                    else if (odds < 98)
                        world.setBlockAndUpdate(blockPos, Blocks.RAW_COPPER_BLOCK.defaultBlockState());
                    else
                        world.setBlockAndUpdate(blockPos, Blocks.COPPER_BLOCK.defaultBlockState());
                }
            }
        }
    }
}
