package eu.darkcode.client;

import eu.darkcode.client.hud.HudSettings;
import eu.darkcode.client.overlay.OverlaySettings;
import eu.darkcode.client.voting.integration.IntegrationSettings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientSettings {
    public OverlaySettings overlay = new OverlaySettings();
    public HudSettings hud = new HudSettings();
    public IntegrationSettings integration = new IntegrationSettings();
    public String lastShownChangelog = null;
}
