package eu.darkcode.client.voting.integration;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import eu.darkcode.BetterChaosModClient;
import eu.darkcode.client.screen.ConfigScreen;
import eu.darkcode.utils.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

/**
 * @author _dark_code_
 * @date 14.02.2026
 **/
@Environment(EnvType.CLIENT)
public class TokenServer {

    private static final @NotNull String INTERNAL_SERVER_ENDPOINT = "https://bcm.matejtomecek.eu/oauth/";

    private static HttpServer http;

    public static boolean startServer() {
        if (http != null)
            return true;
        try {
            http = HttpServer.create(new java.net.InetSocketAddress(6186), 0);
            http.createContext("/kick", exchange -> handleTokenResponse("kick", exchange, BetterChaosModClient.instance().settings().integration.kickIntegration));
            http.createContext("/twitch", exchange -> handleTokenResponse("twitch", exchange, BetterChaosModClient.instance().settings().integration.twitchIntegration));
            http.setExecutor(null);
            http.start();
        } catch (Exception e) {
            BetterChaosModClient.LOGGER.error("Failed to start kick token server!", e);
        }
        return http != null;
    }

    private static void handleTokenResponse(@NotNull String platform, @NotNull HttpExchange exchange, @NotNull AbstractPlatformSettings settings) throws IOException {
        // POST response with access_token and refresh_token
        if ("POST".equals(exchange.getRequestMethod())) {
            byte[] requestBody = exchange.getRequestBody().readAllBytes();
            String body = new String(requestBody);
            String[] params = body.split("&");
            String accessToken = null;
            String refreshToken = null;
            String expiresAt = null;
            for (String param : params) {
                if (param.startsWith("access_token=")) {
                    accessToken = param.substring(13);
                } else if (param.startsWith("refresh_token=")) {
                    refreshToken = param.substring(14);
                } else if (param.startsWith("expires_at=")) {
                    expiresAt = URLDecoder.decode(param.substring(11), StandardCharsets.UTF_8);
                }
            }
            if (accessToken != null && refreshToken != null && expiresAt != null) {
                settings.token = accessToken;
                settings.refreshToken = refreshToken;
                try {
                    settings.tokenExpiry = Instant.parse(expiresAt).toEpochMilli();
                } catch (Exception e) {
                    BetterChaosModClient.LOGGER.error("Failed to parse Platform token expiry time!", e);
                }

                BetterChaosModClient.instance().saveSettings();

                exchange.getResponseHeaders().set("Location", INTERNAL_SERVER_ENDPOINT + platform + "/success");
                exchange.sendResponseHeaders(303, -1);
                exchange.getResponseBody().close();

                Minecraft.getInstance().execute(() -> {
                    if (Minecraft.getInstance().gui.screen() instanceof ConfigScreen cs)
                        cs.reinit();
                });
                return;
            } else {
                String response = "Invalid token data!";
                exchange.sendResponseHeaders(400, response.length());
                exchange.getResponseBody().write(response.getBytes());
            }
        } else {
            String response = "Method not allowed!";
            exchange.sendResponseHeaders(405, response.length());
            exchange.getResponseBody().write(response.getBytes());
        }
        exchange.getResponseBody().close();
    }

    private static void openPlatform(@NotNull String platform) {
        if (!startServer()) {
            // TODO: Open error screen
            return;
        }
        Util.getPlatform().openUri(INTERNAL_SERVER_ENDPOINT + platform + "/authorize");
    }

    public static void openKick() {
        openPlatform("kick");
    }

    public static void openTwitch() {
        openPlatform("twitch");
    }

    @NotNull
    public static CompletableFuture<Boolean> refreshKickToken() {
        return refreshToken("kick", BetterChaosModClient.instance().settings().integration.kickIntegration);
    }

    @NotNull
    public static CompletableFuture<Boolean> refreshTwitchToken() {
        return refreshToken("twitch", BetterChaosModClient.instance().settings().integration.twitchIntegration);
    }

    @NotNull
    private static CompletableFuture<Boolean> refreshToken(@NotNull String platform, @NotNull AbstractPlatformSettings settings) {
        if (settings.refreshToken == null || settings.refreshToken.isEmpty())
            return CompletableFuture.completedFuture(null);
        return CompletableFuture.supplyAsync(() -> {
            String url = INTERNAL_SERVER_ENDPOINT + platform + "/refresh";
            try {
                HttpURLConnection connection = (HttpURLConnection) new URI(url).toURL().openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                String postData = "refresh_token=" + URLEncoder.encode(settings.refreshToken, StandardCharsets.UTF_8);
                byte[] postDataBytes = postData.getBytes(StandardCharsets.UTF_8);
                connection.getOutputStream().write(postDataBytes);
                connection.getOutputStream().flush();
                if (connection.getResponseCode() == 200) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder responseBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            responseBuilder.append(line);
                        }
                        String response = responseBuilder.toString();
                        // Parse JSON response
                        JsonObject json = Utils.GSON.fromJson(response, JsonObject.class);
                        String accessToken = json.get("access_token").getAsString();
                        String refreshToken = json.get("refresh_token").getAsString();
                        long expiresIn = json.get("expires_in").getAsLong();

                        settings.token = accessToken;
                        settings.refreshToken = refreshToken;
                        settings.tokenExpiry = Instant.now().toEpochMilli() + (expiresIn * 1000);

                        BetterChaosModClient.instance().saveSettings();
                        return true;
                    }
                } else {
                    BetterChaosModClient.LOGGER.error("Failed to refresh Platform token, response code: {}", connection.getResponseCode());
                }
            } catch (Throwable e) {
                BetterChaosModClient.LOGGER.error("Failed to refresh Platform token!", e);
            }
            return false;
        });
    }

}
