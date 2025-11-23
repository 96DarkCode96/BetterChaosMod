package eu.darkcode.client.voting;

import eu.darkcode.client.voting.integration.DebugIntegration;
import eu.darkcode.client.voting.integration.Integration;
import eu.darkcode.voting.Poll;
import net.minecraft.util.TriState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

import static eu.darkcode.utils.Utils.COUNT_VOTE_OPTIONS;

public class VotingSystem {

    private final int[] votes = new int[COUNT_VOTE_OPTIONS];
    private int totalVotes = 0;

    private @Nullable Integration activeIntegration = new DebugIntegration();
    private @Nullable Poll currentPoll;
    private @NotNull TriState pollVisibility = TriState.DEFAULT;

    public void processNewPoll(@NotNull Poll poll) {

        this.totalVotes = 0;
        Arrays.fill(this.votes, 0);

        if (this.activeIntegration != null)
            this.activeIntegration.sendPoll(poll);
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

    public @Nullable Integration activeIntegration() {
        return activeIntegration;
    }

    public void activeIntegration(@Nullable Integration activeIntegration) {
        this.activeIntegration = activeIntegration;
    }

    public @Nullable Poll currentPoll() {
        return currentPoll;
    }

    public boolean canViewPoll() {
        return currentPoll != null && pollVisibility.toBoolean(activeIntegration != null);
    }
}