package eu.darkcode.utils;

import com.google.gson.Gson;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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
        return ResourceLocation.fromNamespaceAndPath("betterchaosmod", name);
    }
}
