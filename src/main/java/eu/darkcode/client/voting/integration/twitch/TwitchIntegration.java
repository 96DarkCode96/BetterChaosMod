package eu.darkcode.client.voting.integration.twitch;

import eu.darkcode.BetterChaosModClient;
import eu.darkcode.client.voting.integration.Integration;
import eu.darkcode.client.voting.integration.twitch.irc.IRCClient;
import eu.darkcode.client.voting.integration.twitch.irc.IRCEvent;
import eu.darkcode.client.voting.integration.twitch.irc.IRCUser;
import eu.darkcode.voting.PollDefinition;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class TwitchIntegration implements Integration, IRCEvent {

    private final @NotNull IRCClient client;

    public TwitchIntegration(@NotNull TwitchIntegrationSettings settings) {
        try {
            client = new IRCClient(settings, this);
            client.start();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to connect to twitch!", e);
        }
    }

    @Override
    public void sendPoll(@NotNull PollDefinition poll) {
        if (!client.settings.announceNewPolls)
            return;
        try {
            client.sendMessage(poll.toMessage());
        } catch (Exception e) {
            BetterChaosModClient.LOGGER.warn("Failed to send poll!", e);
        }
    }

    @Override
    public void shutdown() {
        client.close();
    }

    @Override
    public void onMessage(@NotNull IRCUser user, @NotNull String channel, @NotNull String message) {
        message = message.trim();
        if (message.length() == 1) {
            handleVote(user, message);
            return;
        }
    }
}
