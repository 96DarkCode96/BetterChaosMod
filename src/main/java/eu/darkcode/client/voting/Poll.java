package eu.darkcode.client.voting;

import eu.darkcode.networking.ServerboundSendVotesPayload;
import eu.darkcode.utils.Utils;
import eu.darkcode.voting.PollDefinition;
import eu.darkcode.voting.VoteEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class Poll {
    private final @NotNull PollDefinition pollDefinition;
    private final int[] votes = new int[Utils.COUNT_VOTE_OPTIONS];
    private final @NotNull List<VoteEntry> receivedVotes = new ArrayList<>();
    private int totalVotes = 0;

    public Poll(@NotNull PollDefinition pollDefinition) {
        this.pollDefinition = pollDefinition;
    }

    public double percentage(int optionIndex) {
        if (this.totalVotes == 0)
            return 0;
        return (double) this.votes[optionIndex] / this.totalVotes;
    }

    public int totalVotes() {
        return this.totalVotes;
    }

    public @NotNull PollDefinition definition() {
        return this.pollDefinition;
    }

    public int id() {
        return this.pollDefinition.pollId();
    }

    public void receiveUpdate(int totalVotes, int[] votes) {
        this.totalVotes = totalVotes;
        System.arraycopy(votes, 0, this.votes, 0, votes.length);
    }

    public void recordVote(@NotNull VoteEntry voteEntry) {
        synchronized (receivedVotes) {
            this.receivedVotes.add(voteEntry);
        }
    }

    public synchronized void sendVotes() {
        synchronized (receivedVotes) {
            if (this.receivedVotes.isEmpty())
                return;
            ClientPlayNetworking.send(new ServerboundSendVotesPayload(pollDefinition.pollId(), this.receivedVotes.stream().toList()));
            this.receivedVotes.clear();
        }
    }

}
