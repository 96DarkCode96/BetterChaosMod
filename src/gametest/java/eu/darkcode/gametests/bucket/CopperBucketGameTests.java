package eu.darkcode.gametests.bucket;

import eu.darkcode.items.ModItems;
import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Method;
import java.util.Objects;

public class CopperBucketGameTests implements CustomTestMethodInvoker {

    @Override
    public void invokeTestMethod(
            @NonNull GameTestHelper context,
            @NonNull Method method
    ) throws ReflectiveOperationException {
        method.invoke(this, context);
    }

    /**
     * Collect powder snow using a copper bucket.
     */
    @GameTest
    public void testPowderSnow(GameTestHelper context) {
        BlockPos pos = new BlockPos(1, 1, 1);
        Player player = createPlayer(context, GameType.SURVIVAL);

        context.setBlock(pos, Blocks.POWDER_SNOW);
        context.assertBlockPresent(Blocks.POWDER_SNOW, pos);
        testBucket(context, player, ModItems.COPPER_BUCKET, pos);
        context.assertBlockNotPresent(Blocks.POWDER_SNOW, pos);
        context.assertTrue(
                player.getMainHandItem().is(ModItems.POWDER_SNOW_COPPER_BUCKET),
                "Copper bucket should be filled after collecting powder snow"
        );

        context.succeed();
    }

    /**
     * Collect a water source block.
     */
    @GameTest
    public void testWaterPickup(GameTestHelper context) {
        BlockPos pos = new BlockPos(1, 1, 1);
        Player player = createPlayer(context, GameType.SURVIVAL);

        context.setBlock(pos, Blocks.WATER);
        context.assertBlockPresent(Blocks.WATER, pos);

        testBucket(context, player, ModItems.COPPER_BUCKET, pos);

        context.assertBlockNotPresent(Blocks.WATER, pos);
        context.assertTrue(
                player.getMainHandItem().is(ModItems.WATER_COPPER_BUCKET),
                "Copper bucket should be filled after collecting water"
        );

        context.succeed();
    }

    /**
     * Collect a lava source block.
     */
    @GameTest
    public void testLavaPickup(GameTestHelper context) {
        BlockPos pos = new BlockPos(1, 1, 1);
        Player player = createPlayer(context, GameType.SURVIVAL);

        context.setBlock(pos, Blocks.LAVA);
        testBucket(context, player, ModItems.COPPER_BUCKET, pos);

        context.assertBlockNotPresent(Blocks.LAVA, pos);
        context.assertTrue(
                !player.getMainHandItem().is(ModItems.COPPER_BUCKET),
                "Copper bucket should not be filled after collecting lava"
        );
        context.assertTrue(player.isOnFire(), "Player should be on fire");

        context.succeed();
    }

    /**
     * A copper bucket must not collect flowing water.
     */
    @GameTest
    public void testFlowingWaterPickup(GameTestHelper context) {
        BlockPos pos = new BlockPos(1, 1, 1);
        Player player = createPlayer(context, GameType.SURVIVAL);

        context.setBlock(pos, Blocks.WATER.defaultBlockState()
                .setValue(net.minecraft.world.level.block.LiquidBlock.LEVEL, 1));

        testBucket(context, player, ModItems.COPPER_BUCKET, pos);

        context.assertBlockPresent(Blocks.WATER, pos);
        context.assertTrue(
                player.getMainHandItem().is(ModItems.COPPER_BUCKET),
                "Copper bucket must not collect flowing water"
        );

        context.succeed();
    }

    /**
     * Creative players should retain their empty copper bucket.
     */
    @GameTest
    public void testCreativePickup(GameTestHelper context) {
        BlockPos pos = new BlockPos(1, 1, 1);
        Player player = createPlayer(context, GameType.CREATIVE);

        context.setBlock(pos, Blocks.WATER);
        testBucket(context, player, ModItems.COPPER_BUCKET, pos);

        context.assertBlockNotPresent(Blocks.WATER, pos);
        context.assertTrue(
                player.getMainHandItem().is(ModItems.COPPER_BUCKET),
                "Creative player should retain the empty copper bucket"
        );

        context.succeed();
    }

    /**
     * Place water from a filled copper bucket and return the empty bucket.
     */
    @GameTest
    public void testWaterPlacement(GameTestHelper context) {
        BlockPos pos = new BlockPos(1, 1, 1);
        BlockPos placedPos = pos.above();
        Player player = createPlayer(context, GameType.SURVIVAL);

        context.setBlock(pos, Blocks.STONE);
        testBucket(context, player, ModItems.WATER_COPPER_BUCKET, pos);

        context.assertBlockPresent(Blocks.WATER, placedPos);
        context.assertTrue(
                player.getMainHandItem().is(ModItems.COPPER_BUCKET),
                "Empty copper bucket should remain after placing water"
        );

        context.succeed();
    }

    /**
     * Fill cauldron with a filled copper bucket and return the empty bucket.
     */
    @GameTest
    public void testCauldronWaterPlacement(GameTestHelper context) {
        BlockPos pos = new BlockPos(1, 1, 1);
        Player player = createPlayer(context, GameType.SURVIVAL);

        context.setBlock(pos, Blocks.CAULDRON);
        testBucket(context, player, ModItems.WATER_COPPER_BUCKET, pos);

        context.assertBlockPresent(Blocks.WATER_CAULDRON, pos);
        context.assertBlockPresent(Blocks.AIR, pos.above());

        context.assertTrue(
                player.getMainHandItem().is(ModItems.COPPER_BUCKET),
                "Empty copper bucket should remain after placing water"
        );

        context.succeed();
    }

