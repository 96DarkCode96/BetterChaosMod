package eu.darkcode.client.overlay;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class OverlaySettings {
    public boolean enabled = false;
    public int webServerPort = 9696;
}
