package eu.darkcode.client.voting.integration;

import eu.darkcode.voting.PollDefinition;
import org.jetbrains.annotations.NotNull;

public interface Integration {
    void sendPoll(@NotNull PollDefinition poll);

    void shutdown();
}
