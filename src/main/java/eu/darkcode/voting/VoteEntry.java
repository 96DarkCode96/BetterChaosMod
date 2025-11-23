package eu.darkcode.voting;

import org.jetbrains.annotations.NotNull;

public record VoteEntry(@NotNull String voterName, int voteOption, int voteWeight) {
}
