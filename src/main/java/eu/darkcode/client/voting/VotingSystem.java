package eu.darkcode.client.voting;

import eu.darkcode.client.overlay.OverlayWebServer;
import eu.darkcode.client.voting.integration.Integration;
import eu.darkcode.voting.Poll;
import net.minecraft.util.TriState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static eu.darkcode.utils.Utils.COUNT_VOTE_OPTIONS;

public class VotingSystem {

    private final int[] votes = new int[COUNT_VOTE_OPTIONS];

    private @NotNull List<Integration> activeIntegrations = new ArrayList<>();
    private int totalVotes = 0;
    private @Nullable Poll currentPoll;
    private @NotNull TriState pollVisibility = TriState.DEFAULT;

    public void processNewPoll(@NotNull Poll poll) {

        this.totalVotes = 0;
        Arrays.fill(this.votes, 0);

        for (Integration activeIntegration : this.activeIntegrations)
            activeIntegration.sendPoll(poll);

        OverlayWebServer.sendPollUpdate(poll);
        this.currentPoll = poll;
    }

    public double percentage(int optionIndex) {
        if (this.totalVotes == 0)
            return 0;
        return (double) this.votes[optionIndex] / this.totalVotes;
    }

    public int totalVotes() {
        return this.totalVotes;
    }

    @NotNull
    public List<Integration> getActiveIntegrations() {
        return activeIntegrations;
    }

    public void setActiveIntegrations(@NotNull List<Integration> activeIntegrations) {
        this.activeIntegrations = activeIntegrations;
        if (currentPoll != null)
            this.activeIntegrations.forEach(i -> i.sendPoll(this.currentPoll));
    }

    public @Nullable Poll currentPoll() {
        return currentPoll;
    }

    public boolean canViewPoll() {
        return currentPoll != null && pollVisibility.toBoolean(!activeIntegrations.isEmpty());
    }

    public void end() {
        for (Integration activeIntegration : this.activeIntegrations)
            activeIntegration.shutdown();
        OverlayWebServer.sendPollUpdate(null);
    }
}