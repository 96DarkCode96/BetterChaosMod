package eu.darkcode;

import com.google.gson.JsonObject;
import eu.darkcode.utils.Utils;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public enum UpdateStatus {
    UNKNOWN, OK, OUTDATED;

    public static final @NotNull URI UPDATE_LINK = URI.create("https://bcm.matejtomecek.eu/download");

    @NotNull
    public static String getModVersion() {
        return FabricLoader.getInstance().getModContainer(Utils.MOD_ID).orElseThrow().getMetadata().getVersion().getFriendlyString();
    }

    @NotNull
    public static UpdateStatus checkVersion() {
        try {

            String versionString = getModVersion();

            try (HttpClient client = HttpClient.newHttpClient()) {
                HttpRequest request = HttpRequest.newBuilder(URI.create("https://bcm.matejtomecek.eu/outdated?currentVersion=" + URLEncoder.encode(versionString, StandardCharsets.UTF_8)))
                        .GET()
                        .timeout(Duration.ofSeconds(5))
                        .setHeader("User-Agent", "BetterChaosMod-" + FabricLoaderImpl.INSTANCE.getEnvironmentType().name())
                        .build();

                HttpResponse<String> send = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (send.statusCode() != 200)
                    return UNKNOWN;

                JsonObject object = Utils.GSON.fromJson(send.body(), JsonObject.class);
                if (object.get("status").getAsString().equals("outdated"))
                    return OUTDATED;
            }
            return OK;
        } catch (Throwable e) {
            return UNKNOWN;
        }
    }
}