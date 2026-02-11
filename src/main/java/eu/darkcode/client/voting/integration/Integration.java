package eu.darkcode.client.voting.integration;

import eu.darkcode.BetterChaosModClient;
import eu.darkcode.client.EventManager;
import eu.darkcode.client.voting.Poll;
import eu.darkcode.client.voting.VotingSystem;
import eu.darkcode.client.voting.integration.twitch.irc.IRCUser;
import eu.darkcode.utils.Utils;
import eu.darkcode.voting.PollDefinition;
import eu.darkcode.voting.VoteEntry;
import org.jetbrains.annotations.NotNull;

public interface Integration {
    void sendPoll(@NotNull PollDefinition poll);

    void shutdown();


    default void handleVote(@NotNull IRCUser user, @NotNull String message) {
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
}
