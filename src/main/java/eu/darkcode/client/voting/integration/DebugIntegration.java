package eu.darkcode.client.voting.integration;

import eu.darkcode.voting.Poll;
import org.jetbrains.annotations.NotNull;

public class DebugIntegration implements Integration {
    @Override
    public void sendPoll(@NotNull Poll poll) {
        System.out.println("Sending poll: ");
    }
}
