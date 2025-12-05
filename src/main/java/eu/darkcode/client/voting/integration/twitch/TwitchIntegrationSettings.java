package eu.darkcode.client.voting.integration.twitch;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TwitchIntegrationSettings {
    public String token;
    public String channel;
    public boolean announceNewPolls = true;
}
