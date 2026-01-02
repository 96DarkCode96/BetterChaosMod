package eu.darkcode.utils;

import com.google.gson.Gson;
import eu.darkcode.ChaosTags;
import eu.darkcode.server.EventManager;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class Utils {

    public static final int COUNT_VOTE_OPTIONS = 4;
    public static final @NotNull Gson GSON = new Gson();
    public static final String MOD_ID = "betterchaosmod";

    private Utils() {
    }

    public static int getVoteOptionIndex(int pollId, int optionIndex) {
        return (pollId % 2 == 0 ? 1 : 5) + optionIndex;
    }

    @NotNull
    public static Optional<Integer> toOptionIndex(int pollId, int option) {
        if (pollId % 2 == 0)
            return (option >= 1 && option <= 4) ? Optional.of(option - 1) : Optional.empty();
        return (option >= 5 && option <= 8) ? Optional.of(option - 5) : Optional.empty();
    }

    @NotNull
    @Unmodifiable
    public static List<ServerPlayer> getActivePlayers(@NotNull MinecraftServer server) {
        return PlayerLookup.all(server).stream().filter(p -> !p.isSpectator() && !p.isCreative()).toList();
    }

    @Nullable
    public static <T> T loadObject(@NotNull String path, @NotNull Class<T> clazz) throws IOException {
        File file = new File(path);
        if (!file.exists())
            return null;
        try (FileReader fileReader = new FileReader(file)) {
            return GSON.fromJson(fileReader, clazz);
        }
    }

    public static void saveObject(@NotNull String path, Object object) throws IOException {
        File file = new File(path);
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(GSON.toJson(object));
        }
    }

    @NotNull
    @Contract("_ -> new")
    public static ResourceLocation resourceLocation(@NotNull String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }

    public static void teleportPlayer(@NotNull ServerPlayer player, double x, double y, double z) {
        teleportPlayer(player, player.level(), x, y, z);
    }

    public static void teleportPlayer(@NotNull ServerPlayer player, @NotNull ServerLevel level, double x, double y, double z) {
        // Remove roof bedrock so it's possible to get down
        if (level.dimension() == Level.NETHER && y >= 128)
            Utils.clearNetherRoof(player, level);

        player.stopRiding();
        player.teleportTo(level, x, y, z, Set.of(), player.getYRot(), player.getXRot(), true);
    }

    public static void teleportPlayer(@NotNull ServerPlayer player, @NotNull Vec3 pos) {
        teleportPlayer(player, player.level(), pos.x(), pos.y(), pos.z());
    }

    public static void teleportPlayer(@NotNull ServerPlayer player, @NotNull ServerLevel level, @NotNull Vec3 pos) {
        teleportPlayer(player, level, pos.x(), pos.y(), pos.z());
    }

    public static void clearNetherRoof(@NotNull ServerPlayer player, @NotNull ServerLevel level) {
        final int posX = player.blockPosition().getX();
        final int posZ = player.blockPosition().getZ();
        for (int x = -3; x <= 4; x++) {
            for (int z = -3; z <= 4; z++) {
                for (int y = -2; y <= 6; y++) {
                    level.setBlockAndUpdate(new BlockPos(posX + x, 122 + y, posZ + z), Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    /**
     * Makes player surrounding safe
     *
     * @param player player
     */
    public static void clearPlayerArea(@NotNull ServerPlayer player) {
        ServerLevel level = player.level();
        BlockPos blockPos = player.blockPosition();
        Vec3 position = player.position();

        level.destroyBlock(blockPos, false);
        level.destroyBlock(blockPos.above(), false);
        BlockHitResult blockHitResult = level.clip(new ClipContext(position, position.subtract(0, -6, 0), ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, player));
        if (blockHitResult.getType() == HitResult.Type.MISS || level.getBlockState(blockHitResult.getBlockPos()).liquid())
            level.setBlockAndUpdate(blockPos.below(), Blocks.STONE.defaultBlockState());
    }

    @NotNull
    public static Holder.Reference<Item> getRandomItem(@NotNull ServerLevel serverLevel) {
        Holder.Reference<Item> holder;
        do {
            holder = BuiltInRegistries.ITEM.getRandom(RandomSource.create()).orElseThrow();
        } while (holder.is(ChaosTags.DO_NOT_DROP_RANDOMLY) || !holder.value().requiredFeatures().isSubsetOf(serverLevel.enabledFeatures()));
        return holder;
    }

    public static void savePositions(@NotNull EventManager eventManager, @NotNull Map<ServerPlayer, TeleportInfo> positions) {
        eventManager.activePlayers().forEach(player -> {
            BlockPos pos = player.blockPosition();
            positions.put(player, new TeleportInfo(player.level(), pos.getX(), pos.getY(), pos.getZ(), player.getYHeadRot(), player.getXRot()));
        });
    }

    public static void loadPositions(@NotNull EventManager eventManager, @NotNull Map<ServerPlayer, TeleportInfo> positions) {
        eventManager.activePlayers().forEach(player -> {
            TeleportInfo info = positions.get(player);

            player.fallDistance = 0;
            player.teleportTo(info.level, info.x + 0.5D, info.y, info.z + 0.5D, Set.of(), info.yaw, info.pitch, true);
        });
    }

    public record TeleportInfo(@NotNull ServerLevel level, int x, int y, int z, float yaw, float pitch) {
    }
}
