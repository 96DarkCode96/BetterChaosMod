package eu.darkcode.client.voting.integration.twitch.irc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public interface IRCEvent {
    void onMessage(@NotNull IRCUser user, @NotNull String channel, @NotNull String message);
}