    /**
     * Use dispenser to place water filled copper bucket and return the empty bucket.
     */
    @GameTest
    public void testDispenserPlacement(GameTestHelper context) {
        BlockPos dispenserPos = new BlockPos(1, 1, 1);
        BlockPos targetPos = new BlockPos(1, 1, 0);
        BlockPos leverPos = new BlockPos(1, 2, 1);

        context.setBlock(dispenserPos, Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING, Direction.NORTH));

        DispenserBlockEntity dispenserEntity = context.getBlockEntity(dispenserPos, DispenserBlockEntity.class);
        dispenserEntity.setItem(0, new ItemStack(ModItems.WATER_COPPER_BUCKET));

        context.setBlock(leverPos, Blocks.OAK_BUTTON);
        context.pressButton(leverPos);

        context.runAfterDelay(4, () -> {
            context.assertBlockPresent(Blocks.WATER, targetPos);
            ItemStack stackInDispenser = dispenserEntity.getItem(0);
            context.assertTrue(stackInDispenser.is(ModItems.COPPER_BUCKET), "Expected empty bucket in dispenser slot 0, found: " + stackInDispenser.getItem());
            context.setBlock(leverPos, Blocks.OAK_BUTTON);
            context.pressButton(leverPos);
        });

        context.runAfterDelay(8, () -> {
            context.assertBlockNotPresent(Blocks.WATER, targetPos);
            ItemStack stackInDispenser = dispenserEntity.getItem(0);
            context.assertTrue(stackInDispenser.is(ModItems.WATER_COPPER_BUCKET), "Expected filled bucket in dispenser slot 0, found: " + stackInDispenser.getItem());
            context.succeed();
        });
    }

    /**
     * Place powder snow from a filled copper bucket and return the empty bucket.
     */
    @GameTest
    public void testPowderSnowPlacement(GameTestHelper context) {
        BlockPos pos = new BlockPos(1, 1, 1);
        BlockPos placedPos = pos.above();
        Player player = createPlayer(context, GameType.SURVIVAL);

        context.setBlock(pos, Blocks.STONE);
        testBucket(context, player, ModItems.POWDER_SNOW_COPPER_BUCKET, pos);

        context.assertBlockPresent(Blocks.POWDER_SNOW, placedPos);
        context.assertTrue(
                player.getMainHandItem().is(ModItems.COPPER_BUCKET),
                "Empty copper bucket should remain after placing powder snow"
        );

        context.succeed();
    }

    /**
     * Milk an adult cow with a copper bucket.
     */
    @GameTest
    public void testCowMilking(GameTestHelper context) {
        BlockPos cowPos = new BlockPos(1, 1, 1);
        Player player = createPlayer(context, GameType.SURVIVAL);
        Cow cow = context.spawn(EntityTypes.COW, cowPos);

        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.COPPER_BUCKET));
        cow.mobInteract(player, InteractionHand.MAIN_HAND);

        context.assertTrue(
                player.getMainHandItem().is(ModItems.MILK_COPPER_BUCKET),
                "Adult cow should fill a copper bucket with milk"
        );

        context.succeed();
    }

    /**
     * Baby cows must not be milkable with a copper bucket.
     */
    @GameTest
    public void testBabyCowCannotBeMilked(GameTestHelper context) {
        BlockPos cowPos = new BlockPos(1, 1, 1);
        Player player = createPlayer(context, GameType.SURVIVAL);
        Cow cow = context.spawn(EntityTypes.COW, cowPos);
        cow.setBaby(true);

        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.COPPER_BUCKET));
        cow.mobInteract(player, InteractionHand.MAIN_HAND);

        context.assertTrue(
                player.getMainHandItem().is(ModItems.COPPER_BUCKET),
                "Baby cow should not fill a copper bucket"
        );

        context.succeed();
    }

    /**
     * Creative players retain their copper bucket after milking an adult cow.
     */
    @GameTest
    public void testCreativeCowMilking(GameTestHelper context) {
        BlockPos cowPos = new BlockPos(1, 1, 1);
        Player player = createPlayer(context, GameType.CREATIVE);
        Cow cow = context.spawn(EntityTypes.COW, cowPos);

        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.COPPER_BUCKET));
        cow.mobInteract(player, InteractionHand.MAIN_HAND);

        context.assertTrue(
                player.getMainHandItem().is(ModItems.COPPER_BUCKET),
                "Creative player should retain the empty copper bucket after milking"
        );

        context.succeed();
    }

    @NotNull
    private Player createPlayer(@NotNull GameTestHelper context, @NotNull GameType gameType) {
        return context.makeMockServerPlayer(gameType);
    }

    private void testBucket(
            GameTestHelper context,
            Player player,
            Item bucket,
            BlockPos pos
    ) {
        BlockPos absolutePos = context.absolutePos(pos);
        Vec3 center = Vec3.atCenterOf(absolutePos);

        player.setPos(center.x(), center.y() + 2.0D, center.z());
        player.setXRot(90.0F);
        player.setYRot(0.0F);

        ServerPlayer serverPlayer = (ServerPlayer) player;
        serverPlayer.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(bucket));

        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

        BlockHitResult hitResult = new BlockHitResult(center, Direction.UP, absolutePos, false);
        serverPlayer.gameMode.useItemOn(serverPlayer, context.getLevel(), stack, InteractionHand.MAIN_HAND, hitResult);

        stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.is(bucket) && stack.use(context.getLevel(), player, InteractionHand.MAIN_HAND) instanceof InteractionResult.Success a) {
            player.setItemInHand(InteractionHand.MAIN_HAND, Objects.requireNonNull(a.heldItemTransformedTo()));
        }

    }
}
