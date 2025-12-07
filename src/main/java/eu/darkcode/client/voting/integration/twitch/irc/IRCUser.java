package eu.darkcode.client.voting.integration.twitch.irc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record IRCUser(long userId, @Nullable String displayName, @NotNull String name, int color, boolean vip,
                      boolean mod,
                      boolean subscriber) {
    public @NotNull String effectiveName() {
        return displayName != null ? displayName : name;
    }
}
