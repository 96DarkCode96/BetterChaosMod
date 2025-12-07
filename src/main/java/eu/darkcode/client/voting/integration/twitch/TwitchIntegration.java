package eu.darkcode.client.voting.integration.twitch;

import eu.darkcode.BetterChaosModClient;
import eu.darkcode.client.EventManager;
import eu.darkcode.client.voting.Poll;
import eu.darkcode.client.voting.VotingSystem;
import eu.darkcode.client.voting.integration.Integration;
import eu.darkcode.client.voting.integration.twitch.irc.IRCClient;
import eu.darkcode.client.voting.integration.twitch.irc.IRCEvent;
import eu.darkcode.client.voting.integration.twitch.irc.IRCUser;
import eu.darkcode.utils.Utils;
import eu.darkcode.voting.PollDefinition;
import eu.darkcode.voting.VoteEntry;
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

    private static void handleVote(@NotNull IRCUser user, @NotNull String message) {
        int val = message.charAt(0) - '0';
        if (val < 1 || val > 8)
            return;

        EventManager eventManager = BetterChaosModClient.instance().eventManager();
        if (eventManager == null)
            return;

        VotingSystem votingSystem = eventManager.votingSystem();
        Poll poll = votingSystem.poll();
        if (poll == null)
            return;

        Utils.toOptionIndex(poll.id(), val).ifPresent(voteOption -> {
            VoteEntry entry = new VoteEntry(user.userId(), user.effectiveName(), voteOption, 1);
            eventManager.votingSystem().handleVote(poll.id(), entry);
        });
    }

    @Override
    public void sendPoll(@NotNull PollDefinition poll) {
        if (!client.settings.announceNewPolls)
            return;
        try {
            // Format: [1] Entry1 | [2] Entry2 | ... or 5, 6, 7, 8 based on poll id
            StringBuilder message = new StringBuilder("New poll started! ");
            for (int i = 0; i < poll.entries().size(); i++) {
                message.append("[").append(Utils.getVoteOptionIndex(poll.pollId(), i)).append("] ").append(poll.entries().get(i).getString());
                if (i < poll.entries().size() - 1)
                    message.append(" | ");
            }
            client.sendMessage(message.toString());
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
