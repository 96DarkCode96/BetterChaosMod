package eu.darkcode;

import com.google.gson.JsonObject;
import eu.darkcode.utils.Utils;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public enum UpdateStatus {
    UNKNOWN, OK, OUTDATED;

    private static final @NotNull URI CHECK_URL = URI.create("https://store.matejtomecek.eu/api/latest/5312ceda48/");
    public static final @NotNull URI UPDATE_LINK = URI.create("https://store.matejtomecek.eu/product/5312ceda48/?ref=game&action=update");

    @NotNull
    public static UpdateStatus checkVersion() {
        try {

            ModContainer modContainer = FabricLoader.getInstance().getModContainer(Utils.MOD_ID).orElseThrow();
            String versionString = modContainer.getMetadata().getVersion().getFriendlyString();

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(CHECK_URL).GET().timeout(Duration.ofSeconds(5)).setHeader("User-Agent", "BetterChaosMod-" + FabricLoaderImpl.INSTANCE.getEnvironmentType().name()).build();

            HttpResponse<String> send = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (send.statusCode() != 200)
                return UNKNOWN;

            JsonObject object = Utils.GSON.fromJson(send.body(), JsonObject.class);
            if (object.get("status").getAsString().equals("ok")) {
                JsonObject latest = object.getAsJsonObject("latest");
                String mod_version = latest.get("mod_version").getAsString();
                String minecraft_version = latest.get("minecraft_version").getAsString();
                System.out.println("Mod version: " + mod_version + "-" + minecraft_version);
                System.out.println("Current version: " + versionString);
                if (!versionString.equals(mod_version + "-" + minecraft_version))
                    return OUTDATED;
            }

            return OK;
        } catch (Throwable e) {
            return UNKNOWN;
        }
    }
}