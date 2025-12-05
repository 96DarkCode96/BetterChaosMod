package eu.darkcode.client.voting.integration.twitch;

import org.jetbrains.annotations.NotNull;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class IRCClient {

    public final @NotNull TwitchIntegrationSettings settings;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final AtomicBoolean running = new AtomicBoolean(true);
    private SSLSocket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private long lastPing = System.currentTimeMillis();

    public IRCClient(@NotNull TwitchIntegrationSettings settings) {
        this.settings = settings;
        if (settings.channel == null || settings.channel.isBlank() || settings.token == null || settings.token.isBlank())
            throw new NullPointerException("channel or token is null or blank");
    }

    @NotNull
    public static CompletableFuture<Boolean> testConnection(@NotNull TwitchIntegrationSettings settings) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("Testing Twitch IRC connection...");
                SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                try (SSLSocket socket = (SSLSocket) factory.createSocket("irc.chat.twitch.tv", 6697)) {
                    socket.startHandshake();

                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

                    System.out.println("Connected to Twitch IRC for testing. Authenticating...");

                    writer.write("PASS oauth:" + settings.token + "\r\n");
                    writer.write("NICK " + settings.channel.toLowerCase() + "\r\n");
                    writer.write("JOIN #" + settings.channel + "\r\n");
                    writer.write("CAP REQ :twitch.tv/tags twitch.tv/commands twitch.tv/membership\r\n");
                    writer.flush();

                    String line;
                    while (socket.isConnected() && (line = reader.readLine()) != null)
                        if (line.contains("Welcome, GLHF!"))
                            return true;
                }

                System.out.println("Connection test failed: Did not receive welcome message.");

                return false;
            } catch (Throwable e) {
                System.out.println("Connection test failed: " + e.getMessage());
                return false;
            }
        });
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
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
                System.out.println("Connection lost: " + e.getMessage());
                connected.set(false);
            }

            // reconnect with backoff
            retry++;
            int delay = Math.min(30, retry * 2); // max 30 seconds
            System.out.println("Reconnecting in " + delay + "s...");
            sleep(delay * 1000);
        }
    }

    private void connectOnce() throws Exception {
        System.out.println("Connecting to Twitch IRC (SSL)...");

        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        socket = (SSLSocket) factory.createSocket("irc.chat.twitch.tv", 6697);
        socket.startHandshake();

        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

        connected.set(true);

        System.out.println("Connected!");

        sendRaw("PASS oauth:" + this.settings.token);
        sendRaw("NICK " + this.settings.channel.toLowerCase());
        sendRaw("JOIN #" + this.settings.channel);
        sendRaw("CAP REQ :twitch.tv/tags twitch.tv/commands twitch.tv/membership");

        lastPing = System.currentTimeMillis();
    }

    private void readLoop() throws Exception {
        String line;

        while ((line = reader.readLine()) != null) {
            System.out.println("<< " + line);

            if (line.startsWith("PING")) {
                lastPing = System.currentTimeMillis();
                sendRaw("PONG :" + line.substring(6));
                continue;
            }
        }

        throw new IOException("Stream disconnected");
    }

    private void watchdogLoop() {
        while (running.get()) {
            sleep(5000);

            // If no ping in 2 minutes -> assume dead
            if (connected.get() && (System.currentTimeMillis() - lastPing) > 120_000) {
                System.out.println("Ping timeout — forcing reconnect...");
                close();
            }
        }
    }

    public synchronized void sendRaw(String message) throws IOException {
        if (!connected.get()) return;
        writer.write(message + "\r\n");
        writer.flush();
        System.out.println(">> " + message);
    }

    public void sendMessage(String message) throws IOException {
        sendRaw("PRIVMSG " + this.settings.channel + " :" + message);
    }

    public void close() {
        try {
            running.set(false);
            socket.close();
        } catch (Exception ignored) {
        }
        connected.set(false);
    }
}
