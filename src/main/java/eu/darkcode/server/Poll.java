package eu.darkcode.server;

import eu.darkcode.utils.Utils;
import eu.darkcode.voting.PollDefinition;
import eu.darkcode.voting.VoteEntry;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Poll {
    private final @NotNull PollDefinition definition;
    private final @NotNull Map<Long, String> voters = new HashMap<>();
    private final int[] votes = new int[Utils.COUNT_VOTE_OPTIONS];
    private int totalVotes = 0;

    public Poll(@NotNull PollDefinition definition) {
        this.definition = definition;
    }

    public @NotNull PollDefinition definition() {
        return this.definition;
    }

    public int id() {
        return this.definition.pollId();
    }

    public int totalVotes() {
        return this.totalVotes;
    }

    public int[] votes() {
        return this.votes;
    }

    public void receiveVotes(@NotNull List<VoteEntry> votes) {
        for (VoteEntry vote : votes) {
            int option = vote.voteOption();
            if (option < 0 || option >= this.votes.length || this.voters.containsKey(vote.uid()))
                continue;

            this.votes[option] += vote.voteWeight();
            this.totalVotes += vote.voteWeight();
            this.voters.put(vote.uid(), vote.voterName());
        }
    }

    public int pollResult() {
        if (totalVotes <= 0)
            return -1;

        int max = 0;
        int selected = -1;
        for (int i = 0; i < votes.length; i++) {
            if (votes[i] <= max)
                continue;
            max = votes[i];
            selected = i;
        }
        return selected;
    }
}
