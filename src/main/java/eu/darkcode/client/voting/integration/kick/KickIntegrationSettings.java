package eu.darkcode.client.voting.integration.kick;

import eu.darkcode.client.voting.integration.AbstractPlatformSettings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class KickIntegrationSettings extends AbstractPlatformSettings {
    public boolean announceNewPolls = true;
    public int chatroomId = -1;
}
