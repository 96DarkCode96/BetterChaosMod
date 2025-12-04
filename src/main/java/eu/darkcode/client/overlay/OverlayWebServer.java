package eu.darkcode.client.overlay;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import eu.darkcode.BetterChaosModClient;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public final class OverlayWebServer {

    private static HttpServer http;
    private static @Nullable Exception startupException;

    public static void startServer(int webPort) throws Exception {
        if (http != null)
            return; // Server already running

        http = HttpServer.create(new InetSocketAddress("localhost", webPort), 0);
        http.createContext("/", new ResourceHandler());
        http.setExecutor(Executors.newFixedThreadPool(4));
        http.start();
        BetterChaosModClient.LOGGER.info("HTTP server running at http://localhost:" + webPort + "/");

        SimpleWebSocketServer ws = new SimpleWebSocketServer(6180);
        ws.start();
        BetterChaosModClient.LOGGER.info("WebSocket server running at ws://localhost:6180/");
    }

    public static @Nullable Exception getStartupException() {
        return startupException;
    }

    public static void startFailed(@Nullable Exception e) {
        OverlayWebServer.startupException = e;
    }

    static class ResourceHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("overlay/index.html")) {
                if (is == null) {
                    byte[] notFound = "404 Not Found".getBytes();
                    exchange.sendResponseHeaders(404, notFound.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(notFound);
                    }
                    return;
                }

                Headers h = exchange.getResponseHeaders();
                h.add("Content-Type", "text/html; charset=utf-8");
                byte[] data = is.readAllBytes();
                exchange.sendResponseHeaders(200, data.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(data);
                }
            }
        }
    }
}