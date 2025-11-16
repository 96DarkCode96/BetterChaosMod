package eu.darkcode.server;

import eu.darkcode.event.Event;
import eu.darkcode.utils.Utils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    public final @NotNull MinecraftServer server;
    private final @NotNull List<Event> activeEvents = new ArrayList<>();

    public EventManager(@NotNull MinecraftServer server) {
        this.server = server;
    }

    @NotNull
    public List<ServerPlayer> activePlayers() {
        return Utils.getActivePlayers(server);
    }

}
