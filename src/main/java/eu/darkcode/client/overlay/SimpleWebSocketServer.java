package eu.darkcode.client.overlay;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.darkcode.BetterChaosModClient;
import eu.darkcode.client.EventManager;
import eu.darkcode.client.voting.Poll;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.*;

import static eu.darkcode.utils.Utils.GSON;

@Environment(EnvType.CLIENT)
public class SimpleWebSocketServer {

    private final int port;
    private final List<Socket> clients = new CopyOnWriteArrayList<>();
    private volatile boolean running = false;

    private volatile ServerSocket serverSocket;
    private ExecutorService acceptExecutor;
    private ExecutorService clientExecutor;
    private ScheduledExecutorService broadcaster;

    public SimpleWebSocketServer(int port) {
        this.port = port;
    }

    private static String generateAccept(String key) {
        try {
            String concat = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(concat.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Very small encoder: only handles payload length < 126
    private static byte[] encodeTextFrame(String msg) {
        byte[] payload = msg.getBytes(StandardCharsets.UTF_8);
        int len = payload.length;
        if (len <= 125) {
            byte[] frame = new byte[2 + len];
            frame[0] = (byte) 0x81; // FIN + text
            frame[1] = (byte) len;
            System.arraycopy(payload, 0, frame, 2, len);
            return frame;
        } else {
            // fallback for longer messages (simple)
            return ByteBuffer.allocate(4 + len)
                    .put((byte) 0x81)
                    .put((byte) 126)
                    .putShort((short) len)
                    .put(payload)
                    .array();
        }
    }

    public void start() {
        if (running) return;

        running = true;
        acceptExecutor = Executors.newSingleThreadExecutor();
        clientExecutor = Executors.newCachedThreadPool();
        broadcaster = Executors.newSingleThreadScheduledExecutor();

        acceptExecutor.execute(() -> {
            try (ServerSocket server = new ServerSocket(port)) {
                this.serverSocket = server;
                BetterChaosModClient.LOGGER.info("WebSocket server listening on port " + port);
                while (running && !server.isClosed()) {
                    try {
                        Socket client = server.accept();
                        clientExecutor.execute(() -> handleClient(client));
                    } catch (IOException e) {
                        if (running) {
                            BetterChaosModClient.LOGGER.warn("Error accepting websocket client", e);
                        }
                    }
                }
            } catch (IOException e) {
                BetterChaosModClient.LOGGER.error("Failed to start WebSocket server on port {}", port, e);
            } finally {
                running = false;
            }
        });
    }

    private void handleClient(Socket client) {
        try {
            if (doHandshake(client)) {
                clients.add(client);
                BetterChaosModClient.LOGGER.info("Client connected: " + client.getRemoteSocketAddress());
                InputStream in = client.getInputStream();
                byte[] buf = new byte[2];
                while (running && !client.isClosed()) {
                    int r;
                    try {
                        r = in.read(buf);
                    } catch (IOException e) {
                        // will be handled by outer finally
                        break;
                    }
                    if (r == -1)
                        break;
                    // ignore incoming data
                }
            }
        } catch (Exception e) {
            BetterChaosModClient.LOGGER.debug("Client handler exception", e);
        } finally {
            clients.remove(client);
            try {
                client.close();
            } catch (IOException ignored) {
            }
            BetterChaosModClient.LOGGER.info("Client disconnected");
        }
    }

    private boolean doHandshake(Socket client) throws IOException {
        InputStream in = client.getInputStream();
        StringBuilder req = new StringBuilder();
        byte[] buffer = new byte[1024];
        int read = in.read(buffer);
        if (read <= 0) return false;
        req.append(new String(buffer, 0, read, StandardCharsets.UTF_8));
        String request = req.toString();
        String key = null;
        for (String line : request.split("\r\n")) {
            if (line.toLowerCase().startsWith("sec-websocket-key:")) {
                key = line.substring(line.indexOf(':') + 1).trim();
                break;
            }
        }
        if (key == null) return false;
        String accept = generateAccept(key);
        String response = "HTTP/1.1 101 Switching Protocols\r\n" +
                "Upgrade: websocket\r\n" +
                "Connection: Upgrade\r\n" +
                "Sec-WebSocket-Accept: " + accept + "\r\n\r\n";
        OutputStream out = client.getOutputStream();
        out.write(response.getBytes(StandardCharsets.UTF_8));
        out.flush();

        updateStyles();
        EventManager eventManager = BetterChaosModClient.instance().eventManager();
        if (eventManager != null)
            broadcastNewPoll(eventManager.votingSystem().poll());

        return true;
    }

    public void updateStyles() {
        JsonObject obj = new JsonObject();
        obj.addProperty("message", "update_style");
        JsonObject style = new JsonObject();
        style.addProperty("timer_height", BetterChaosModClient.instance().settings().hud.timerHeight());
        style.addProperty("poll_width", BetterChaosModClient.instance().settings().hud.pollWidth());
        style.addProperty("gui_scale", Minecraft.getInstance().getWindow().getGuiScale());

        obj.add("style", style);
        broadcast(obj);
    }

    public void broadcast(@NotNull JsonElement json) {
        broadcaster.execute(() -> {
            byte[] frame = encodeTextFrame(GSON.toJson(json));
            clients.forEach(s -> {
                try {
                    OutputStream out = s.getOutputStream();
                    out.write(frame);
                    out.flush();
                } catch (IOException e) {
                    BetterChaosModClient.LOGGER.debug("Error writing to client, closing socket", e);
                    clients.remove(s);
                    try {
                        s.close();
                    } catch (IOException ignored) {
                    }
                }
            });
        });
    }

    public void stop() {
        running = false;

        // close server socket to unblock accept
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                BetterChaosModClient.LOGGER.debug("Error closing server socket", e);
            }
        }

        // close all client sockets
        for (Socket s : clients) {
            try {
                s.close();
            } catch (IOException ignored) {
            }
        }
        clients.clear();

        // shutdown executors
        if (broadcaster != null) {
            broadcaster.shutdownNow();
            try {
                broadcaster.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException ignored) {
            }
        }
        if (clientExecutor != null) {
            clientExecutor.shutdownNow();
            try {
                clientExecutor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException ignored) {
            }
        }
        if (acceptExecutor != null) {
            acceptExecutor.shutdownNow();
            try {
                acceptExecutor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException ignored) {
            }
        }

        BetterChaosModClient.LOGGER.info("WebSocket server stopped.");
    }

    public void broadcastNewPoll(@Nullable Poll poll) {
        JsonObject obj = new JsonObject();

        if (poll == null) {
            obj.addProperty("message", "clear_poll");
            broadcast(obj);
            return;
        }

        obj.addProperty("message", "new_poll");
        JsonObject pollObject = new JsonObject();
        pollObject.addProperty("pollId", poll.id());
        JsonArray entriesArray = new JsonArray();
        for (Component entry : poll.definition().entries())
            entriesArray.add(entry.getString());
        pollObject.add("entries", entriesArray);
        obj.add("poll", pollObject);
        broadcast(obj);
    }
}