package eu.darkcode.client.voting.integration.kick;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import eu.darkcode.BetterChaosModClient;
import eu.darkcode.client.voting.integration.Integration;
import eu.darkcode.utils.Utils;
import eu.darkcode.voting.PollDefinition;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author _dark_code_
 * @date 28.01.2026
 **/
public class KickIntegration implements Integration {

    private static final @NotNull URI SEND_MESSAGE_URI = URI.create("https://api.kick.com/public/v1/chat");
    private static final @NotNull Gson GSON = new GsonBuilder().create();
    private static final @NotNull HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    private final @NotNull KickIntegrationSettings settings;

    public KickIntegration(@NotNull KickIntegrationSettings settings) {
        this.settings = settings;
        try {

        } catch (Exception e) {
            throw new IllegalStateException("Failed to connect to kick!", e);
        }
    }

    @Override
    public void sendPoll(@NotNull PollDefinition poll) {
        if (!settings.announceNewPolls)
            return;
        try {
            // Format: [1] Entry1 | [2] Entry2 | ... or 5, 6, 7, 8 based on poll id
            StringBuilder message = new StringBuilder("New poll started! ");
            for (int i = 0; i < poll.entries().size(); i++) {
                message.append("[").append(Utils.getVoteOptionIndex(poll.pollId(), i)).append("] ").append(poll.entries().get(i).getString());
                if (i < poll.entries().size() - 1)
                    message.append(" | ");
            }
            sendAsyncMessage(message.toString());
        } catch (Exception e) {
            BetterChaosModClient.LOGGER.warn("Failed to send poll!", e);
        }
    }

    @Override
    public void shutdown() {

    }

    @NotNull
    public CompletableFuture<String> sendAsyncMessage(@NotNull String message) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(SEND_MESSAGE_URI)
                .header("Authorization", "Bearer " + settings.token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(Map.of(
                        "type", "bot",
                        "content", message
                ))))
                .build();
        return HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(response -> {
            if (response.statusCode() != 200)
                return null;
            JsonObject object = GSON.fromJson(response.body(), JsonObject.class);
            return object.getAsJsonObject("data").get("message_id").getAsString();
        });
    }
}
