package eu.darkcode.client.voting.integration.kick;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.darkcode.BetterChaosModClient;
import eu.darkcode.client.voting.integration.Integration;
import eu.darkcode.client.voting.integration.TokenServer;
import eu.darkcode.client.voting.integration.kick.centrifugo.CentrifugoWebSocket;
import eu.darkcode.client.voting.integration.kick.centrifugo.CentrifugoHelper;
import eu.darkcode.client.voting.integration.twitch.irc.IRCUser;
import eu.darkcode.utils.Utils;
import eu.darkcode.voting.PollDefinition;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author _dark_code_
 * @date 28.01.2026
 **/
@Environment(EnvType.CLIENT)
public class KickIntegration implements Integration {

    private final @NotNull KickIntegrationSettings settings;
    private final @NotNull CentrifugoWebSocket webSocket;

    public KickIntegration(@NotNull KickIntegrationSettings settings) {
        this.settings = settings;
        try {
            if (KickHelper.sendAsyncMessage(settings.token, "Loading world...").join() == null &&
                    !Boolean.TRUE.equals(TokenServer.refreshKickToken().join()))
                throw new IllegalStateException("Failed to connect to kick! Invalid token!");

            if (settings.token == null || settings.token.isBlank())
                throw new IllegalArgumentException("token is null or blank");

            // Find ChatRoomId if undefined (-1)
            if (settings.chatroomId == -1) {
                settings.chatroomId = KickHelper.fetchChatroomId(settings.token);
                BetterChaosModClient.instance().saveSettings();
            }

            this.webSocket = new CentrifugoWebSocket() {
                @Override
                protected @NotNull String getWebSocketUrl() {
                    return Objects.requireNonNull(KickHelper.fetchWebSocketURL(KickHelper.getClientId()));
                }

                @Override
                protected void handlePacket(@NotNull JsonObject packet) {
                    if (packet.has("push")) { // push message
                        handlePushPacket(packet.getAsJsonObject("push"));
                    }
                }

                @Override
                protected void onConnect() {
                    sendData(CentrifugoHelper.connect(Objects.requireNonNull(KickHelper.fetchJWTToken(KickHelper.getClientId())), "js") + "\n" +
                            CentrifugoHelper.subscribe(requestMessageId(), "chatrooms." + settings.chatroomId + ".v2"), true);
                }

                @Override
                protected void onDisconnected() {

                }
            };
        } catch (Exception e) {
            throw new IllegalStateException("Failed to connect to kick!", e);
        }
    }

    @Override
    public void sendPoll(@NotNull PollDefinition poll) {
        if (!settings.announceNewPolls)
            return;
        try {
            KickHelper.sendAsyncMessage(settings.token, poll.toMessage());
        } catch (Exception e) {
            BetterChaosModClient.LOGGER.warn("Failed to send poll!", e);
        }
    }

    @Override
    public void shutdown() {
        this.webSocket.shutdown();
    }

    private void handlePushPacket(@NotNull JsonObject packet) {
        // ignore invalid channel
        if (packet.has("channel") && !packet.get("channel").getAsString().equals("chatrooms." + settings.chatroomId + ".v2"))
            return;

        if (!packet.has("pub")) return;
        JsonObject pub = packet.getAsJsonObject("pub");

        if (!pub.has("data")) return;
        JsonObject data = pub.getAsJsonObject("data");

        if (!data.has("event") || !data.get("event").getAsString().equals("App\\Events\\ChatMessageEvent")) return;

        onChatMessage(Utils.GSON.fromJson(data.get("data").getAsString(), JsonObject.class));
    }

    private void onChatMessage(@NotNull JsonObject data) {
        try {
            if (!data.has("content") || !data.has("sender"))
                return;
            String content = data.get("content").getAsString().trim();
            JsonObject sender = data.getAsJsonObject("sender");
            JsonObject identity = sender.getAsJsonObject("identity");
            JsonArray badges = identity.getAsJsonArray("badges");
            boolean vip = false;
            boolean mod = false;
            boolean sub = false;
            for (JsonElement badge : badges) {
                String type = badge.getAsJsonObject().get("type").getAsString().toLowerCase();
                switch (type) {
                    case "subscriber" -> sub = true;
                    case "vip" -> vip = true;
                    case "moderator" -> mod = true;
                }
            }

            IRCUser user = new IRCUser(
                    sender.get("id").getAsLong(),
                    null,
                    sender.has("username") ? sender.get("username").getAsString() : sender.get("slug").getAsString(),
                    Integer.parseInt(identity.get("color").getAsString().substring(1), 16),
                    vip,
                    mod,
                    sub
            );

            handleMessage(user, content);
        } catch (Throwable ignored) {
            // ignored
        }
    }

}
