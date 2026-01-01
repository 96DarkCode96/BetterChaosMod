package eu.darkcode.client.voting;

import eu.darkcode.client.overlay.OverlayWebServer;
import eu.darkcode.client.voting.integration.Integration;
import eu.darkcode.voting.PollDefinition;
import eu.darkcode.voting.VoteEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class VotingSystem {

    private final @NotNull List<Integration> activeIntegrations = new ArrayList<>();
    private @Nullable Poll poll;

    public void processNewPoll(@NotNull PollDefinition definition) {
        this.poll = new Poll(definition);
        for (Integration activeIntegration : this.activeIntegrations)
            activeIntegration.sendPoll(definition);
        OverlayWebServer.sendPollUpdate(this.poll);
    }

    public double percentage(int optionIndex) {
        return poll == null ? 0.0 : poll.percentage(optionIndex);
    }

    public int totalVotes() {
        return poll == null ? 0 : poll.totalVotes();
    }

    @NotNull
    public List<Integration> activeIntegrations() {
        return activeIntegrations;
    }

    public void setActiveIntegrations(@NotNull List<Integration> activeIntegrations) {
        for (Integration activeIntegration : this.activeIntegrations)
            if (!activeIntegrations.contains(activeIntegration))
                activeIntegration.shutdown();
        this.activeIntegrations.clear();

        this.activeIntegrations.addAll(activeIntegrations);
        if (poll != null)
            this.activeIntegrations.forEach(i -> i.sendPoll(this.poll.definition()));
    }

    @Nullable
    public Poll poll() {
        return poll;
    }

    public boolean canViewPoll() {
        return poll != null && poll.definition().visibility().orElse(!activeIntegrations.isEmpty());
    }

    public void receiveUpdate(int pollId, int totalVotes, int @NotNull [] votes) {
        if (poll == null || poll.id() != pollId)
            return;
        poll.receiveUpdate(totalVotes, votes);
        OverlayWebServer.sendPollVotes(totalVotes, votes);
    }

    public void end() {
        for (Integration activeIntegration : this.activeIntegrations)
            activeIntegration.shutdown();
        OverlayWebServer.sendPollUpdate(null);
    }

    public void handleVote(int pollId, @NotNull VoteEntry voteEntry) {
        if (poll == null || poll.id() != pollId)
            return;
        poll.recordVote(voteEntry);
    }
}