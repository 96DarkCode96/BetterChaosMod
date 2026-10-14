package eu.darkcode.client.voting.integration.kick.centrifugo;

import com.google.gson.JsonObject;
import eu.darkcode.BetterChaosModClient;
import eu.darkcode.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author _dark_code_
 * @date 14.10.2026
 **/
public abstract class CentrifugoWebSocket implements WebSocket.Listener {

    private static final HttpClient SHARED_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final AtomicBoolean shuttingDown = new AtomicBoolean(false);
    private final AtomicBoolean reconnecting = new AtomicBoolean(false);
    private WebSocket websocket;
    private int messageId = 2;

    protected CentrifugoWebSocket() throws IOException {
        try {
            connect().join();
        } catch (Exception e) {
            throw new IOException("Failed to connect to Centrifugo WebSocket!", e);
        }

        if (this.websocket == null) {
            throw new IOException("Failed to connect to Centrifugo WebSocket: WebSocket instance is null.");
        }
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        onDisconnected();
        if (!shuttingDown.get()) {
            scheduleReconnect();
        }
        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        BetterChaosModClient.LOGGER.error("Centrifugo WebSocket encountered an error", error);
        onDisconnected();
        if (!shuttingDown.get()) {
            scheduleReconnect();
        }
    }

    @Override
    public final CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        if (data == null || data.isEmpty()) {
            return WebSocket.Listener.super.onText(webSocket, data, last);
        }

        String[] messages = data.toString().split("\n");
        for (String msg : messages) {
            if (msg.isBlank()) continue;

            try {
                JsonObject packet = Utils.GSON.fromJson(msg, JsonObject.class);
                if (packet == null || packet.isEmpty()) {
                    // Ping message response
                    sendData("{}", true);
                } else {
                    handlePacket(packet);
                }
            } catch (Exception e) {
                BetterChaosModClient.LOGGER.error("Failed to parse Centrifugo packet: {}", msg, e);
            }
        }
        return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    @NotNull
    private CompletableFuture<Void> connect() {
        return SHARED_CLIENT.newWebSocketBuilder()
                .buildAsync(URI.create(getWebSocketUrl()), this)
                .thenAccept(ws -> {
                    this.websocket = ws;
                    this.messageId = 2;
                    reconnecting.set(false);
                    onConnect();
                })
                .exceptionally(ex -> {
                    BetterChaosModClient.LOGGER.error("Failed to establish WebSocket connection", ex);
                    if (!shuttingDown.get()) {
                        scheduleReconnect();
                    }
                    return null;
                });
    }

    protected final int requestMessageId() {
        return this.messageId++;
    }

    public void sendData(@NotNull String data, boolean last) {
        if (websocket == null || shuttingDown.get()) {
            return;
        }
        try {
            websocket.sendText(data, last);
        } catch (Exception t) {
            BetterChaosModClient.LOGGER.error("Failed to send text over Centrifugo WebSocket", t);
            scheduleReconnect();
        }
    }

    private void scheduleReconnect() {
        if (shuttingDown.get() || !reconnecting.compareAndSet(false, true)) {
            return;
        }

        Thread reconnectThread = new Thread(() -> {
            try {
                Thread.sleep(3000); // Simple backoff (3s)
                if (!shuttingDown.get()) {
                    BetterChaosModClient.LOGGER.warn("Websocket disconnected, attempting to reconnect...");
                    connect().join();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                BetterChaosModClient.LOGGER.error("Reconnection attempt failed", e);
                reconnecting.set(false);
                if (!shuttingDown.get()) {
                    scheduleReconnect(); // Retry again if failed
                }
            }
        }, "Centrifugo-Reconnect-" + ThreadLocalRandom.current().nextInt(1000, 10000));

        reconnectThread.setDaemon(true);
        reconnectThread.start();
    }

    public void shutdown() {
        if (!shuttingDown.compareAndSet(false, true)) {
            return;
        }

        reconnecting.set(false);
        if (websocket != null) {
            try {
                websocket.sendClose(WebSocket.NORMAL_CLOSURE, "Shutting down").join();
            } catch (Exception e) {
                websocket.abort();
            }
        }
    }

    @NotNull
    protected abstract String getWebSocketUrl();

    protected abstract void onConnect();

    protected abstract void onDisconnected();

    protected abstract void handlePacket(@NotNull JsonObject packet);
}