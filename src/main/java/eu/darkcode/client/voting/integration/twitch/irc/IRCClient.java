package eu.darkcode.client.voting.integration.twitch.irc;

import eu.darkcode.client.voting.integration.twitch.TwitchIntegrationSettings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class IRCClient {

    private static final @NotNull Logger LOGGER = LoggerFactory.getLogger("IRCClient");

    public final @NotNull TwitchIntegrationSettings settings;
    private final @NotNull AtomicBoolean running = new AtomicBoolean(true);
    private final @NotNull AtomicBoolean connected = new AtomicBoolean(false);
    private final @NotNull AtomicBoolean successful = new AtomicBoolean(false);
    private final @NotNull List<String> messageQueue = new ArrayList<>();
    private final @NotNull IRCEvent eventHandler;
    private SSLSocket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private long lastPing = System.currentTimeMillis();

    public IRCClient(@NotNull TwitchIntegrationSettings settings, @NotNull IRCEvent eventHandler) {
        this.settings = settings;
        this.eventHandler = eventHandler;
        if (settings.channel == null || settings.channel.isBlank() || settings.token == null || settings.token.isBlank())
            throw new NullPointerException("channel or token is null or blank");
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            LOGGER.warn("Sleep interrupted", e);
        }
    }

    @NotNull
    private static Map<String, String> parseTags(@NotNull String tagsPart) {
        if (tagsPart.startsWith("@"))
            tagsPart = tagsPart.substring(1);

        String[] tagPairs = tagsPart.split(";");
        return Arrays.stream(tagPairs)
                .map(tag -> tag.split("=", 2))
                .collect(Collectors.toMap(
                        pair -> pair[0],
                        pair -> pair.length > 1 ? pair[1] : ""
                ));
    }

    @NotNull
    private static IRCUser makeUser(@NotNull String sourcePart, @NotNull Map<String, String> tags) {
        return new IRCUser(Long.parseLong(tags.get("user-id")), tags.get("display-name"),
                sourcePart.substring(1, sourcePart.indexOf('!')),
                Integer.parseInt(tags.getOrDefault("color", "#FFFFFF").substring(1), 16),
                tags.containsKey("vip"),
                tags.getOrDefault("mod", "0").equals("1"),
                tags.getOrDefault("subscriber", "0").equals("1"));
    }

    public void start() {
        new Thread(this::connectionLoop, "IRC-ConnectionLoop").start();
        new Thread(this::watchdogLoop, "IRC-Watchdog").start();
    }

    private void connectionLoop() {
        int retry = 0;

        while (running.get()) {
            try {
                connectOnce();
                retry = 0; // reset retry count on successful connect
                readLoop();
            } catch (Exception e) {
                LOGGER.warn("Connection lost", e);
                successful.set(false);
                connected.set(false);
            }

            // reconnect with backoff
            retry++;
            int delay = Math.min(30, retry * 2); // max 30 seconds
            LOGGER.info("Reconnecting in {} seconds...", delay);
            sleep(delay * 1000L);
        }
    }

    private void connectOnce() throws Exception {
        LOGGER.info("Attempting to connect to IRC");

        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        socket = (SSLSocket) factory.createSocket("irc.chat.twitch.tv", 6697);
        socket.startHandshake();

        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

        connected.set(true);

        LOGGER.info("Successfully connected to IRC");

        sendRaw("PASS oauth:" + this.settings.token);
        sendRaw("NICK " + this.settings.channel.toLowerCase());
        sendRaw("JOIN #" + this.settings.channel);
        sendRaw("CAP REQ :twitch.tv/tags twitch.tv/commands twitch.tv/membership");

        lastPing = System.currentTimeMillis();
    }

    private void readLoop() throws Exception {
        String line;
        while ((line = reader.readLine()) != null) {
            lastPing = System.currentTimeMillis();

            if (line.startsWith("@")) {
                handleTwitchMessage(line);
            } else if (line.startsWith("PING")) {
                sendRaw("PONG :" + line.substring(6));
            } else if (line.contains(" :Welcome, GLHF!") && !successful.get()) {
                successful.set(true);
                sendQueue();
            }
        }
        throw new IOException("Stream disconnected");
    }

    private void handleTwitchMessage(@NotNull String line) {
        try {
            // Pattern: @<tags> :<source> <command> <params> :<message>
            String[] parts = line.split(" ", 5);
            if (parts.length < 5)
                return;

            if (parts[2].equals("PRIVMSG")) {
                Map<String, String> tags = parseTags(parts[0]);
                if (settings.ignoreSharedChat && tags.containsKey("room-id") && tags.containsKey("source-room-id") && !tags.get("room-id").equals(tags.get("source-room-id")))
                    return; // shared chat message, ignore
                eventHandler.onMessage(makeUser(parts[1], tags), parts[3].substring(1), parts[4].substring(1));
            }

        } catch (Throwable e) {
            LOGGER.error("Failed to handle Twitch message: {}", line, e);
        }
    }

    private void watchdogLoop() {
        while (running.get()) {
            sleep(5000);

            if (connected.get() && (System.currentTimeMillis() - lastPing) > 315_000) {
                LOGGER.warn("Watchdog loop has been interrupted");
                close();
            }
        }
    }

    public synchronized void sendRaw(@NotNull String message) throws IOException {
        if (!connected.get())
            return;
        writer.write(message + "\r\n");
        writer.flush();
    }

    public void sendMessage(@NotNull String message) throws IOException {
        final String msg = "PRIVMSG #" + this.settings.channel + " :/me " + message;
        if (!successful.get() || !connected.get()) {
            messageQueue.add(msg);
            return;
        }
        sendQueue();
        sendRaw(msg);
    }

    private void sendQueue() throws IOException {
        for (String s : messageQueue)
            sendRaw(s);
        messageQueue.clear();
    }

    public void close() {
        running.set(false);
        try {
            socket.close();
        } catch (Exception e) {
            LOGGER.warn("Failed to close IRC connection", e);
        }
        connected.set(false);
        successful.set(false);
    }
}
