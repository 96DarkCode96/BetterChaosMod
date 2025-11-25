package eu.darkcode.client.voting.integration;

import eu.darkcode.client.voting.integration.twitch.TwitchIntegrationSettings;
import org.jetbrains.annotations.NotNull;

public class IntegrationSettings {
    public boolean twitchEnabled = false;
    public @NotNull TwitchIntegrationSettings twitchIntegration = new TwitchIntegrationSettings();
}
