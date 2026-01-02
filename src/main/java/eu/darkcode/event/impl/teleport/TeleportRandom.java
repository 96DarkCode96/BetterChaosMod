package eu.darkcode.event.impl.teleport;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventCategory;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import eu.darkcode.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author _dark_code_
 * @date 02.01.2026
 **/
public class TeleportRandom extends InstantEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("teleport_random", TeleportRandom::new).category(EventCategory.TELEPORT).build();

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            player.stopRiding();
            spread(player);
            Utils.clearPlayerArea(player);
        });
    }

    private static void spread(@NotNull ServerPlayer player) {
        ServerLevel level = player.level();
        DimensionType dimensionType = level.dimensionType();
        RandomSource randomSource = player.getRandom();

        Vec2 vec2 = new Vec2((float) player.position().x(), (float) player.position().z());

        int i = dimensionType.hasCeiling() ? (dimensionType.logicalHeight() - 1) : (level.getMaxY() + 1);

        double g = 3000;
        double d = vec2.x - g;
        double e = vec2.y - g;
        double h = vec2.x + g;
        double k = vec2.y + g;

        Position position = new Position();
        position.randomize(randomSource, d, e, h, k);

        spreadPosition(level, randomSource, d, e, h, k, i, position);
        player.teleportTo(level, (double) Mth.floor(position.x) + (double) 0.5F, position.getSpawnY(level, i), (double) Mth.floor(position.z) + (double) 0.5F, Set.of(), player.getYRot(), player.getXRot(), true);
    }

    private static void spreadPosition(@NotNull ServerLevel serverLevel, @NotNull RandomSource randomSource, double e, double f, double g, double h, int i, @NotNull Position position) {
        boolean bl2 = true;

        int k;
        for (k = 0; k < 10000 && bl2; ++k) {
            bl2 = position.clamp(e, f, g, h);

            if (!bl2 && !position.isSafe(serverLevel, i)) {
                position.randomize(randomSource, e, f, g, h);
                bl2 = true;
            }
        }
    }

    static class Position {
        double x;
        double z;

        public boolean clamp(double d, double e, double f, double g) {
            boolean bl = false;
            if (this.x < d) {
                this.x = d;
                bl = true;
            } else if (this.x > f) {
                this.x = f;
                bl = true;
            }

            if (this.z < e) {
                this.z = e;
                bl = true;
            } else if (this.z > g) {
                this.z = g;
                bl = true;
            }
            return bl;
        }

        public int getSpawnY(@NotNull BlockGetter blockGetter, int i) {
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(this.x, i + 1, this.z);
            boolean bl = blockGetter.getBlockState(mutableBlockPos).isAir();
            mutableBlockPos.move(Direction.DOWN);

            boolean bl3;
            for (boolean bl2 = blockGetter.getBlockState(mutableBlockPos).isAir(); mutableBlockPos.getY() > blockGetter.getMinY(); bl2 = bl3) {
                mutableBlockPos.move(Direction.DOWN);
                bl3 = blockGetter.getBlockState(mutableBlockPos).isAir();
                if (!bl3 && bl2 && bl)
                    return mutableBlockPos.getY() + 1;

                bl = bl2;
            }

            return i + 1;
        }

        public boolean isSafe(@NotNull BlockGetter blockGetter, int i) {
            BlockPos blockPos = BlockPos.containing(this.x, this.getSpawnY(blockGetter, i) - 1D, this.z);
            BlockState blockState = blockGetter.getBlockState(blockPos);
            return blockPos.getY() < i && !blockState.liquid() && !blockState.is(BlockTags.FIRE);
        }

        public void randomize(@NotNull RandomSource randomSource, double d, double e, double f, double g) {
            this.x = Mth.nextDouble(randomSource, d, f);
            this.z = Mth.nextDouble(randomSource, e, g);
        }
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
