package eu.darkcode.client.voting.integration;

import eu.darkcode.client.voting.integration.kick.KickIntegrationSettings;
import eu.darkcode.client.voting.integration.twitch.TwitchIntegrationSettings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class IntegrationSettings {
    public @NotNull TwitchIntegrationSettings twitchIntegration = new TwitchIntegrationSettings();
    public @NotNull KickIntegrationSettings kickIntegration = new KickIntegrationSettings();
}
