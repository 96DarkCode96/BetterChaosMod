package eu.darkcode.client.voting.integration.kick;

import com.google.gson.JsonObject;
import eu.darkcode.BetterChaosModClient;
import eu.darkcode.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author _dark_code_
 * @date 14.10.2026
 **/
public final class KickHelper {

    private static final @NotNull URI SEND_MESSAGE_URI = URI.create("https://api.kick.com/public/v1/chat");
    private static final @NotNull URI FETCH_USER_URI = URI.create("https://api.kick.com/public/v1/users");
    private static final @NotNull URI FETCH_WEBSOCKET_URI = URI.create("https://web.kick.com/api/v1/realtime/connection");
    private static final @NotNull URI JWT_TOKEN_URI = URI.create("https://web.kick.com/api/v1/realtime/auth/connection");

    // Shared HttpClient instance for performance and connection pooling
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private KickHelper() {
    }

    public static int fetchChatroomId(@NotNull String token) {
        String userName = fetchUserName(token);
        if (userName == null || userName.isEmpty()) {
            throw new IllegalArgumentException("Username is empty or null");
        }

        BetterChaosModClient.LOGGER.debug("KickUserName: {}", userName);
        Integer chatroomId = fetchChannelRoomId(userName);
        BetterChaosModClient.LOGGER.debug("KickChannelRoomId: {}", chatroomId);

        if (chatroomId == null || chatroomId == -1) {
            throw new IllegalArgumentException("ChatroomId is null or invalid");
        }
        return chatroomId;
    }

    @Nullable
    @VisibleForTesting
    public static Integer fetchChannelRoomId(@NotNull String userName) throws IllegalStateException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://kick.com/api/v2/channels/" + userName + "/chatroom"))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                BetterChaosModClient.LOGGER.error("Failed to fetch Kick channel room id! StatusCode: {}", response.statusCode());
                return null;
            }

            JsonObject jsonObject = Utils.GSON.fromJson(response.body(), JsonObject.class);
            return jsonObject.has("id") ? jsonObject.get("id").getAsInt() : null;
        } catch (Throwable e) {
            throw new IllegalStateException("Failed to fetch channel room ID for user: " + userName, e);
        }
    }

    @Nullable
    public static String fetchUserName(@NotNull String token) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(FETCH_USER_URI)
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                BetterChaosModClient.LOGGER.error("Failed to fetch Kick user data! StatusCode: {}", response.statusCode());
                return null;
            }

            String responseBody = response.body();
            BetterChaosModClient.LOGGER.debug("Kick user data has been fetched!: {}", responseBody);

            JsonObject object = Utils.GSON.fromJson(responseBody, JsonObject.class);
            return object.getAsJsonArray("data").get(0).getAsJsonObject().get("name").getAsString();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to fetch user's name!", e);
        }
    }

    @NotNull
    public static CompletableFuture<String> sendAsyncMessage(@NotNull String token, @NotNull String message) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(SEND_MESSAGE_URI)
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(Utils.GSON.toJson(Map.of(
                        "type", "bot",
                        "content", message
                ))))
                .build();

        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() != 200) {
                        BetterChaosModClient.LOGGER.error("Failed to send async message! StatusCode: {}", response.statusCode());
                        return null;
                    }
                    JsonObject object = Utils.GSON.fromJson(response.body(), JsonObject.class);
                    return object.getAsJsonObject("data").get("message_id").getAsString();
                });
    }

    @Nullable
    public static String fetchWebSocketURL(@NotNull String clientId) {
        try {
            String requestBody = Utils.GSON.toJson(Map.of(
                    "client", Map.of(
                            "id", clientId,
                            "type", "web"
                    ),
                    "capabilities", Map.of(
                            "accepted_providers", List.of(
                                    Map.of("provider", "centrifugo")
                            )
                    )
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(FETCH_WEBSOCKET_URI)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("X-App-Platform", "web")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                BetterChaosModClient.LOGGER.error("Failed to fetch WebSocket URL! StatusCode: {}; Body: {}", response.statusCode(), response.body());
                return null;
            }

            JsonObject jsonObject = Utils.GSON.fromJson(response.body(), JsonObject.class);
            return jsonObject.getAsJsonObject("data")
                    .getAsJsonArray("connections")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("credentials")
                    .get("url").getAsString();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to fetch WebSocket URL", e);
        }
    }

    @Nullable
    public static String fetchJWTToken(@NotNull String clientId) {
        try {
            String requestBody = Utils.GSON.toJson(Map.of("client_id", clientId));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(JWT_TOKEN_URI)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("X-App-Platform", "web")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                BetterChaosModClient.LOGGER.error("Failed to fetch JWT token! StatusCode: {}; Body: {}; Client: {}", response.statusCode(), response.body(), clientId);
                return null;
            }

            JsonObject jsonObject = Utils.GSON.fromJson(response.body(), JsonObject.class);
            return jsonObject.getAsJsonObject("data").get("token").getAsString();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to fetch JWT token", e);
        }
    }

    @NotNull
    public static String getClientId() {
        return UUID.randomUUID().toString();
    }
}