package eu.darkcode.client.voting.integration.kick;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import eu.darkcode.BetterChaosModClient;
import eu.darkcode.client.voting.integration.Integration;
import eu.darkcode.client.voting.integration.kick.websocket.KickService;
import eu.darkcode.voting.PollDefinition;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
@Environment(EnvType.CLIENT)
public class KickIntegration implements Integration {

    private static final @NotNull URI SEND_MESSAGE_URI = URI.create("https://api.kick.com/public/v1/chat");
    private static final @NotNull Gson GSON = new GsonBuilder().create();
    private static final @NotNull HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    private final @NotNull KickIntegrationSettings settings;
    private final @NotNull KickService kickService;

    public KickIntegration(@NotNull KickIntegrationSettings settings) {
        this.settings = settings;
        try {
            String msgId = sendAsyncMessage("Loading world...").join();
            if (msgId == null && !Boolean.TRUE.equals(KickTokenServer.refreshToken().join()))
                throw new IllegalStateException("Failed to connect to kick! Invalid token!");

            kickService = new KickService(settings, this);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to connect to kick!", e);
        }
    }

    @Override
    public void sendPoll(@NotNull PollDefinition poll) {
        if (!settings.announceNewPolls)
            return;
        try {
            sendAsyncMessage(poll.toMessage());
        } catch (Exception e) {
            BetterChaosModClient.LOGGER.warn("Failed to send poll!", e);
        }
    }

    @Override
    public void shutdown() {
        kickService.shutdown();
    }

    @NotNull
    public static CompletableFuture<String> getUserName(@NotNull String token) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.kick.com/public/v1/users"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        return HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(response -> {
            if (response.statusCode() != 200) {
                BetterChaosModClient.LOGGER.error("Failed to fetch Kick user data! sCode: {}", response.statusCode());
                return null;
            }
            BetterChaosModClient.LOGGER.info("Kick user data has been fetched!: {}", response.body());
            JsonObject object = GSON.fromJson(response.body(), JsonObject.class);
            JsonObject data = object.getAsJsonArray("data").get(0).getAsJsonObject();
            return data.get("name").getAsString();
        });
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
