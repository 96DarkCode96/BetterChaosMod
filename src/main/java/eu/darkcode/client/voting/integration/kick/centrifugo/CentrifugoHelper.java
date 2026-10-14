package eu.darkcode.client.voting.integration.kick.centrifugo;

import eu.darkcode.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author _dark_code_
 * @date 14.10.2026
 **/
public final class CentrifugoHelper {
    private CentrifugoHelper() {
    }

    @NotNull
    public static String connect(@NotNull String token, @NotNull String name) {
        return Utils.GSON.toJson(Map.of(
                "id", 1,
                "connect", Map.of(
                        "token", token,
                        "name", name
                )
        ));
    }

    @NotNull
    public static String subscribe(int commandId, @NotNull String channel) {
        return Utils.GSON.toJson(Map.of(
                "id", commandId,
                "subscribe", Map.of(
                        "channel", channel,
                        "flag", 1
                )
        ));
    }
}
