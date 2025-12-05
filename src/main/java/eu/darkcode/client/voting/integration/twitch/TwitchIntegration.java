package eu.darkcode.client.voting.integration.twitch;

import eu.darkcode.client.voting.integration.Integration;
import eu.darkcode.voting.Poll;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class TwitchIntegration implements Integration {

    private final @NotNull IRCClient client;

    public TwitchIntegration(@NotNull TwitchIntegrationSettings settings) {
        try {
            client = new IRCClient(settings);
            client.start();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to connect to twitch!", e);
        }
    }

    @Override
    public void sendPoll(@NotNull Poll poll) {
        if (!client.settings.announceNewPolls)
            return;
        try {
            client.sendMessage(poll.pollId() + " ");
        } catch (IOException e) {
        }
    }

    @Override
    public void shutdown() {
        client.close();
    }
}
