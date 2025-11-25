package eu.darkcode.client;

import eu.darkcode.client.hud.HudSettings;
import eu.darkcode.client.voting.integration.IntegrationSettings;

public class ClientSettings {
    public HudSettings hud = new HudSettings();
    public IntegrationSettings integration = new IntegrationSettings();
    public String lastShownChangelog = null;
}
