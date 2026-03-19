package eu.darkcode.client.overlay;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import eu.darkcode.BetterChaosModClient;
import eu.darkcode.client.voting.Poll;
import eu.darkcode.client.voting.integration.twitch.irc.IRCUser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

@Environment(EnvType.CLIENT)
public final class OverlayWebServer {

    public static final @NotNull String HTML_CONTENT_TYPE = "text/html; charset=utf-8";
    public static final @NotNull String JS_CONTENT_TYPE = "application/javascript; charset=utf-8";
    public static final @NotNull String CSS_CONTENT_TYPE = "text/css; charset=utf-8";

    private static HttpServer http;
    private static SimpleWebSocketServer wsServer;

    private static @Nullable Exception startupException;

    public static void startServer(int webPort) {
        if (http != null)
            return; // Server already running

        startupException = null;
        try {
            http = HttpServer.create(new InetSocketAddress("localhost", webPort), 0);
            http.createContext("/", new ResourceHandler());
            http.setExecutor(Executors.newFixedThreadPool(4));
            http.start();
            BetterChaosModClient.LOGGER.info("HTTP server running at http://localhost:" + webPort + "/");

            // start websocket server and keep reference so we can stop it later
            wsServer = new SimpleWebSocketServer(6180);
            wsServer.start();
            BetterChaosModClient.LOGGER.info("WebSocket server running at ws://localhost:6180/");
        } catch (Exception e) {
            startupException = e;
            http = null;
            if (wsServer != null) {
                try {
                    wsServer.stop();
                } catch (Exception ignored) {
                }
                wsServer = null;
            }
            BetterChaosModClient.LOGGER.error("Failed to start web servers", e);
        }
    }

    public static void stopServer() {
        if (http == null && wsServer == null)
            return;
        startupException = null;

        if (http != null) {
            try {
                http.stop(0);
                BetterChaosModClient.LOGGER.info("HTTP server stopped.");
            } catch (Exception e) {
                BetterChaosModClient.LOGGER.warn("Error stopping HTTP server", e);
            } finally {
                http = null;
            }
        }

        if (wsServer != null) {
            try {
                wsServer.stop();
            } catch (Exception e) {
                BetterChaosModClient.LOGGER.warn("Error stopping WebSocket server", e);
            } finally {
                wsServer = null;
            }
        }
    }

    public static @Nullable Exception getStartupException() {
        return startupException;
    }

    public static boolean running() {
        return http != null;
    }

    public static void sendPollVotes(int totalVotes, int @NotNull [] votes) {
        if (wsServer != null)
            wsServer.broadcastPollVotes(totalVotes, votes);
    }

    public static void sendPollUpdate(@Nullable Poll poll) {
        if (wsServer != null)
            wsServer.broadcastNewPoll(poll);
    }

    public static void sendStyleUpdate() {
        if (wsServer != null)
            wsServer.updateStyles();
    }

    public static void clearChatHistory() {
        if (wsServer != null)
            wsServer.clearChatHistory();
    }

    public static void broadcastNewMessage(@NotNull IRCUser user, @NotNull String message) {
        if (wsServer != null)
            wsServer.broadcastNewMessage(user, message);
    }


    static class ResourceHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            switch (exchange.getRequestURI().getPath()) {
                case "/poll" -> serveContent("overlay/poll/index.html", exchange, HTML_CONTENT_TYPE);
                case "/poll/app.js" -> serveContent("overlay/poll/app.js", exchange, JS_CONTENT_TYPE);

                case "/chat" -> serveContent("overlay/chat/index.html", exchange, HTML_CONTENT_TYPE);
                case "/chat/app.js" -> serveContent("overlay/chat/app.js", exchange, JS_CONTENT_TYPE);

                case "/app.js" -> serveContent("overlay/app.js", exchange, JS_CONTENT_TYPE);
                case "/app.css" -> serveContent("overlay/app.css", exchange, CSS_CONTENT_TYPE);

                default -> serveContent("overlay/index.html", exchange, HTML_CONTENT_TYPE);
            }
        }

        private void serverNotFound(@NotNull HttpExchange exchange) throws IOException {
            byte[] notFound = "404 Not Found".getBytes();
            exchange.sendResponseHeaders(404, notFound.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(notFound);
            }
        }

        private void serveContent(@NotNull String name, @NotNull HttpExchange exchange, @NotNull String contentType) throws IOException {
            try (InputStream is = getClass().getClassLoader().getResourceAsStream(name)) {
                if (is == null) {
                    serverNotFound(exchange);
                    return;
                }
                Headers h = exchange.getResponseHeaders();
                h.add("Content-Type", contentType);
                byte[] data = is.readAllBytes();
                exchange.sendResponseHeaders(200, data.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(data);
                }
            }
        }
    }
}