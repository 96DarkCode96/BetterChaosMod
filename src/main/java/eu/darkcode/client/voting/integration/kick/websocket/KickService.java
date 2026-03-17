package eu.darkcode.client.voting.integration.kick.websocket;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import eu.darkcode.BetterChaosModClient;
import eu.darkcode.client.voting.integration.kick.KickIntegration;
import eu.darkcode.client.voting.integration.kick.KickIntegrationSettings;
import eu.darkcode.client.voting.integration.twitch.irc.IRCUser;
import eu.darkcode.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * @author _dark_code_
 * @date 11.02.2026
 **/
public class KickService implements WebSocket.Listener {
    private final @NotNull KickIntegration integration;
    private final @NotNull String endpoint;
    private final @NotNull HttpClient client;
    private final @NotNull KickIntegrationSettings settings;
    private WebSocket websocket;
    private volatile boolean shuttingDown = false;
    private volatile boolean reconnecting = false;

    public KickService(@NotNull KickIntegrationSettings settings, @NotNull KickIntegration integration) throws Exception {
        this.settings = settings;
        this.integration = integration;
        this.endpoint = "wss://ws-" + settings.kickPusherCluster + ".pusher.com/app/" + settings.kickPusherKey + "?protocol=7&client=js&version=8.4.0&flash=false";
        this.client = HttpClient.newHttpClient();

        if (settings.token == null || settings.token.isBlank())
            throw new IllegalArgumentException("token is null or blank");

        if (settings.chatroomId == -1) {
            String userName = KickIntegration.getUserName(settings.token).join();
            if (userName == null || userName.isEmpty())
                throw new IllegalArgumentException("Username is empty");

            BetterChaosModClient.LOGGER.info("KickUserName: {}", userName);
            settings.chatroomId = fetchChannelRoomId(userName);
            BetterChaosModClient.LOGGER.info("KickChannelRoomId: {}", settings.chatroomId);
            if (settings.chatroomId == -1)
                throw new IllegalArgumentException("ChatroomId is null");
            BetterChaosModClient.instance().saveSettings();
        }

        connect().join();
        if (this.websocket == null)
            throw new IOException("Failed to connect to KickService!");
    }

    private CompletableFuture<Void> connect() {
        return client.newWebSocketBuilder()
                .buildAsync(URI.create(endpoint), this)
                .thenAccept(ws -> {
                    this.websocket = ws;
                    BetterChaosModClient.LOGGER.info("Connected to KickService!");
                    subscribe();
                })
                .exceptionally(ex -> {
                    scheduleReconnect();
                    return null;
                });
    }

    private void subscribe() {
        try {
            JsonObject subscribe = new JsonObject();
            subscribe.addProperty("auth", "");
            subscribe.addProperty("channel", "chatrooms." + settings.chatroomId + ".v2");
            sendMessage("pusher:subscribe", subscribe);
        } catch (Throwable t) {
            scheduleReconnect();
        }
    }

    private void scheduleReconnect() {
        if (shuttingDown || reconnecting)
            return;
        reconnecting = true;

        new Thread(() -> {
            try {
                Thread.sleep(3000); // simple backoff (3s)
                if (!shuttingDown) {
                    BetterChaosModClient.LOGGER.warn("KickService disconnected, reconnecting...");
                    connect();
                }
            } catch (InterruptedException ignored) {
            } finally {
                reconnecting = false;
            }
        }, "KickService-Reconnect").start();
    }


    private void sendMessage(@NotNull String event, @NotNull JsonObject data) {
        JsonObject msg = new JsonObject();
        msg.addProperty("event", event);
        msg.add("data", data);
        String json = Utils.GSON.toJson(msg);
        websocket.sendText(json, true);
    }

    public static int fetchChannelRoomId(@NotNull String userName) throws IllegalStateException {
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpResponse<String> response = httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create("https://kick.com/api/v2/channels/" + userName + "/chatroom"))
                    .header("Accept", "application/json")
                    .GET()
                    .build(), HttpResponse.BodyHandlers.ofString());
            JsonObject jsonObject = Utils.GSON.fromJson(response.body(), JsonObject.class);
            return jsonObject.get("id").getAsInt();
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        try {
            JsonObject packet = Utils.GSON.fromJson(data.toString(), JsonObject.class);
            String event = packet.get("event").getAsString();
            if (event.equals("App\\Events\\ChatMessageEvent"))
                onChatMessage(Utils.GSON.fromJson(packet.get("data").getAsString(), JsonObject.class));
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    private void onChatMessage(@NotNull JsonObject data) {
        try {
            if (!data.has("content") || !data.has("sender"))
                return;
            String content = data.get("content").getAsString().trim();
            JsonObject sender = data.getAsJsonObject("sender");
            JsonObject identity = sender.getAsJsonObject("identity");
            JsonArray badges = identity.getAsJsonArray("badges");
            boolean vip = false;
            boolean mod = false;
            boolean sub = false;
            for (JsonElement badge : badges) {
                String type = badge.getAsJsonObject().get("type").getAsString().toLowerCase();
                switch (type) {
                    case "subscriber" -> sub = true;
                    case "vip" -> vip = true;
                    case "moderator" -> mod = true;
                }
            }

            IRCUser user = new IRCUser(
                    sender.get("id").getAsLong(),
                    null,
                    sender.has("username") ? sender.get("username").getAsString() : sender.get("slug").getAsString(),
                    Integer.parseInt(identity.get("color").getAsString().substring(1), 16),
                    vip,
                    mod,
                    sub
            );

            integration.handleMessage(user, content);
        } catch (Throwable ignored) {
            // ignored
        }
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        if (!shuttingDown) {
            scheduleReconnect();
        }
        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        if (!shuttingDown)
            scheduleReconnect();
    }

    public void shutdown() {
        shuttingDown = true;
        if (websocket != null)
            websocket.abort();
        client.close();
    }
}
