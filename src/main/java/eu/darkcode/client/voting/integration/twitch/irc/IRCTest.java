package eu.darkcode.client.voting.integration.twitch.irc;

import eu.darkcode.BetterChaosModClient;
import eu.darkcode.client.voting.integration.twitch.TwitchIntegrationSettings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public final class IRCTest {
    private IRCTest() {
    }

    @NotNull
    public static CompletableFuture<TestResult> testConnection(@NotNull TwitchIntegrationSettings settings) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                try (SSLSocket socket = (SSLSocket) factory.createSocket("irc.chat.twitch.tv", 6697)) {
                    socket.startHandshake();

                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

                    writer.write("PASS oauth:" + settings.token + "\r\n");
                    writer.write("NICK " + settings.channel.toLowerCase() + "\r\n");
                    writer.write("JOIN #" + settings.channel + "\r\n");
                    writer.write("CAP REQ :twitch.tv/tags twitch.tv/commands twitch.tv/membership\r\n");
                    writer.flush();

                    String line;
                    while (socket.isConnected() && (line = reader.readLine()) != null) {
                        if (line.contains("Welcome, GLHF!"))
                            return TestResult.SUCCESS;
                        if (line.contains("Login authentication failed"))
                            return TestResult.AUTHENTICATION_FAILED;
                    }
                }
            } catch (Throwable e) {
                BetterChaosModClient.LOGGER.error("Failed to test Twitch IRC connection", e);
            }
            return TestResult.CONNECTION_ERROR;
        });
    }

    public enum TestResult {
        SUCCESS,
        AUTHENTICATION_FAILED,
        CONNECTION_ERROR
    }
}
