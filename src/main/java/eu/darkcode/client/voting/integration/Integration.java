package eu.darkcode.client.voting.integration;

import eu.darkcode.voting.Poll;
import org.jetbrains.annotations.NotNull;

public interface Integration {
    void sendPoll(@NotNull Poll poll);
}
