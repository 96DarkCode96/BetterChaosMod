package eu.darkcode.client.voting.integration.twitch;

import eu.darkcode.client.voting.integration.AbstractPlatformSettings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TwitchIntegrationSettings extends AbstractPlatformSettings {
    public String channel;
    public boolean announceNewPolls = true;
    public boolean ignoreSharedChat = true;
}
