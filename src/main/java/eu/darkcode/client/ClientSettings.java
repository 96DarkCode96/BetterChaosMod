package eu.darkcode.client;

import eu.darkcode.client.hud.HudSettings;
import eu.darkcode.client.overlay.OverlaySettings;
import eu.darkcode.client.voting.integration.IntegrationSettings;

public class ClientSettings {
    public OverlaySettings overlay = new OverlaySettings();
    public HudSettings hud = new HudSettings();
    public IntegrationSettings integration = new IntegrationSettings();
    public String lastShownChangelog = null;
}
